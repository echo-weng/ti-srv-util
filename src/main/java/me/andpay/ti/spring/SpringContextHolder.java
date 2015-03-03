package me.andpay.ti.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Spring上下文持有类
 * 
 * @author alex
 */
public class SpringContextHolder implements ApplicationContextAware, DisposableBean {
	/**
	 * Spring上下文
	 */
	private static ApplicationContext context;

	/**
	 * 获取上下文
	 * 
	 * @return
	 */
	public static ApplicationContext getApplicationContext() {
		if (context == null) {
			throw new RuntimeException("Application context hasn't be injected yet");
		}

		return context;
	}

	/**
	 * 获取指定名称的Bean
	 * 
	 * @param name
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getBean(String name) {
		return (T) getApplicationContext().getBean(name);
	}

	/**
	 * 获取指定类型的Bean
	 * 
	 * @param requiredType
	 * @return
	 */
	public static <T> T getBean(Class<T> requiredType) {
		return getApplicationContext().getBean(requiredType);
	}

	/**
	 * 获取指定名称和指定类型的Bean
	 * 
	 * @param name
	 * @param requiredType
	 * @return
	 */
	public static <T> T getBean(String name, Class<T> requiredType) {
		return (T) getApplicationContext().getBean(name, requiredType);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		context = applicationContext;
	}

	/**
	 * {@inheritDoc}
	 */
	public void destroy() throws Exception {
		context = null;
	}
}
