package org.azkfw.walter;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.azkfw.walter.crawler.CrawlerEvent;
import org.azkfw.walter.crawler.CrawlerListener;
import org.azkfw.walter.crawler.CrawlerThread;
import org.azkfw.walter.searcher.SearcherEvent;
import org.azkfw.walter.searcher.SearcherListener;
import org.azkfw.walter.searcher.SearcherThread;

/**
 * Hello world!
 *
 */
public class App {
	public static void main(String[] args) throws Exception {

		App app = new App();
		app.start();

		while (app.isRunning()) {
			Thread.sleep(1000);
		}
	}

	private Boolean runningFlag;

	public App() {
		runningFlag = false;
	}

	private CrawlerThread crawler;
	private List<SearcherThread> searchers;
	private Queue<SearchFileInfo> files;

	public synchronized void start() {
		if (runningFlag) {
			return;
		}
		runningFlag = true;

		Thread thread = new Thread(new Runnable() {
			public void run() {
				doTask();
				runningFlag = false;
			}
		});
		thread.start();
	}
	

	private void doTask() {
		files = new LinkedList<SearchFileInfo>();
		crawler = new CrawlerThread(new File("C:\\temp"));
		crawler.addCrawlerListener(new CrawlerListener() {
			public void findFile(final File file, final CrawlerEvent event) {
				synchronized (files) {
					files.offer(new SearchFileInfo(file));
				}
			}
		});
		crawler.start();

		searchers = new ArrayList<SearcherThread>();
		for (int i = 0; i < 4; i++) {
			SearcherThread searcher = new SearcherThread();
			searchers.add(searcher);
			searcher.addSearchListener(new SearcherListener() {
				public SearchFileInfo getFile(final SearcherEvent event) {
					SearchFileInfo file = null;
					synchronized (files) {
						file = files.poll();
					}
					return file;
				}
				public void searchResult(final SearchFileInfo info, final SearcherEvent event) {
					System.out.println(info.getFile().getAbsolutePath());
				}
			});
			searcher.start();
		}

		while (crawler.isRunning() || 0 < files.size()) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
		}
		for (SearcherThread searcher : searchers) {
			searcher.stop();
		}
		while (isThreadRunning()) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
		}
		System.out.println("END");
	}
	
	private boolean isThreadRunning() {
		for (SearcherThread searcher : searchers) {
			if (searcher.isRunning()) {
				return true;
			}
		}
		return false;
	}
	

	public boolean isRunning() {
		return runningFlag;
	}

}
