package org.azkfw.walter.crawler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.azkfw.walter.AbstractThread;

public class CrawlerThread extends AbstractThread {

	private File baseDir;

	private CrawlerEvent event;
	private List<CrawlerListener> listeners;

	public CrawlerThread(final File file) {
		baseDir = file;
		event = new CrawlerEvent(this);
		listeners = new ArrayList<CrawlerListener>();
	}

	public void addCrawlerListener(final CrawlerListener listener) {
		synchronized (listeners) {
			listeners.add(listener);
		}
	}

	protected void doTask() {
		if (baseDir.isDirectory()) {
			dir(baseDir);
		} else {
			file(baseDir);
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
		}
	}

	private boolean enable(final File file) {
		if (".svn".equals(file.getName())) {
			return false;
		}
		return true;
	}
}
