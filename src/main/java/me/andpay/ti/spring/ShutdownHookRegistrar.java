package me.andpay.ti.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.AbstractApplicationContext;

/**
 * Spring容器关闭任务注册器
 * 
 * @author alex
 */
public class ShutdownHookRegistrar implements ApplicationContextAware {
	private static final Logger LOG = LoggerFactory.getLogger(ShutdownHookRegistrar.class);

	/**
	 * {@inheritDoc}
	 */
	public void setApplicationContext(ApplicationContext ctx) throws BeansException {
		if (ctx instanceof AbstractApplicationContext) {
			((AbstractApplicationContext) ctx).registerShutdownHook();

			LOG.info("Register shutdown hook with spring context");
		}
	}
}
