package org.azkfw.walter.searcher;

public class SearcherEvent {

	private SearcherThread thread;

	public SearcherEvent(final SearcherThread thread) {
		this.thread = thread;
	}

	public SearcherThread getSource() {
		return this.thread;
	}
}
