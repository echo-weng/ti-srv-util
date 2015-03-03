package me.andpay.ti.util;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 类路径扫描器类。
 * 
 * @author sea.bao
 */
public class ClassPathScanner {
	/**
	 * Recursive method used to find all classes in a given directory and
	 * subdirs.
	 * 
	 * @param directory
	 *            The base directory
	 * @param packageName
	 *            The package name for classes found inside the base directory
	 * @return The classes
	 * @throws ClassNotFoundException
	 */
	private static Set<Class<?>> findClasses(File directory, String packageName) {
		Set<Class<?>> classes = new HashSet<Class<?>>();
		if (!directory.exists()) {
			return classes;
		}
		
		File[] files = directory.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				assert !file.getName().contains(".");
				classes.addAll(findClasses(file, packageName + "." + file.getName()));
			} else if (file.getName().endsWith(".class")) {
				try {
					classes.add(Class.forName(packageName + '.'
							+ file.getName().substring(0, file.getName().length() - 6)));
				} catch (Throwable e) {
					// ignore class not found
				}
			}
		}
		return classes;
	}

	public static class ClassAnnotationsFilter implements ClassFilter {
		private Class<?>[] annotations;

		public static ClassAnnotationsFilter newFilter(Class<?>... annotations) {
			ClassAnnotationsFilter filter = new ClassAnnotationsFilter();
			filter.annotations = annotations;
			return filter;
		}

		@SuppressWarnings("unchecked")
		public boolean filter(Class<?> clazz) {
			for (Class<?> annotation : annotations) {
				if (clazz.isAnnotationPresent((Class<? extends Annotation> )annotation)) {
					return true;
				}
			}

			return false;
		}
	}

	public static class ClassInterfacesFilter implements ClassFilter {
		private Class<?>[] interfaces;

		public static ClassInterfacesFilter newFilter(Class<?>... interfaces) {
			ClassInterfacesFilter filter = new ClassInterfacesFilter();
			filter.interfaces = interfaces;
			return filter;
		}

		public boolean filter(Class<?> clazz) {
			for (Class<?> inf : interfaces) {
				if (inf.isAssignableFrom(clazz)) {
					return true;
				}
			}

			return false;
		}

	}

	public static Set<Class<?>> scanForClassesByAnnotations(String packageName,
			Class<?>... annotations) {
		return scanForClasses(packageName, ClassAnnotationsFilter.newFilter(annotations));
	}

	public static Set<Class<?>> scanForClassesByInterfaces(String packageName, Class<?>... interfaces) {
		return scanForClasses(packageName, ClassInterfacesFilter.newFilter(interfaces));
	}

	public static Set<Class<?>> scanForClasses(String packageName, ClassFilter filter) {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		assert classLoader != null;
		String path = packageName.replace('.', '/');
		Enumeration<URL> resources;
		try {
			resources = classLoader.getResources(path);
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}

		List<File> dirs = new ArrayList<File>();
		while (resources.hasMoreElements()) {
			URL resource = resources.nextElement();
			dirs.add(new File(resource.getFile()));
		}

		Set<Class<?>> classes = new HashSet<Class<?>>();
		for (File directory : dirs) {
			Set<Class<?>> subClasses = findClasses(directory, packageName);
			for (Class<?> clazz : subClasses) {
				if (filter == null || filter.filter(clazz)) {
					classes.add(clazz);
				}
			}
		}

		return classes;
	}
}
