package me.whereareiam.yueverification.common.step;

import lombok.AllArgsConstructor;
import me.whereareiam.yue.api.annotation.ComponentListener;
import me.whereareiam.yue.api.model.PayloadButton;
import me.whereareiam.yue.api.output.service.LanguageService;
import me.whereareiam.yue.api.output.service.UserProfileService;
import me.whereareiam.yue.api.style.StyleKit;
import me.whereareiam.yue.api.util.Components;
import me.whereareiam.yue.api.util.EmojiUtil;
import me.whereareiam.yue.api.util.Translatable;
import me.whereareiam.yue.api.util.Users;
import me.whereareiam.yueverification.api.VerificationStep;
import me.whereareiam.yueverification.api.model.VerificationContext;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.internal.utils.tuple.Pair;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Component
@AllArgsConstructor
@Order(Integer.MIN_VALUE)
public class WelcomeStep implements VerificationStep {
	private final LanguageService languageService;
	private final UserProfileService userProfileService;

	private final Map<Long, VerificationContext> contexts = new ConcurrentHashMap<>();

	@Override
	public CompletableFuture<Void> execute(VerificationContext context) {
		CompletableFuture<Void> future = context.start();

		Pair<MessageEmbed, List<ActionRow>> content = buildContent(context.getUserId(), false);

		context.getChannel()
				.sendMessageEmbeds(content.getLeft())
				.setComponents(content.getRight())
				.queue(message -> {
					context.setMessage(message);
					contexts.put(message.getIdLong(), context);
				});

		return future;
	}

	@ComponentListener("select_primary_language")
	private void onButtonClick(ButtonInteractionEvent event) {
		String payload = Components.payload(event);
		DiscordLocale locale = DiscordLocale.from(payload);
		if (locale == null)
			return;

		long userId = event.getUser().getIdLong();
		userProfileService.changePrimaryLanguage(userId, locale);

		Pair<MessageEmbed, List<ActionRow>> content = buildContent(userId, true);

		event.editMessageEmbeds(content.getLeft())
				.setComponents(content.getRight())
				.queue();
	}

	@ComponentListener("continue_verification")
	private void onContinueClick(ButtonInteractionEvent event) {
		VerificationContext ctx = contexts.remove(event.getMessageIdLong());
		if (ctx == null)
			return;

		ctx.next();
		event.deferEdit().queue();
	}

	private Pair<MessageEmbed, List<ActionRow>> buildContent(long userId, boolean includeContinue) {
		MessageEmbed embed = StyleKit.embeds()
				.primary()
				.setTitle(Translatable.of("plugin.yueverification.steps.welcome.title", userId))
				.setDescription(Translatable.forUser("plugin.yueverification.steps.welcome.description", userId, Users.getMention(userId)))
				.build();

		List<Button> buttons = new ArrayList<>(languageService.getAvailableLanguages()
				.stream()
				.map(lang -> Components.button(
						ButtonStyle.SECONDARY,
						"select_primary_language",
						EmojiUtil.of(lang),
						lang.getLocale()
				))
				.map(PayloadButton::getButton)
				.toList());

		if (includeContinue) {
			buttons.add(Components.button(
					ButtonStyle.SUCCESS,
					"continue_verification",
					Translatable.of("vocabulary.proceed", userId)
			));
		}

		List<ActionRow> rows = new ArrayList<>();
		for (int i = 0; i < buttons.size(); i += 5)
			rows.add(ActionRow.of(buttons.subList(i, Math.min(i + 5, buttons.size()))));

		return Pair.of(embed, rows);
	}
}