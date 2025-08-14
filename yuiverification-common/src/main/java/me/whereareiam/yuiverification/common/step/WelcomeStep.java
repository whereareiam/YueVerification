package me.whereareiam.yuiverification.common.step;

import lombok.AllArgsConstructor;
import me.whereareiam.yui.api.annotation.ComponentListener;
import me.whereareiam.yui.api.input.TemporaryChannelService;
import me.whereareiam.yui.api.model.PayloadButton;
import me.whereareiam.yui.api.model.profile.UserProfile;
import me.whereareiam.yui.api.output.service.LanguageService;
import me.whereareiam.yui.api.output.service.UserProfileService;
import me.whereareiam.yui.api.style.StyleKit;
import me.whereareiam.yui.api.util.Components;
import me.whereareiam.yui.api.util.EmojiUtil;
import me.whereareiam.yui.api.util.Translatable;
import me.whereareiam.yui.api.util.Users;
import me.whereareiam.yuiverification.api.VerificationStep;
import me.whereareiam.yuiverification.api.VerificationStepRegistry;
import me.whereareiam.yuiverification.api.model.VerificationContext;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.internal.utils.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Component
@AllArgsConstructor
@Order(Integer.MIN_VALUE)
public class WelcomeStep implements VerificationStep {
	private final LanguageService languageService;
	private final UserProfileService userProfileService;
	private final TemporaryChannelService temporaryChannelService;

	private final Map<Long, VerificationContext> contexts = new ConcurrentHashMap<>();

	private static final String STEP_PREFIX = "verification_step_welcome_";
	public static final String SELECT_PRIMARY_LISTENER = STEP_PREFIX + "select_primary";
	public static final String CONTINUE_LISTENER = STEP_PREFIX + "continue";

	@Autowired
	private void register(VerificationStepRegistry registry) {
		registry.register(this);
	}

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

	@ComponentListener(SELECT_PRIMARY_LISTENER)
	private void onButtonClick(ButtonInteractionEvent event) {
		event.deferEdit().queue((_) -> {
			String payload = Components.payload(event);
			DiscordLocale locale = DiscordLocale.from(payload);

			long userId = event.getUser().getIdLong();
			userProfileService.changePrimaryLanguage(userId, locale);

			Pair<MessageEmbed, List<ActionRow>> content = buildContent(userId, true);

			event.getHook()
					.editOriginalEmbeds(content.getLeft())
					.setComponents(content.getRight())
					.queue();
		});
	}

	@ComponentListener(CONTINUE_LISTENER)
	private void onContinueClick(ButtonInteractionEvent event) {
		event.deferEdit().queue((_) -> {
			VerificationContext ctx = contexts.remove(event.getMessageIdLong());
			if (ctx == null)
				return;

			ctx.next();
		});
	}

	private Pair<MessageEmbed, List<ActionRow>> buildContent(long userId, boolean includeContinue) {
		MessageEmbed embed = StyleKit.embeds()
				.primary()
				.setTitle(Translatable.of("plugin.yuiverification.steps.welcome.title", userId))
				.setDescription(Translatable.forUser("plugin.yuiverification.steps.welcome.description", userId, Users.getMention(userId)))
				.build();

		Optional<UserProfile> userProfile = Users.get(userId);
		if (userProfile.isEmpty())
			throw new IllegalStateException("User profile not found for user " + userId);

		List<Button> buttons = new ArrayList<>(languageService.getAvailableLanguages()
				.stream()
				.filter(lang -> !Objects.equals(userProfile.get().getPrimaryLanguage(), lang))
				.map(lang -> Components.button(
						ButtonStyle.SECONDARY,
						SELECT_PRIMARY_LISTENER,
						EmojiUtil.of(lang),
						lang.getLocale()
				))
				.map(PayloadButton::getButton)
				.toList());

		if (includeContinue) {
			buttons.add(Components.button(
					ButtonStyle.SUCCESS,
					CONTINUE_LISTENER,
					Translatable.of("vocabulary.proceed", userId)
			));
		}

		List<ActionRow> rows = new ArrayList<>();
		for (int i = 0; i < buttons.size(); i += 5)
			rows.add(ActionRow.of(buttons.subList(i, Math.min(i + 5, buttons.size()))));

		return Pair.of(embed, rows);
	}

	@Override
	public void cleanup() {
		contexts.values().forEach(ctx -> {
			if (ctx.getChannel() != null)
				temporaryChannelService.close(ctx.getChannel(), 0L);
		});
		contexts.clear();
	}
}