package org.azkfw.walter;

import java.io.File;

public class SearchFileInfo {

	private File file;
	private String encoding;
	private String data;
	
	public SearchFileInfo(final File file) {
		this.file = file;
	}
	
	public File getFile() {
		return file;
	}
	
	public void setEncoding(final String encoding) {
		this.encoding = encoding;
	}
	
	public String getEncoding() {
		return encoding;
	}
	
	public void setData(final String data) {
		this.data = data;
	}
	
	public String getData() {
		return data;
	}
}
