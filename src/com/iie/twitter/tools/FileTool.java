package com.iie.twitter.tools;

import java.io.File;

public class FileTool {
	public  static boolean isExistFile(String filename) {
		File ff = new File(filename);
		if (ff.exists()) {
			return true;
		}
		return false;
	}
}

