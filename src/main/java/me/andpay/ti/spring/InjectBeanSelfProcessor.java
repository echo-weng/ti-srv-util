package me.andpay.ti.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * 注入Bean自身(代理对象)处理器
 * 
 * @author alex
 */
public class InjectBeanSelfProcessor implements BeanPostProcessor {
	/**
	 * {@inheritDoc}
	 */
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (bean instanceof BeanSelfAware) {
			((BeanSelfAware) bean).setSelf(bean);
		}

		return bean;
	}

	/**
	 * {@inheritDoc}
	 */
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}
}
