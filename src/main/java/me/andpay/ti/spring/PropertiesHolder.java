package me.andpay.ti.spring;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * 属性保持类。
 * 
 * @author sea.bao
 */
public class PropertiesHolder {
	/**
	 * 属性名称
	 */
	private List<String> propertyNames = new ArrayList<String>();

	/**
	 * 属性值
	 */
	private List<String> propertyValues = new ArrayList<String>();

	/**
	 * 对象
	 */
	private List<Object> propertyBeans = new ArrayList<Object>();

	/**
	 * 需要引用注入的对象
	 */
	private List<Object> refBeans = new ArrayList<Object>();

	/**
	 * 注入引用的属性名称
	 */
	private List<String> refNames = new ArrayList<String>();

	/**
	 * 注入引用的值对象
	 */
	private List<Object> refValues = new ArrayList<Object>();

	/**
	 * 必须标志列表
	 */
	private List<Boolean> refRequireds = new ArrayList<Boolean>();

	/**
	 * 初始化对象
	 */
	private List<InitializingBean> waitingInitBeans = new ArrayList<InitializingBean>();
	
	/**
	 * 注销对象
	 */
	private List<DisposableBean> waitingDisposeBeans = new ArrayList<DisposableBean>();

	public void setProperty(Object bean, String propertyName, String propertyValue) {
		propertyBeans.add(bean);
		propertyNames.add(propertyName);
		propertyValues.add(propertyValue);
	}

	public void setRef(Object bean, String propertyName, Object propertyRef) {
		setRef(bean, propertyName, propertyRef, true);
	}

	public void setRef(Object bean, String propertyName, Object propertyRef, boolean required) {
		if (propertyRef != null) {
			if (!(propertyRef instanceof String) && !(propertyRef instanceof Class)) {
				throw new RuntimeException("PropertyRef must be String or Class.");
			}
		}

		refBeans.add(bean);
		refNames.add(propertyName);
		refValues.add(propertyRef);
		refRequireds.add(required);
	}

	public void addInitBean(InitializingBean bean) {
		waitingInitBeans.add(bean);
	}

	public List<InitializingBean> getWaitingInitBeans() {
		return waitingInitBeans;
	}

	public List<String> getPropertyNames() {
		return propertyNames;
	}

	public List<String> getPropertyValues() {
		return propertyValues;
	}

	public List<Object> getPropertyBeans() {
		return propertyBeans;
	}

	public List<Object> getBeans() {
		return propertyBeans;
	}

	public void setPropertyBeans(List<Object> propertyBeans) {
		this.propertyBeans = propertyBeans;
	}

	public List<Object> getRefBeans() {
		return refBeans;
	}

	public void setRefBeans(List<Object> refBeans) {
		this.refBeans = refBeans;
	}

	public List<String> getRefNames() {
		return refNames;
	}

	public void setRefNames(List<String> refNames) {
		this.refNames = refNames;
	}

	public List<Object> getRefValues() {
		return refValues;
	}

	public void setRefValues(List<Object> refValues) {
		this.refValues = refValues;
	}

	public void setPropertyNames(List<String> propertyNames) {
		this.propertyNames = propertyNames;
	}

	public void setPropertyValues(List<String> propertyValues) {
		this.propertyValues = propertyValues;
	}

	public void setWaitingInitBeans(List<InitializingBean> waitingInitBeans) {
		this.waitingInitBeans = waitingInitBeans;
	}

	public List<Boolean> getRefRequireds() {
		return refRequireds;
	}

	public void setRefRequireds(List<Boolean> refRequireds) {
		this.refRequireds = refRequireds;
	}

	public List<DisposableBean> getWaitingDisposeBeans() {
		return waitingDisposeBeans;
	}
	
	public void addDisposeBean(DisposableBean bean) {
		this.waitingDisposeBeans.add(bean);
	}

	public void setWaitingDisposeBeans(List<DisposableBean> waitingDisposableBeans) {
		this.waitingDisposeBeans = waitingDisposableBeans;
	}

}
