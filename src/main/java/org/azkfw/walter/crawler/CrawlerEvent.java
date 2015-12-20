package org.azkfw.walter.crawler;

public class CrawlerEvent {

	private CrawlerThread thread;

	public CrawlerEvent(final CrawlerThread thread) {
		this.thread = thread;
	}

	public CrawlerThread getSource() {
		return this.thread;
	}
}
