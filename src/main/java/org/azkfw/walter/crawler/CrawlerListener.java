package org.azkfw.walter.crawler;

import java.io.File;

public interface CrawlerListener {

	public void findFile(final File file, final CrawlerEvent event);
}
