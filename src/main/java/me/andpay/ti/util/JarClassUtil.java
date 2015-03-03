package me.andpay.ti.util;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Jar类工具类。
 * 
 * @author sea.bao
 */
public class JarClassUtil {
	public static Set<Class<?>> loadClassesFromJars(String[] jars) throws IOException {
		URL[] urls = new URL[jars.length];
		for (int i = 0; i < jars.length; i++) {
			urls[i] = new URL("jar:file:" + jars[i] + "!/");
		}

		ClassLoader cl = URLClassLoader.newInstance(urls, Thread.currentThread().getContextClassLoader());
		Set<Class<?>> classes = new HashSet<Class<?>>();
		for (String jar : jars) {
			JarFile jarFile = new JarFile(jar);
			try {
				Enumeration<JarEntry> e = jarFile.entries();

				while (e.hasMoreElements()) {
					JarEntry je = e.nextElement();
					if (je.isDirectory() || !je.getName().endsWith(".class")) {
						continue;
					}
					// -6 because of .class
					String className = je.getName().substring(0, je.getName().length() - 6);
					className = className.replace('/', '.');
					Class<?> c;
					try {
						c = cl.loadClass(className);
					} catch (Throwable e1) {
						// ignore class not found.
						continue;
					}

					classes.add(c);
				}
			} finally {
				try {
					jarFile.close();
				} catch (Exception ex) {
				}
			}
		}

		return classes;
	}
}
