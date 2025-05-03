package me.whereareiam.yueverification;

import lombok.AllArgsConstructor;
import me.whereareiam.yue.api.output.plugin.YuePlugin;
import me.whereareiam.yueverification.api.service.VerificationService;
import org.springframework.context.ApplicationContext;

@AllArgsConstructor
public class YueVerification implements YuePlugin {
	private final ApplicationContext ctx;

	@Override
	public void onEnable() {
		ctx.getBean(VerificationService.class).verify(1025715153410465803L);
	}
}
