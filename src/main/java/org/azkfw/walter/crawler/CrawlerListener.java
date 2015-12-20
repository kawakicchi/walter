package org.azkfw.walter.crawler;

import java.io.File;

public interface CrawlerListener {

	public void findFile(final File file, final CrawlerEvent event);
	
	public void findIncludeFile(final File file, final CrawlerEvent event);
	
	public void findExcludeFile(final File file, final CrawlerEvent event);
}
