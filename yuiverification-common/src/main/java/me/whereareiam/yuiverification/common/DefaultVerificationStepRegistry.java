package me.whereareiam.yuiverification.common;

import me.whereareiam.yui.api.input.Registry;
import me.whereareiam.yuiverification.api.VerificationStep;
import me.whereareiam.yuiverification.api.VerificationStepRegistry;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class DefaultVerificationStepRegistry implements VerificationStepRegistry, Registry<VerificationStep> {
	private final CopyOnWriteArrayList<VerificationStep> steps = new CopyOnWriteArrayList<>();
	private static final Comparator<VerificationStep> ORDERING = Comparator.comparingInt(DefaultVerificationStepRegistry::resolveOrder);

	@Autowired
	public DefaultVerificationStepRegistry(ApplicationContext ctx) {
		ApplicationContext parent = ctx.getParent();
		if (parent instanceof ConfigurableApplicationContext cac)
			if (BeanFactoryUtils.beanNamesForTypeIncludingAncestors(cac, VerificationStepRegistry.class).length == 0)
				cac.getBeanFactory().registerSingleton("defaultVerificationStepRegistry", this);
	}

	@Override
	public void register(VerificationStep step) {
		steps.add(step);
		steps.sort(ORDERING);
	}

	@Override
	public List<VerificationStep> getSteps() {
		return List.copyOf(steps);
	}

	private static int resolveOrder(VerificationStep step) {
		Order order = AnnotationUtils.findAnnotation(step.getClass(), Order.class);
		if (order != null) return order.value();
		return step instanceof Ordered o ? o.getOrder() : Ordered.LOWEST_PRECEDENCE;
	}

}
