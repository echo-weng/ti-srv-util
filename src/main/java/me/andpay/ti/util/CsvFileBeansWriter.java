package me.andpay.ti.util;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.jxpath.JXPathContext;

/**
 * Csv文件对象写入器类。
 * 
 * @author sea.bao
 */
public class CsvFileBeansWriter {
	/**
	 * 缺省分隔符
	 */
	public static final String DEFAULT_DELIMITER = "|";

	/**
	 * 分隔符的转义串
	 */
	public static final String DELIMITER_ESCAPE = "&deli;";

	/**
	 * 属性名称
	 */
	private List<String> fields = new ArrayList<String>();

	/**
	 * 属性格式化
	 */
	private Map<String, String> fieldFormats = new HashMap<String, String>();

	/**
	 * 文件写入器
	 */
	private PrintWriter out;

	/**
	 * 分隔符
	 */
	private String delimiter = DEFAULT_DELIMITER;

	/**
	 * 写文件头标志
	 */
	private boolean writeHeaderFlag = false;

	public CsvFileBeansWriter(PrintWriter out) {
		this.out = out;
	}

	public CsvFileBeansWriter(PrintWriter out, String delimiter) {
		this.out = out;
		this.delimiter = delimiter;
	}

	public CsvFileBeansWriter(File file, String charsetName) {
		this(file, charsetName, DEFAULT_DELIMITER);
	}

	public CsvFileBeansWriter(File file, String charsetName, String delimiter) {
		try {
			this.out = new PrintWriter(file, charsetName);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}

		this.delimiter = delimiter;
	}

	protected void writeHeader() {
		StringBuffer sb = new StringBuffer();
		for (String field : fields) {
			if (sb.length() > 0) {
				sb.append(delimiter);
			}

			sb.append(field);
			String f = fieldFormats.get(field);
			if (f != null) {
				sb.append("=");
				sb.append(f);
			}
		}

		out.println(sb.toString());
	}

	protected String escapeDelimiter(String value) {
		return value.replace(delimiter, DELIMITER_ESCAPE);
	}

	public void writeBean(Object bean) {
		if (writeHeaderFlag == false) {
			writeHeaderFlag = true;
			writeHeader();
		}

		StringBuffer fmt = new StringBuffer();
		Object[] values = new Object[fields.size()];
		JXPathContext xpath = JXPathContext.newContext(bean);
		for (int i = 0; i < fields.size(); i++) {
			String field = fields.get(i);

			if (fmt.length() > 0) {
				fmt.append(delimiter);
			}

			Object value = xpath.getValue(field);
			if (value == null) {
				fmt.append("%s");
				values[i] = "";
			} else {
				if (value instanceof String) {
					value = escapeDelimiter((String) value);
				}

				String f = fieldFormats.get(field);
				if (f == null) {
					fmt.append("%s");
					if (value instanceof java.util.Date) {
						values[i] = StringUtil.format("yyyy-MM-dd HH:mm:ss.SSS", (java.util.Date) value);
					} else {
						values[i] = value.toString();
					}
				} else {
					if (f.startsWith("%")) {
						fmt.append(f);
						values[i] = value;
					} else {
						fmt.append("%s");
						if (value instanceof java.util.Date) {
							values[i] = StringUtil.format(f, (java.util.Date) value);
						} else {
							values[i] = value.toString();
						}
					}
				}
			}
		}

		fmt.append("\n");
		out.printf(fmt.toString(), values);
	}

	public void addAllFields(Class<?> clazz) {
		BeanInfo beanInfo;
		try {
			beanInfo = java.beans.Introspector.getBeanInfo(clazz);
		} catch (IntrospectionException e) {
			throw new RuntimeException(e.getMessage(), e);
		}

		for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
			if (pd.getName().equals("class")) {
				continue;
			}

			fields.add(pd.getName());
		}
	}

	public void addField(String field, String fmt) {
		fields.add(field);
		fieldFormats.put(field, fmt);
	}

	public void addField(String field) {
		fields.add(field);
	}

	public void removeField(String field) {
		fields.remove(field);
		fieldFormats.remove(field);
	}

	public PrintWriter getOut() {
		return out;
	}

	public void setOut(PrintWriter out) {
		this.out = out;
	}

	public void close() {
		CloseUtil.close(out);
	}
}
