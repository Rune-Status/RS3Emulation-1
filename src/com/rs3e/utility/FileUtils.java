package com.rs3e.utility;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * @author Taylor
 * @since Sep 15, 2013, Earth 1.0
 */
public class FileUtils {

	/**
	 * Returns an array of classes in a specified package
	 * 
	 * @param packageName
	 *            The package name to get the classes of
	 * @return An array of classes in the package
	 */
	@SuppressWarnings({ "rawtypes" })
	public static Class[] getClasses(String packageName) {
		try {
			ClassLoader classLoader = Thread.currentThread()
					.getContextClassLoader();
			assert classLoader != null;
			String path = packageName.replace('.', '/');
			Enumeration<URL> resources = classLoader.getResources(path);
			List<File> dirs = new ArrayList<File>();
			while (resources.hasMoreElements()) {
				URL resource = resources.nextElement();
				dirs.add(new File(resource.getFile().replaceAll("%20", " ")));
			}
			ArrayList<Class> classes = new ArrayList<Class>();
			for (File directory : dirs) {
				classes.addAll(findClasses(directory, packageName));
			}
			return classes.toArray(new Class[classes.size()]);
		} catch (Exception e) {

		}
		return null;
	}

	/**
	 * Finds a list of classes in a specified directory
	 * 
	 * @param directory
	 *            The directory to look in
	 * @param packageName
	 *            The name of the package that contains the classes
	 * @return The {@code List} of classes
	 */
	@SuppressWarnings("rawtypes")
	private static List<Class> findClasses(File directory, String packageName) {
		List<Class> classes = new ArrayList<Class>();
		if (!directory.exists()) {
			return classes;
		}
		File[] files = directory.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				assert !file.getName().contains(".");
				classes.addAll(findClasses(file,
						packageName + "." + file.getName()));
			} else if (file.getName().endsWith(".class")) {
				try {
					classes.add(Class.forName(packageName
							+ '.'
							+ file.getName().substring(0,
									file.getName().length() - 6)));
				} catch (Throwable e) {

				}
			}
		}
		return classes;
	}
}
