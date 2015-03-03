package me.andpay.ti.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringEscapeUtils;

/**
 * 简单属性文件解析器抽象类
 * 
 * <pre>
 * 文件格式:
 * ## [scope@]category1
 * # comment
 * category1.name1=value1
 * category1.name2=value2
 * 
 * ## [scope@]category2
 * category2.name1=value1\
 * value2
 * 
 * 解析结果:
 * propHolder1 : ( scope, category1, { name1:value1, name2:value2 } )
 * propHolder2 : ( scope, category2, { name1:value1value2 } )
 * </pre>
 * 
 * @author alex
 */
public abstract class SimplePropertyParser<T> {
	/**
	 * 新建属性数据持有对象
	 * 
	 * @param scope
	 * @param category
	 * @return
	 */
	protected abstract T newPropertyHolder(String scope, String category);

	/**
	 * 添加属性值到持有对象
	 * 
	 * @param propHolder
	 * @param name
	 * @param value
	 */
	protected abstract void addProperty(T propHolder, String name, String value);

	/**
	 * 获得默认范围
	 * 
	 * @return
	 */
	protected String getDefaultScope() {
		return null;
	}

	/**
	 * 从数据流中加载属性文件数据并组装成属性对象集合
	 * 
	 * @param in
	 * @return
	 * @throws IOException
	 */
	public List<T> parse(InputStream in) throws IOException {
		// [scope - [category - propHolder]]
		Map<String, Map<String, T>> scopeMap = parseAsMap(in);

		List<T> propHolers = new ArrayList<T>();
		for (Map<String, T> cateMap : scopeMap.values()) {
			propHolers.addAll(cateMap.values());
		}

		return propHolers;
	}

	/**
	 * 从数据流中加载属性文件数据并组装成属性对象键值对
	 * 
	 * @param in
	 * @return
	 * @throws IOException
	 */
	protected Map<String, Map<String, T>> parseAsMap(InputStream in) throws IOException {
		List<String> lines = readLines(in);

		// [scope - [category - propHolder]]
		Map<String, Map<String, T>> scopeMap = new LinkedHashMap<String, Map<String, T>>();

		String scope = null, category = null;
		int lineSize = lines.size();
		for (int i = 0; i < lineSize; i++) {
			String line = lines.get(i).trim();
			if (line.length() == 0) {
				continue;
			}

			SimpleStringBuffer buff = null;
			if (line.startsWith("#")) {
				// #(comment) or ##(category definition)
				buff = new SimpleStringBuffer(line);

				if ("##".equals(buff.tryRead(2))) {
					// ## scope@category
					scope = unescape(buff.find("@", true));
					if (scope == null) {
						scope = getDefaultScope();
					}

					category = unescape(buff.readRemaining());
				}

				continue;
			}

			// support multi-line (end with \)
			while (line.matches("(.*[^\\\\]\\\\|\\\\)")) {
				line = line.substring(0, line.length() - 1);
				if (i == lineSize - 1) {
					break; // last line
				}

				line += lines.get(++i).trim();
			}

			if (category == null) {
				throw new RuntimeException(String.format("No category defined, line=[%d][%s]", i + 1, line));
			}

			buff = new SimpleStringBuffer(line);

			if (StringUtil.startsWith(buff.tryRead(category.length() + 1), category) == false) {
				throw new RuntimeException(String.format(
						"Missmatching category or invalid content, scope=%s, category=%s, line=[%d][%s]", scope,
						category, i + 1, buff));
			}

			// category.name=value
			String name = unescape(buff.find("=", true));
			if (name == null) {
				throw new RuntimeException(String.format("Invalid content, scope=%s, category=%s, line=[%d][%s]",
						scope, category, i + 1, buff));
			}

			String value = unescape(buff.readRemaining());

			Map<String, T> cateMap = scopeMap.get(scope);
			if (cateMap == null) {
				cateMap = new LinkedHashMap<String, T>();
				scopeMap.put(scope, cateMap);
			}

			T propHolder = cateMap.get(category);
			if (propHolder == null) {
				propHolder = newPropertyHolder(scope, category);

				cateMap.put(category, propHolder);
			}

			addProperty(propHolder, name, value);
		}

		return scopeMap;
	}

	/**
	 * 从输入流中读取数据，按行转换为数据集合
	 * 
	 * @param in
	 * @return
	 * @throws IOException
	 */
	protected List<String> readLines(InputStream in) throws IOException {
		return IOUtil.readLines(in);
	}

	/**
	 * 解码Unicode字符串
	 * 
	 * @param str
	 * @return
	 */
	protected String unescape(String str) {
		if (str == null) {
			return null;
		}

		str = str.trim();
		if (str.length() == 0) {
			return null;
		}

		return StringEscapeUtils.unescapeJava(str);
	}
}
