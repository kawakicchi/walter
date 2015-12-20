package org.azkfw.walter.searcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.azkfw.walter.AbstractThread;
import org.azkfw.walter.SearchFileInfo;
import org.azkfw.walter.SearchOption;
import org.azkfw.walter.util.DetectUtils;

public class SearcherThread extends AbstractThread {

	private SearchOption option;
	
	private SearcherEvent event;
	private List<SearcherListener> listeners;

	private Queue<SearchFileInfo> files;

	public SearcherThread(final SearchOption option) {
		this.option = option;
		
		event = new SearcherEvent(this);
		listeners = new ArrayList<SearcherListener>();
		files = new LinkedList<SearchFileInfo>();
	}

	public void addSearchListener(final SearcherListener listener) {
		synchronized (listeners) {
			listeners.add(listener);
		}
	}

	public void addSearchFile(final SearchFileInfo file) {
		synchronized (files) {
			files.offer(file);
		}
	}

	@Override
	protected void doTask() {

		while (!isStop() || 0 < files.size()) {
			SearchFileInfo file = null;
			synchronized (files) {
				file = files.poll();
			}
			if (null == file) {
				synchronized (files) {
					synchronized (listeners) {
						for (SearcherListener listener : listeners) {
							SearchFileInfo info = listener.getFile(event);
							if (null != info) {
								files.offer(info);
							}
						}
					}
				}
				if (0 == files.size()) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException ex) {
						ex.printStackTrace();
					}
				}
			} else {
				search(file);
			}
		}
	}

	private void search(final SearchFileInfo file) {
		try {
			String encoding = DetectUtils.getEncoding(file.getFile());
			if (null == encoding) {
				encoding = System.getProperty("file.encoding");
			}
			
			file.setEncoding(encoding);
			
			String data = readString(file.getFile(), encoding);
			data = data.replaceAll("\r\n", "\n");
			file.setData(data);
			
			
			Pattern ptn = Pattern.compile(option.getKeyword());
			Matcher m = ptn.matcher(data);
			
			SearchResult result = new SearchResult(file);
			while (m.find()) {
				result.addPosition(m.start(), m.end());
			}
			
			synchronized (listeners) {
				for (SearcherListener listener : listeners) {
					listener.searchResult(result, event);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private String readString(final File file, final String encoding) {
		String string = null;
		InputStreamReader reader = null;
		try {
			StringBuffer str = new StringBuffer();
			reader = new InputStreamReader(new FileInputStream(file), encoding);

			int size = -1;
			char[] buffer = new char[1024];
			while (-1 != (size = reader.read(buffer, 0, 1024))) {
				str.append(buffer, 0, size);
			}

			string = str.toString();
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (null != reader) {
				try {
					reader.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}
		return string;
	}
}
