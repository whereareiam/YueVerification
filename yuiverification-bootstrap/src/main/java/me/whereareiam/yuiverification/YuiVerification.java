package me.whereareiam.yuiverification;

import lombok.AllArgsConstructor;
import me.whereareiam.yui.plugin.YuiPlugin;
import me.whereareiam.yuiverification.common.DefaultVerificationService;
import me.whereareiam.yuiverification.model.config.VerificationSettings;
import org.springframework.context.ApplicationContext;

@AllArgsConstructor
public class YuiVerification implements YuiPlugin {
	private final ApplicationContext ctx;

	@Override
	public void onEnable() {
		if (ctx.getBean(VerificationSettings.class).getTriggers().isOnStartup())
			ctx.getBean(VerificationService.class).verify();
	}

	@Override
	public void onDisable() {
		try {
			VerificationService service = ctx.getBean(VerificationService.class);
			if (service instanceof DefaultVerificationService verificationService)
				verificationService.cancelAllVerifications();

		} catch (Exception ignored) {
			// Swallow to avoid lifecycle disruption if context already shutting down
		}
	}
}
