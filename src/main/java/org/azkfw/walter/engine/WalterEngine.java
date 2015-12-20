package org.azkfw.walter.engine;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.azkfw.walter.SearchFileInfo;
import org.azkfw.walter.SearchOption;
import org.azkfw.walter.crawler.CrawlerEvent;
import org.azkfw.walter.crawler.CrawlerListener;
import org.azkfw.walter.crawler.CrawlerThread;
import org.azkfw.walter.searcher.SearchResult;
import org.azkfw.walter.searcher.SearcherEvent;
import org.azkfw.walter.searcher.SearcherListener;
import org.azkfw.walter.searcher.SearcherThread;

/**
 *
 */
public final class WalterEngine {

	private SearchOption option;

	private int threadSize = 4;
	
	private BasicWalterEngineState state;
	
	private WalterEngineEvent event;
	private List<WalterEngineListener> listeners;

	private Boolean runningFlag;

	private CrawlerThread crawler;
	private List<SearcherThread> searchers;
	private Queue<SearchFileInfo> files;

	private long startTime;
	private long endTime;

	public WalterEngine(final SearchOption option) {
		this.option = option;

		state = new BasicWalterEngineState();
		
		event = new WalterEngineEvent(this);
		listeners = new ArrayList<WalterEngineListener>();

		runningFlag = false;
	}

	public void addWalterEngineListener(final WalterEngineListener listener) {
		synchronized (listeners) {
			listeners.add(listener);
		}
	}

	public synchronized void start() {
		if (runningFlag) {
			return;
		}
		runningFlag = true;

		state.reset();
		startTime = System.nanoTime();

		Thread thread = new Thread(new Runnable() {
			public void run() {
				doTask();
				runningFlag = false;
			}
		});
		thread.start();
	}

	private void doTask() {
		synchronized (listeners) {
			for (WalterEngineListener listener : listeners) {
				listener.start(event);
			}
		}
		
		files = new LinkedList<SearchFileInfo>();
		crawler = new CrawlerThread(option);
		crawler.addCrawlerListener(new CrawlerListener() {
			@Override
			public void findFile(final File file, final CrawlerEvent event) {
				state.countupTotalFile();
			}
			@Override
			public void findIncludeFile(final File file, final CrawlerEvent event) {
				state.countupCrawleFile();
				synchronized (files) {
					files.offer(new SearchFileInfo(file));
				}
			}
			@Override
			public void findExcludeFile(final File file, final CrawlerEvent event) {
			}
		});
		crawler.start();

		searchers = new ArrayList<SearcherThread>();
		for (int i = 0; i < threadSize; i++) {
			SearcherThread searcher = new SearcherThread(option);
			searchers.add(searcher);
			searcher.addSearchListener(new SearcherListener() {
				public SearchFileInfo getFile(final SearcherEvent event) {
					SearchFileInfo file = null;
					synchronized (files) {
						file = files.poll();
					}
					return file;
				}

				public void searchResult(final SearchResult result, final SearcherEvent e) {
					if (result.isFind()) {
						synchronized (listeners) {
							for (WalterEngineListener listener : listeners) {
								listener.findFile(result, event);
							}
						}
					} else {
						
					}
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

		
		synchronized (listeners) {
			for (WalterEngineListener listener : listeners) {
				listener.end(event);
			}
		}
		endTime = System.nanoTime();
		System.out.println(String.format("%.2f s", (double) (endTime - startTime) / 1000000000f));
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
