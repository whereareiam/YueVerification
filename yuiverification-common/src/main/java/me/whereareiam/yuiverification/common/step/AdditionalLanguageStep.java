package me.whereareiam.yuiverification.common.step;

import lombok.AllArgsConstructor;
import me.whereareiam.yui.api.annotation.ComponentListener;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
@Order(Integer.MIN_VALUE + 1)
public class AdditionalLanguageStep implements VerificationStep {
	private final LanguageService languageService;
	private final UserProfileService userProfileService;

	private final Map<Long, VerificationContext> contexts = new ConcurrentHashMap<>();

	@Autowired
	private void register(VerificationStepRegistry registry) {
		registry.register(this);
	}

	@Override
	public CompletableFuture<Void> execute(VerificationContext context) {
		CompletableFuture<Void> future = context.start();

		Pair<MessageEmbed, List<ActionRow>> content = buildContent(context.getUserId());

		context.getMessage()
				.editMessageEmbeds(content.getLeft())
				.setComponents(content.getRight())
				.queue(message -> {
					context.setMessage(message);
					contexts.put(message.getIdLong(), context);
				});

		return future;
	}

	@ComponentListener("add_additional_language")
	private void onAdditionalLanguageClick(ButtonInteractionEvent event) {
		String payload = Components.payload(event);
		DiscordLocale locale = DiscordLocale.from(payload);
		if (locale == null)
			throw new IllegalStateException("Invalid locale: " + payload);

		long userId = event.getUser().getIdLong();
		userProfileService.addAdditionalLanguage(userId, locale);

		Pair<MessageEmbed, List<ActionRow>> content = buildContent(userId);

		event.editMessageEmbeds(content.getLeft())
				.setComponents(content.getRight())
				.queue();
	}

	@ComponentListener("continue_verification_additional")
	private void onContinueClick(ButtonInteractionEvent event) {
		VerificationContext ctx = contexts.remove(event.getMessageIdLong());
		if (ctx == null)
			return;

		ctx.next();
		event.deferEdit().queue();
	}

	private Pair<MessageEmbed, List<ActionRow>> buildContent(long userId) {
		MessageEmbed embed = StyleKit.embeds()
				.primary()
				.setTitle(Translatable.of("plugin.yuiverification.steps.additionalLanguage.title", userId))
				.setDescription(Translatable.forUser("plugin.yuiverification.steps.additionalLanguage.description", userId, Users.getMention(userId)))
				.build();

		Optional<UserProfile> userProfile = Users.get(userId);
		if (userProfile.isEmpty())
			throw new IllegalStateException("User profile not found for user " + userId);

		DiscordLocale primary = userProfile.get().getPrimaryLanguage();
		List<DiscordLocale> alreadySelected = List.of(userProfile.get().getAdditionalLanguages());

		List<Button> languageButtons = languageService.getAvailableLanguages().stream()
				.filter(lang -> !lang.equals(primary) && !alreadySelected.contains(lang))
				.map(lang -> Components.button(
								ButtonStyle.SECONDARY,
								"add_additional_language",
								EmojiUtil.of(lang),
								lang.getLocale())
						.getButton())
				.collect(Collectors.toCollection(ArrayList::new));

		languageButtons.add(Components.button(
				ButtonStyle.SUCCESS,
				"continue_verification_additional",
				Translatable.of("vocabulary.proceed", userId)
		));

		List<ActionRow> rows = new ArrayList<>();
		for (int i = 0; i < languageButtons.size(); i += 5)
			rows.add(ActionRow.of(languageButtons.subList(i, Math.min(i + 5, languageButtons.size()))));

		return Pair.of(embed, rows);
	}
}