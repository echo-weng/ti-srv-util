package me.andpay.ti.spring;

/**
 * Spring注入Bean自身接口
 * 
 * @author alex.yang
 */
public interface BeanSelfAware {
	/**
	 * 注入Bean
	 * 
	 * @param self
	 *            Bean对象
	 */
	public void setSelf(Object self);
}
