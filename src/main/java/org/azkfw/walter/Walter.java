/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.azkfw.walter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.azkfw.walter.component.WalterFrame;

/**
 * @author Kawakicchi
 *
 */
public final class Walter {

	public static void main(String[] args) throws Exception {
		Walter.getInstance().load(new File("setting.properties"));
		
		WalterFrame frame = new WalterFrame();
		frame.setVisible(true);
	}

	private static final Walter INSTANCE = new Walter();

	private File propertyFile;
	private String keyword;
	private String targetDirectory;
	
	private Walter() {
		keyword = "";
		targetDirectory = "";
	}
	
	public static final Walter getInstance() {
		return INSTANCE;
	}
	
	public static String getKeyword() {
		return INSTANCE.keyword;
	}
	
	public void setKeyword(final String keyword) {
		this.keyword = keyword;
	}
	
	public static String getTargetDirectory() {
		return INSTANCE.targetDirectory;
	}
	
	public void setTargetDirectory(final String directory) {
		targetDirectory = directory;
	}
	
	public void load(final File file) {
		propertyFile = file;
		InputStream stream = null;
		try {
			stream = new FileInputStream(file);
			Properties p = new Properties();
			p.load(stream);
			
			keyword = p.getProperty("searchOption.keyword", "");
			targetDirectory = p.getProperty("searchOption.targetDirectory", "");
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (null != stream) {
				try {
				stream.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	public void save() {
		save(propertyFile);
	}
	public void save(final File file) {
		OutputStream stream = null;
		try {
			stream = new FileOutputStream(file);
			Properties p = new Properties();
			
			p.put("searchOption.keyword", keyword);
			p.put("searchOption.targetDirectory", targetDirectory);
			
			p.store(stream, "Walter");
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (null != stream) {
				try {
				stream.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}
	}
}
