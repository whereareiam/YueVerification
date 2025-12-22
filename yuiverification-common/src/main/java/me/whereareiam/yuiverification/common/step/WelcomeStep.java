package me.whereareiam.yuiverification.common.step;

import lombok.AllArgsConstructor;
import me.whereareiam.yui.annotation.ComponentListener;
import me.whereareiam.yui.model.PayloadButton;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Component
@AllArgsConstructor
@Order(Integer.MIN_VALUE)
public class WelcomeStep implements VerificationStep {
	private final LanguagePersistence languagePersistence;

	private final Map<Long, VerificationContext> contexts = new ConcurrentHashMap<>();

	private static final String STEP_PREFIX = "verification_step_welcome_";
	public static final String SELECT_PRIMARY_LISTENER = STEP_PREFIX + "select_primary";
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
		List<ActionRow> rows = buildActionRows(fluctlight, false);

		context.getConversation().getChannel()
				.sendMessageEmbeds(embed.build())
				.setComponents(rows)
				.queue(message -> {
					context.setMessage(message);
					contexts.put(message.getIdLong(), context);
				});

		return future;
	}

	@ComponentListener(SELECT_PRIMARY_LISTENER)
	public void onButtonClick(Fluctlight fluctlight, ButtonInteractionEvent event) {
		String payload = Components.payload(event);
		if (payload == null || payload.isBlank()) {
			event.deferEdit().queue();
			return;
		}

		DiscordLocale locale = DiscordLocale.from(payload);
		fluctlight.setPrimaryLanguage(locale);

		EmbedBuilder embed = buildEmbed(fluctlight);
		List<ActionRow> rows = buildActionRows(fluctlight, true);

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
				.setTitle(Translatable.text("plugin.yuiverification.steps.welcome.title").resolve(fluctlight))
				.setDescription(Translatable.text("plugin.yuiverification.steps.welcome.description")
						.with("mention", fluctlight.getAsMention())
						.resolve(fluctlight));
	}

	private List<ActionRow> buildActionRows(Fluctlight fluctlight, boolean includeContinue) {
		DiscordLocale currentPrimary = fluctlight.getPrimaryLanguage();

		List<Button> buttons = new ArrayList<>(languagePersistence.getAvailableLanguages()
				.stream()
				.filter(Objects::nonNull)
				.filter(lang -> !Objects.equals(currentPrimary, lang))
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
					Translatable.text("vocabulary.proceed").resolve(fluctlight)
			));
		}

		List<ActionRow> rows = new ArrayList<>();
		for (int i = 0; i < buttons.size(); i += 5)
			rows.add(ActionRow.of(buttons.subList(i, Math.min(i + 5, buttons.size()))));

		return rows;
	}

	@Override
	public void onVerificationCancelled(VerificationContext context) {
		contexts.values().removeIf(ctx -> ctx.getFluctlight().getId() == context.getFluctlight().getId());
	}
}