package org.azkfw.walter.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.mozilla.universalchardet.UniversalDetector;

public class DetectUtils {
	/*
	public String readFileToString(File file) throws IOException {
		String encode = getEncoding(file);
		if (encode == null) {
			encode = defaultEncoding;
		}
		String text = FileUtils.readFileToString(file, encode);
		if (text.charAt(0) == 65279) {// UTF-8 marker
			text = text.substring(1);
		}
		return text;
	}
	*/

	public static String getEncoding(File file) {
		FileInputStream stream = null;
		try {
			stream = new FileInputStream(file);
			byte[] buf = new byte[4096];
			UniversalDetector detector = new UniversalDetector(null);

			detector.reset();
			int size;
			while (0 < (size = stream.read(buf)) && !detector.isDone()) {
				detector.handleData(buf, 0, size);
			}
			detector.dataEnd();

			return detector.getDetectedCharset();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != stream) {
				try {
					stream.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}
		return null;
	}
}
