package ninja.mspp.core.util;

import java.io.File;

public class FileUtil {
	public static void delete(File file) {
		if(file.exists()) {
			if(file.isDirectory()) {
				File[] children = file.listFiles();
				for(File child : children) {
					delete(child);
				}
			}
			file.delete();
		}
	}
}
