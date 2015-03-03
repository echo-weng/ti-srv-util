package me.andpay.ti.util;

/**
 * String模板类。
 * 
 * @author sea.bao
 */
public class StringTemplate {
	private StringBuilder template;

	public StringTemplate(String template) {
		this.template = new StringBuilder(template);
	}

	public void replace(String placeholder, String value) {
		if (value == null) {
			value = "";
		}

		int idx = template.indexOf(placeholder);
		template.replace(idx, idx + placeholder.length(), value);
	}

	public void appendAfter(String placeholder, String value) {
		if (value == null) {
			value = "";
		}

		int idx = template.indexOf(placeholder);
		template.insert(idx, value);
	}

	public void append(String value) {
		if (value == null) {
			value = "";
		}

		template.append(value);
	}

	public String getString() {
		return template.toString();
	}
}
