package me.andpay.ti.xls.impl;

import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import me.andpay.ti.util.CollectionUtil;
import me.andpay.ti.util.DateUtil;
import me.andpay.ti.util.StringUtil;
import me.andpay.ti.xls.XlsWriter;
import me.andpay.ti.xls.exception.ExcelException;
import me.andpay.ti.xls.helper.XlsHelper;
import me.andpay.ti.xls.model.Cell;
import me.andpay.ti.xls.model.Font;
import me.andpay.ti.xls.model.Part;
import me.andpay.ti.xls.model.Row;
import me.andpay.ti.xls.model.Xls;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public abstract class AbstractXlsWriter implements XlsWriter, ApplicationContextAware{
	
	private static final String DATA_TYPE_NUMBER = "number";
	private static final String CONVERT_TYPE_OBJECT = "object";
	private static final String CONVERT_TYPE_BEAN = "bean";
	private static final String CONVERT_TYPE_STATIC = "static";
	private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

	/**
	 * 模板缓存，节省解析文件的时间
	 */
	private Map<String, Xls> cacheTempleMap = new WeakHashMap<String, Xls>();
	
	/**
	 * 方法缓存
	 */
	private Map<String, Method> methodsMap = new WeakHashMap<String, Method>();
	
	/**
	 * 对象缓存
	 */
	private Map<String, Object> objectMap = new WeakHashMap<String, Object>();
	

	protected ApplicationContext applicationContext;
	
	/**
	 * 根据获取xls模板路径 生成xls
	 * 
	 * @param templePath
	 * @return
	 * @throws ExcelException
	 */
	protected Xls getXls(String templePath) throws ExcelException{
		Xls xls = cacheTempleMap.get(templePath);
		if(xls != null){
			return xls;
		}
		
		// 根据模板路径 获取 模板
		try {
			String temple = XlsHelper.getTemple(templePath);
			xls = XlsHelper.parse(temple);
		} catch (Exception e) {
			throw new ExcelException(e);
		}
		// 转化成poi的样式结构
		xls = XlsHelper.convertTo(xls);
		
		cacheTempleMap.put(templePath, xls);
		
		return xls;
	}
	
	/**
	 * 返回border
	 * 
	 * @param cell
	 * @return
	 */
	protected String[] getBorders(Cell cell){
		String borderStr = cell.getCellStyle().getBorder();
		if(StringUtil.isEmpty(borderStr)){
			return null;
		}
		
		String[] borders = StringUtil.split(borderStr, ",");
		if(borders.length != 4){
			throw new ExcelException("Border is illegal");
		}
		
		return borders;
	}
	
	/**
	 * 给每个part 设值, 主要是给每个cell 的表达式填充值
	 * 
	 * @param part
	 * @param value
	 */
	protected void setPartValue(Part part, Object value){
		List<Row> rows = part.getRows();
		if(CollectionUtil.isEmpty(rows)){
			return;
		}
		
		check(part, value);
		
		if(isList(value)){
			List<?> list = (List<?>) value;
			setRowValue(part, list);
			
			return;
		}
		
		for(Row row : rows){
			setRowValue(row, value);
		}
	}
	
	protected void setRowValue(Part part, List<?> listValue){
		if(CollectionUtil.isEmpty(listValue)) return;
		
		Row row = part.getRows().get(0);
		List<Row> newRows = new ArrayList<Row>(listValue.size());
		for(Object value : listValue){
			Row newRow = row.clone();
			//设值
			setRowValue(newRow, value);
			
			newRows.add(newRow);
		}
		part.setRows(newRows);
	}
	
	/**
	 * 给每row 填充值
	 * 
	 * @param row
	 * @param value
	 */
	protected void setRowValue(Row row, Object value){
		List<Cell> cells = row.getCells();
		
		if(CollectionUtil.isEmpty(cells)){
			return;
		}
		
		for(Cell cell : cells){
			setCellValue(cell, value);
		}
	}
	
	/**
	 * 给每cell 填充值
	 * 
	 * @param cell
	 * @param valueObject
	 */
	protected void setCellValue(Cell cell, Object valueObject){
		String originValue = cell.getExpValue();
		
		if(originValue == null || !originValue.matches("^\\$\\{(\\w)*}$")){
			setSpecialValue(cell);
			
			return;
		}
		
		String fieldName = originValue.replaceAll("\\$\\{|}", "");
		String newValue = getValue(valueObject, fieldName, cell);
		cell.setValue(newValue);
	}
	
	/**
	 * 设置特殊的值， 例如Date.now()，如果带有换行符的 则强制换行
	 * 
	 * @param cell
	 */
	protected void setSpecialValue(Cell cell){
		String originValue = cell.getExpValue();
		if(StringUtil.isEmpty(originValue)) return;
		
		cell.setValue(originValue);
		
		if("Date.now()".equals(originValue)){
			cell.setValue(DateUtil.format(getFormate(cell, DEFAULT_DATE_FORMAT), new Date()));
			
			return;
		}
		
		if(originValue.indexOf("\\n") >= 0){
			originValue = originValue.replace("\\n", "\n");
			if(cell.getFont() == null){
				cell.setFont(new Font());
			}
			
			cell.getFont().setWrapText(true);
			cell.setValue(originValue);
		}
		
		if(originValue.indexOf("\\t") >= 0){
			originValue = originValue.replace("\\t", "\t");
			cell.setValue(originValue);
		}
	}
	
	/**
	 * 从bean对象value中 获取字段为fieldName的值， 如果有转换器或者 formate 则进行相应转换
	 * 
	 * @param value
	 * @param fieldName
	 * @param cell
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	protected String getValue(Object value, String fieldName, Cell cell) {
		if(value == null){//如果传入的值为空，则用表达式的值进行替代
			return cell.getExpValue();
		}
		if(isMap(value)){
			Object newValue = ((Map)value).get(fieldName);
			
			if(newValue == null) return "";
			return getValue(newValue, cell);
		}
		
		try {
			//先从缓存中获取
			Method method = methodsMap.get(getKey(value.getClass().getName(), fieldName));
			if(method == null){
				PropertyDescriptor descriptor = new PropertyDescriptor(fieldName, value.getClass());
				method = descriptor.getReadMethod();
				//放入缓存
				methodsMap.put(getKey(value.getClass().getName(), fieldName), method);
			}
			
			if(method != null){
				Object newValue = invoke(method, value, null);
				
				if(newValue == null) return "";
				return getValue(newValue, cell);
			}
		} catch (Exception e) {
		}
		
		return "";
	}
	
	protected String getValue(Object newValue, Cell cell){
		if(newValue == null) return "";
		//转化value
		newValue = getConvertValue(newValue, cell);
		
		if(isDate(newValue)){
			return getDateValue((Date)newValue, cell);
		}
		
		if(isNumber(newValue)){
			cell.setType(DATA_TYPE_NUMBER);
		}
		
		//如果type为 number 的 则进行formatNumber
		if(DATA_TYPE_NUMBER.equals(cell.getType())){
			return newValue.toString();
		}
		
		//如果不进行格式化 则直接tostring 返回
		if(StringUtil.isEmpty(cell.getFormate())){
			return newValue.toString();
		}
		
		return StringUtil.formatString(cell.getFormate(), newValue);
	}
	
	protected String getFormate(Cell cell, String defaultFormate){
		if(!StringUtil.isEmpty(cell.getFormate())){
			return cell.getFormate();
		}
		
		return defaultFormate;
	}
	
	/**
	 * 获取转化后的值
	 * 
	 * @param newValue
	 * @param cell
	 * @return
	 */
	protected Object getConvertValue(Object newValue, Cell cell){
		if(StringUtil.isEmpty(cell.getConvert())){
			return newValue;
		}
		
		String convert = cell.getConvert();
		String convertType = StringUtil.subString(convert, 0, convert.indexOf("#"));
		String objectName = StringUtil.subString(convert, convert.indexOf("#") + 1, convert.lastIndexOf("."));
		String methodName = StringUtil.subString(convert, convert.lastIndexOf(".") + 1);
		
		//检查convertType
		checkConvertType(convertType);
		
		Method method = methodsMap.get(getKey(convertType, convertType, methodName));
		Object object = objectMap.get(getKey(convertType, objectName));
		if(method != null){
			return invoke(method, object, newValue);
		}
		
		Object result = null;
		
		if(CONVERT_TYPE_BEAN.equals(convertType)){
			object = applicationContext.getBean(objectName);
			if(object == null) throw new ExcelException("Can not fund bean=[" + objectName + "] in spring container");
		}
		if(CONVERT_TYPE_STATIC.equals(convertType)){
			object = getClass(objectName);
		}
		if(CONVERT_TYPE_OBJECT.equals(convertType)){
			object = newInstance(getClass(objectName));
		}
		if(!StringUtil.isEmpty(convertType)){
			method = getMethod(object, methodName, newValue);
			result = invoke(method, object, newValue);
			methodsMap.put(getKey(convertType, convertType, methodName), method);
			objectMap.put(getKey(convertType, objectName), object);
		}
		
		if(result != null){
			return result;
		}
		
		return newValue;
	}
	
	private void checkConvertType(String convertType){
		if(!CONVERT_TYPE_BEAN.equals(convertType) && !CONVERT_TYPE_STATIC.equals(convertType) && !CONVERT_TYPE_OBJECT.equals(convertType)){
			throw new ExcelException("ConvertType is illegal");
		}
	}
	
	protected Object newInstance(Class<?> cls){
		try {
			return cls.newInstance();
		} catch (InstantiationException e) {
		} catch (IllegalAccessException e) {
		}
		
		return null;
	}
	
	protected Object invoke(Method method, Object object, Object arg){
		if(object == null) return null;
		
		try {
			if(arg != null){
				return method.invoke(object, arg);
			}
			return method.invoke(object);
		} catch (IllegalArgumentException e) {
		} catch (IllegalAccessException e) {
		} catch (InvocationTargetException e) {
		}
		
		return null;
	}
	
	protected Class<?> getClass(String className){
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw new ExcelException("Class not found for className=[" + className + "]");
		}
	}
	
	protected Method getMethod(Object bean, String methodName, Object arg) {
		try {
			Class<?> cls = bean.getClass();
			if(bean instanceof Class){
				cls = (Class<?>) bean;
			}
			
			return cls.getMethod(methodName, arg.getClass());
		} catch (SecurityException e) {
			throw new ExcelException("Can not get method for bean=[" + bean.getClass().getName() + "], methodName=[" + methodName + "], arg=[" + arg.getClass().getName() + "]");
		} catch (NoSuchMethodException e) {
			throw new ExcelException("Can not get method for bean=[" + bean.getClass().getName() + "], methodName=[" + methodName + "], arg=[" + arg.getClass().getName() + "]");
		}
	}
	
	protected String getDateValue(Date newValue, Cell cell){
		String formate = DEFAULT_DATE_FORMAT;
		if(!StringUtil.isEmpty(cell.getFormate())){
			formate = cell.getFormate();
		}
		
		return DateUtil.format(formate, newValue);
	}
	
	protected boolean isDate(Object value){
		return value instanceof Date;
	}
	
	protected boolean isNumber(Object value){
		return Number.class.isAssignableFrom(value.getClass());
	}
	
	protected boolean isList(Object value){
		return value instanceof List;
	}
	
	protected boolean isMap(Object value){
		return value instanceof Map;
	}
	
	protected void check(Part part, Object value){
		if(part.getRows().size() > 1 && value instanceof List){
			throw new ExcelException("Part rows and value object can not all List");
		}
	}
	
	protected String getKey(String ... keys){
		if(keys == null) return "";
		
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < keys.length; i++) {
			sb.append(keys[i]);
			if(i != keys.length - 1){
				sb.append("-");
			}
		}
		
		return sb.toString();
	}
	
	protected int[] parseIndex(String index){
		if(index.indexOf(",") < 0) return new int[]{Integer.valueOf(index)};
		
		String[] multIndexs = index.split(",");
		if(multIndexs.length > 2) throw new ExcelException("Cell index more than two");
		
		return new int[]{Integer.valueOf(multIndexs[0]), Integer.valueOf(multIndexs[1])};
	}
	
	protected File getNewFile(String filePath){
		File file = new File(filePath);
		if(file.exists()){
			file.delete();
		}
		try {
			file.createNewFile();
		} catch (IOException e) {
		}
		return file;
	}
	
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
	
}
