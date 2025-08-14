package me.whereareiam.yuiverification;

import lombok.AllArgsConstructor;
import me.whereareiam.yui.api.output.plugin.YuiPlugin;
import me.whereareiam.yuiverification.api.VerificationStep;
import me.whereareiam.yuiverification.api.VerificationStepRegistry;
import me.whereareiam.yuiverification.api.model.config.VerificationSettings;
import me.whereareiam.yuiverification.api.service.VerificationService;
import org.springframework.context.ApplicationContext;

@AllArgsConstructor
public class YuiVerification implements YuiPlugin {
	private final ApplicationContext ctx;

	@Override
	public void onEnable() {
		if (ctx.getBean(VerificationSettings.class).isScanOnStartup())
			ctx.getBean(VerificationService.class).verify();
	}

	@Override
	public void onDisable() {
		try {
			VerificationStepRegistry registry = ctx.getBean(VerificationStepRegistry.class);
			for (VerificationStep step : registry.getSteps())
				step.cleanup();
		} catch (Exception ignored) {
			// Swallow to avoid lifecycle disruption if context already shutting down
		}
	}
}
