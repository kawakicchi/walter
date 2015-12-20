package org.azkfw.walter.searcher;

import org.azkfw.walter.SearchFileInfo;


public interface SearcherListener {

	public SearchFileInfo getFile(final SearcherEvent event);
	
	public void searchResult(final SearchResult result, final SearcherEvent event);
	
}
