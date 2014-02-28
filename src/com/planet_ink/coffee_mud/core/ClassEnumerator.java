package com.planet_ink.coffee_mud.core;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ClassEnumerator {

	private static Class<?> loadClass(String className) {
		className = className.replace('/', '.');
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(
					"Unexpected ClassNotFoundException loading class '"
							+ className + "'");
		}
	}

	private static void processDirectory(File directory, String pkgname,
			Collection<Class<?>> classes, Class<?> limitor) {
		for (String fileName : directory.list()) {
			String className = null;
			if (fileName.endsWith(".class")) {
				className = pkgname
						+ fileName.substring(0, fileName.length() - 6);
			}

			if (className != null && !fileName.contains("$")) {
				Class<?> klass = loadClass(className);
				if (limitor.isAssignableFrom(klass)) {
					classes.add(klass);
				}

			}
		}
	}

	private static void processJarfile(URL resource, String pkgname,
			Collection<Class<?>> classes) {
		String relPath = pkgname.replace('.', '/');
		String resPath = resource.getPath();
		String jarPath = resPath.replaceFirst("[.]jar[!].*", ".jar")
				.replaceFirst("file:", "");
		JarFile jarFile = null;
		try {
			jarFile = new JarFile(jarPath);
		} catch (IOException e) {
			throw new RuntimeException(
					"Unexpected IOException reading JAR File '" + jarPath + "'",
					e);
		} finally {
			try {
				if (jarFile != null) {
					jarFile.close();
				}
			} catch (Exception e) {
				// swallow
			}
		}
		Enumeration<JarEntry> entries = jarFile.entries();
		while (entries.hasMoreElements()) {
			JarEntry entry = entries.nextElement();
			String entryName = entry.getName();
			String className = null;
			if (entryName.endsWith(".class") && entryName.startsWith(relPath)
					&& entryName.length() > (relPath.length() + "/".length())) {
				className = entryName.replace('/', '.').replace('\\', '.')
						.replace(".class", "");
			}
			if (className != null) {
				classes.add(loadClass(className));
			}
		}
	}

	public static void getClassesForPackage(String pkgname,
			Collection<Class<?>> classes, Class<?> limitor) {
		String relPath = pkgname.replace('.', '/');
		URL resource = ClassLoader.getSystemClassLoader().getResource(relPath);
		if (resource == null) {
			throw new RuntimeException("Unexpected problem: No resource for "
					+ relPath);
		}

		resource.getPath();
		if (resource.toString().startsWith("jar:")) {
			processJarfile(resource, pkgname, classes);
		} else {
			processDirectory(new File(resource.getPath()), pkgname, classes,
					limitor);
		}
	}
}