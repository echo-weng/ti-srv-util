package me.andpay.ti.util;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Csv格式文件Bean读取器类。
 * 
 * @author sea.bao
 */
public class CsvFileBeansReader {
	/**
	 * 缺省分隔符
	 */
	public static final String DEFAULT_DELIMITER = "|";

	/**
	 * 分隔符的转义串
	 */
	public static final String DELIMITER_ESCAPE = "&deli;";

	private Log logger = LogFactory.getLog(getClass());

	/**
	 * 属性名称
	 */
	private List<String> fields = new ArrayList<String>();

	/**
	 * 属性格式化
	 */
	private Map<String, String> fieldFormats = new HashMap<String, String>();

	/**
	 * 文件读取器
	 */
	private LineNumberReader in;

	/**
	 * 分隔符
	 */
	private String delimiter = DEFAULT_DELIMITER;

	/**
	 * 写文件头标志
	 */
	private boolean readHeaderFlag = false;

	/**
	 * Bean的类
	 */
	private Class<?> clazz;

	/**
	 * 日期属性集
	 */
	private Set<String> dateProperties = new HashSet<String>();

	protected void init(Class<?> clazz, LineNumberReader in, String delimiter) {
		this.in = in;
		this.delimiter = delimiter;
		this.clazz = clazz;

		BeanInfo beanInfo;
		try {
			beanInfo = java.beans.Introspector.getBeanInfo(clazz);
		} catch (IntrospectionException e) {
			throw new RuntimeException(e.getMessage(), e);
		}

		for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
			if (pd.getPropertyType().equals(java.util.Date.class)) {
				dateProperties.add(pd.getName());
			}
		}
	}

	public <T> CsvFileBeansReader(Class<?> clazz, LineNumberReader in, String delimiter) {
		init(clazz, in, delimiter);
	}

	public CsvFileBeansReader(Class<?> clazz, LineNumberReader in) {
		init(clazz, in, DEFAULT_DELIMITER);
	}

	public <T> CsvFileBeansReader(Class<?> clazz, File file, String charsetName, String delimiter) {
		try {
			LineNumberReader in = new LineNumberReader(new InputStreamReader(new FileInputStream(file), charsetName));
			init(clazz, in, delimiter);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public CsvFileBeansReader(Class<?> clazz, File file, String charsetName) {
		this(clazz, file, charsetName, DEFAULT_DELIMITER);
	}

	protected String[] readElements(String line) {
		List<String> elements = new ArrayList<String>();
		StringTokenizer st = new StringTokenizer(line, delimiter, true);
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			if (token.equals(delimiter)) {
				elements.add(null);
			} else {
				elements.add(StringUtil.emptyAsNull(token));
				if (st.hasMoreTokens()) {
					st.nextToken();
					if (st.hasMoreTokens() == false) {
						elements.add(null);
					}
				}
			}
		}

		return ArrayUtil.toArray(elements, String.class);
	}

	protected String unescapeDelimiter(String value) {
		return value.replace(DELIMITER_ESCAPE, delimiter);
	}

	protected Object readBeanImpl() throws Exception {
		if (readHeaderFlag == false) {
			readHeaderFlag = true;
			String header = in.readLine();
			String[] headerEles = readElements(header);
			for (String headerEle : headerEles) {
				int idx = headerEle.indexOf("=");
				String field, fieldFmt;
				if (idx > 0) {
					field = headerEle.substring(0, idx);
					fieldFmt = headerEle.substring(idx + 1);
				} else {
					field = headerEle;
					fieldFmt = null;
				}

				fields.add(field);
				if (fieldFmt != null) {
					fieldFormats.put(field, fieldFmt);
				}
			}
		}

		String line = in.readLine();
		if (line == null) {
			return null;
		}

		String[] eles = readElements(line);
		Object bean = clazz.newInstance();
		JXPathContext xpath = JXPathContext.newContext(bean);
		for (int i = 0; i < fields.size(); i++) {
			String field = fields.get(i);
			Object value = eles[i];

			if (value != null) {
				value = ((String) value).trim();
				String fieldFmt = fieldFormats.get(field);

				if (dateProperties.contains(field)) {
					// 日期属性
					if (fieldFmt != null) {
						value = DateUtil.parse(fieldFmt, (String) value);
					} else {
						value = DateUtil.parse("yyyy-MM-dd HH:mm:ss.SSS", (String) value);
					}
				} else {
					value = unescapeDelimiter((String) value);
				}
			}

			try {
				xpath.setValue(field, value);
			} catch (Exception e) {
				// 忽略xpath设值错误
				logger.warn("Ignore the xpath set error.", e);
			}
		}

		return bean;
	}

	public Object readBean() {
		try {
			return readBeanImpl();
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	public void close() {
		CloseUtil.close(in);
	}
}
