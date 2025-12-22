package me.whereareiam.yuiverification.common.step;

import lombok.AllArgsConstructor;
import me.whereareiam.yui.annotation.ComponentListener;
import me.whereareiam.yui.model.fluctlight.Fluctlight;
import me.whereareiam.yui.persistence.LanguagePersistence;
import me.whereareiam.yui.util.Components;
import me.whereareiam.yui.util.EmojiUtil;
import me.whereareiam.yui.util.style.StyleKit;
import me.whereareiam.yui.util.translation.Translatable;
import me.whereareiam.yuiverification.VerificationStep;
import me.whereareiam.yuiverification.VerificationStepRegistry;
import me.whereareiam.yuiverification.model.VerificationContext;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Component
@AllArgsConstructor
@Order(Integer.MIN_VALUE + 1)
public class AdditionalLanguageStep implements VerificationStep {
	private final LanguagePersistence languagePersistence;

	private final Map<Long, VerificationContext> contexts = new ConcurrentHashMap<>();

	private static final String STEP_PREFIX = "verification_step_additionallanguages_";
	public static final String ADD_LANGUAGE_LISTENER = STEP_PREFIX + "add";
	public static final String CONTINUE_LISTENER = STEP_PREFIX + "continue";

	@Autowired
	private void register(VerificationStepRegistry registry) {
		registry.register(this);
	}

	@Override
	public CompletableFuture<Void> onStepStarted(VerificationContext context) {
		CompletableFuture<Void> future = context.start();

		Fluctlight fluctlight = context.getFluctlight();
		EmbedBuilder embed = buildEmbed(fluctlight);
		List<ActionRow> rows = buildActionRows(fluctlight);

		context.getMessage()
				.editMessageEmbeds(embed.build())
				.setComponents(rows)
				.queue(message -> {
					context.setMessage(message);
					contexts.put(message.getIdLong(), context);
				});

		return future;
	}

	@ComponentListener(ADD_LANGUAGE_LISTENER)
	public void onAdditionalLanguageClick(Fluctlight fluctlight, ButtonInteractionEvent event) {
		String payload = Components.payload(event);
		if (payload == null || payload.isBlank()) {
			event.deferEdit().queue();
			return;
		}

		DiscordLocale locale = DiscordLocale.from(payload);
		fluctlight.addAdditionalLanguage(locale);

		EmbedBuilder embed = buildEmbed(fluctlight);
		List<ActionRow> rows = buildActionRows(fluctlight);

		event.editMessageEmbeds(embed.build())
				.setComponents(rows)
				.queue();
	}

	@ComponentListener(CONTINUE_LISTENER)
	public void onContinueClick(ButtonInteractionEvent event) {
		event.deferEdit().queue(_ -> {
			VerificationContext ctx = contexts.remove(event.getMessageIdLong());
			if (ctx == null)
				return;

			ctx.next();
		});
	}

	@Override
	public void onStepCompleted(VerificationContext context) {
		contexts.values().removeIf(ctx -> ctx.getFluctlight().getId() == context.getFluctlight().getId());
	}

	private EmbedBuilder buildEmbed(Fluctlight fluctlight) {
		return StyleKit.embeds()
				.primary()
				.setTitle(Translatable.text("plugin.yuiverification.steps.additionalLanguage.title").resolve(fluctlight))
				.setDescription(Translatable.text("plugin.yuiverification.steps.additionalLanguage.description")
						.with("mention", fluctlight.getAsMention())
						.resolve(fluctlight));
	}

	private List<ActionRow> buildActionRows(Fluctlight fluctlight) {
		DiscordLocale primary = fluctlight.getPrimaryLanguage();
		Set<DiscordLocale> alreadySelected = new HashSet<>();
		if (fluctlight.getAdditionalLanguages() != null) {
			alreadySelected.addAll(Arrays.asList(fluctlight.getAdditionalLanguages()));
		}

		List<Button> languageButtons = languagePersistence.getAvailableLanguages().stream()
				.filter(Objects::nonNull)
				.filter(lang -> !lang.equals(primary) && !alreadySelected.contains(lang))
				.map(lang -> Components.button(
								ButtonStyle.SECONDARY,
								ADD_LANGUAGE_LISTENER,
								EmojiUtil.of(lang),
								lang.getLocale())
						.getButton())
				.toList();

		List<Button> allButtons = new ArrayList<>(languageButtons);
		allButtons.add(Components.button(
				ButtonStyle.SUCCESS,
				CONTINUE_LISTENER,
				Translatable.text("vocabulary.proceed").resolve(fluctlight)
		));

		List<ActionRow> rows = new ArrayList<>();
		for (int i = 0; i < allButtons.size(); i += 5) {
			rows.add(ActionRow.of(allButtons.subList(i, Math.min(i + 5, allButtons.size()))));
		}

		return rows;
	}

	@Override
	public void onVerificationCancelled(VerificationContext context) {
		contexts.values().removeIf(ctx -> ctx.getFluctlight().getId() == context.getFluctlight().getId());
	}
}