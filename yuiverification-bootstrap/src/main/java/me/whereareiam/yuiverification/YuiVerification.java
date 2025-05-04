package me.whereareiam.yuiverification;

import lombok.AllArgsConstructor;
import me.whereareiam.yui.api.output.plugin.YuiPlugin;
import me.whereareiam.yuiverification.api.service.VerificationService;
import org.springframework.context.ApplicationContext;

@AllArgsConstructor
public class YuiVerification implements YuiPlugin {
	private final ApplicationContext ctx;

	@Override
	public void onEnable() {
		ctx.getBean(VerificationService.class).verify(1025715153410465803L);
	}
}
