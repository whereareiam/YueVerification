package me.whereareiam.yuiverification.common.journey;

import lombok.AllArgsConstructor;
import me.whereareiam.yui.annotation.ComponentListener;
import me.whereareiam.yui.annotation.journey.JourneyStep;
import me.whereareiam.yui.journey.JourneyService;
import me.whereareiam.yui.journey.definition.group.JourneyStepDefinition;
import me.whereareiam.yui.model.journey.JourneyInstruction;
import me.whereareiam.yui.model.journey.JourneySignal;
import me.whereareiam.yui.model.journey.JourneyStepContext;
import me.whereareiam.yui.model.PayloadButton;
import me.whereareiam.yui.model.component.ComponentAttributes;
import me.whereareiam.yui.model.config.languages.LanguageEntry;
import me.whereareiam.yui.model.config.languages.Languages;
import me.whereareiam.yui.model.fluctlight.Fluctlight;
import me.whereareiam.yui.persistence.LanguagePersistence;
import me.whereareiam.yui.journey.JourneyKeys;
import me.whereareiam.yui.util.Components;
import me.whereareiam.yui.util.style.StyleKit;
import me.whereareiam.yui.util.translation.Translatable;
import me.whereareiam.yuiverification.VerificationAttributes;
import me.whereareiam.yuiverification.model.VerificationState;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
@AllArgsConstructor
@JourneyStep(journeyId = "verification", stepId = "welcome", order = 10)
public class WelcomeStep implements JourneyStepDefinition<VerificationState> {
	private final LanguagePersistence languagePersistence;
	private final ObjectProvider<Languages> languagesProvider;
	private final JourneyService journeyService;

	private static final String STEP_PREFIX = "verification_step_welcome_";
	public static final String SELECT_PRIMARY_LISTENER = STEP_PREFIX + "select_primary";
	public static final String CONTINUE_LISTENER = STEP_PREFIX + "continue";

	@Override
	public @NotNull JourneyInstruction onEnter(JourneyStepContext<VerificationState> context) {
		VerificationState state = context.state();
		Fluctlight fluctlight = state.getFluctlight();
		ComponentAttributes sessionAttributes = JourneyKeys.forSession(context.getSession().getId());

		state.getConversation().getChannel()
				.sendMessageEmbeds(buildEmbed(fluctlight).build())
				.setComponents(buildActionRows(fluctlight, false, sessionAttributes))
				.queue(state::setMessage);

		return JourneyInstruction.waitForSignal();
	}

	@Override
	public @NotNull JourneyInstruction onSignal(JourneyStepContext<VerificationState> context, JourneySignal signal) {
		VerificationState state = context.state();
		Fluctlight fluctlight = state.getFluctlight();
		ComponentAttributes sessionAttributes = JourneyKeys.forSession(context.getSession().getId());

		switch (signal.getType()) {
			case "select_primary" -> {
				String payload = signal.attribute(VerificationAttributes.LOCALE).orElse(null);
				if (payload == null || payload.isBlank())
					return JourneyInstruction.waitForSignal();

				DiscordLocale locale = DiscordLocale.from(payload);
				fluctlight.setPrimaryLanguage(locale);

				if (state.getMessage() != null)
					state.getMessage().editMessageEmbeds(buildEmbed(fluctlight).build())
							.setComponents(buildActionRows(fluctlight, true, sessionAttributes))
							.queue(state::setMessage, _ -> {
							});

				return JourneyInstruction.waitForSignal();
			}
			case "continue" -> {
				return JourneyInstruction.next();
			}
			default -> {
				return JourneyInstruction.waitForSignal();
			}
		}
	}

	@ComponentListener(SELECT_PRIMARY_LISTENER)
	public void onSelectPrimary(ButtonInteractionEvent event) {
		String payload = Components.payload(event);
		if (payload == null || payload.isBlank()) {
			event.deferEdit().queue();
			return;
		}

		String sessionId = Components.attributes(event).get(JourneyKeys.SESSION_ID).orElse(null);
		if (sessionId == null) {
			event.deferEdit().queue();
			return;
		}

		event.deferEdit().queue();
		journeyService.signal(
				sessionId,
				VerificationState.class,
				JourneySignal.builder("select_primary")
						.attribute(VerificationAttributes.LOCALE, payload)
						.actorId(event.getUser().getIdLong())
						.build()
		);
	}

	@ComponentListener(CONTINUE_LISTENER)
	public void onContinue(ButtonInteractionEvent event) {
		String sessionId = Components.attributes(event).get(JourneyKeys.SESSION_ID).orElse(null);
		if (sessionId == null) {
			event.deferEdit().queue();
			return;
		}

		event.deferEdit().queue();
		journeyService.signal(sessionId, VerificationState.class, JourneySignal.of("continue"));
	}

	private EmbedBuilder buildEmbed(Fluctlight fluctlight) {
		return StyleKit.embeds()
				.primary()
				.setTitle(Translatable.text("plugin.yuiverification.steps.welcome.title").resolve(fluctlight))
				.setDescription(Translatable.text("plugin.yuiverification.steps.welcome.description")
						.with("mention", fluctlight.getAsMention())
						.resolve(fluctlight));
	}

	private List<ActionRow> buildActionRows(
			Fluctlight fluctlight,
			boolean includeContinue,
			ComponentAttributes sessionAttributes
	) {
		DiscordLocale currentPrimary = fluctlight.getPrimaryLanguage();
		Map<DiscordLocale, LanguageEntry> languageConfig = languagesProvider.getObject().toLocaleMap();

		List<Button> buttons = new ArrayList<>(languagePersistence.getAvailableLanguages()
				.stream()
				.filter(Objects::nonNull)
				.filter(lang -> !Objects.equals(currentPrimary, lang))
				.map(lang -> buildLanguageButton(lang, languageConfig, sessionAttributes))
				.map(PayloadButton::getButton)
				.toList());

		if (includeContinue)
			buttons.add(Components.button(
					ButtonStyle.SUCCESS,
					CONTINUE_LISTENER,
					Translatable.text("vocabulary.proceed").resolve(fluctlight),
					sessionAttributes
			));

		List<ActionRow> rows = new ArrayList<>();
		for (int i = 0; i < buttons.size(); i += 5)
			rows.add(ActionRow.of(buttons.subList(i, Math.min(i + 5, buttons.size()))));

		return rows;
	}

	private PayloadButton buildLanguageButton(
			DiscordLocale lang,
			Map<DiscordLocale, LanguageEntry> languageConfig,
			ComponentAttributes sessionAttributes
	) {
		LanguageEntry entry = languageConfig.get(lang);
		String emoji = entry != null ? entry.getEmoji() : null;
		String displayName = entry != null ? entry.getDisplayName() : null;

		if (emoji != null && !emoji.isBlank()) {
			try {
				return Components.button(
						ButtonStyle.SECONDARY,
						SELECT_PRIMARY_LISTENER,
						Emoji.fromFormatted(emoji),
						lang.getLocale(),
						sessionAttributes
				);
			} catch (IllegalArgumentException ignored) {
				// Fall back to label when emoji is invalid.
			}
		}

		String label = (displayName != null && !displayName.isBlank()) ? displayName : fallbackLabel(lang);
		return Components.button(
				ButtonStyle.SECONDARY,
				SELECT_PRIMARY_LISTENER,
				label,
				lang.getLocale(),
				sessionAttributes
		);
	}

	private String fallbackLabel(DiscordLocale locale) {
		String nativeName = locale.getNativeName();
		if (!nativeName.isBlank()) return nativeName;

		return locale.getLocale();
	}
}
