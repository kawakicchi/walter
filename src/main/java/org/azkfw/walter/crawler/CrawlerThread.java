package org.azkfw.walter.crawler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.azkfw.walter.AbstractThread;
import org.azkfw.walter.SearchOption;

public class CrawlerThread extends AbstractThread {

	private SearchOption option;

	private CrawlerEvent event;
	private List<CrawlerListener> listeners;

	public CrawlerThread(final SearchOption option) {
		this.option = option;

		event = new CrawlerEvent(this);
		listeners = new ArrayList<CrawlerListener>();
	}

	public void addCrawlerListener(final CrawlerListener listener) {
		synchronized (listeners) {
			listeners.add(listener);
		}
	}

	protected void doTask() {
		File dir = new File(option.getTargetDirectory());
		if (dir.isDirectory()) {
			dir(dir);
		} else {
			file(dir);
		}
	}

	private void dir(final File dir) {
		File[] files = dir.listFiles();
		for (File file : files) {
			if (!enable(file)) {
				continue;
			}
			if (file.isDirectory()) {
				dir(file);
			} else if (file.isFile()) {
				file(file);
			}
		}
	}

	private void file(final File file) {
		synchronized (listeners) {
			for (CrawlerListener listener : listeners) {
				listener.findFile(file, event);
			}
			// TODO:
			if (true) {
				for (CrawlerListener listener : listeners) {
					listener.findIncludeFile(file, event);
				}
			} else {
				for (CrawlerListener listener : listeners) {
					listener.findExcludeFile(file, event);
				}				
			}
		}
	}

	private boolean enable(final File file) {
		if (file.getName().startsWith(".")) {
			return false;
		}
		return true;
	}
}
