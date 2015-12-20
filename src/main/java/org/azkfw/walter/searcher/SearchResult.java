/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.azkfw.walter.searcher;

import java.util.ArrayList;
import java.util.List;

import org.azkfw.walter.SearchFileInfo;


/**
 * @author Kawakicchi
 *
 */
public class SearchResult {

	private SearchFileInfo fileInfo;
	private List<SearchPosition> positions;
	
	public SearchResult(final SearchFileInfo fileInfo) {
		this.fileInfo = fileInfo;
		
		positions = new ArrayList<SearchResult.SearchPosition>();
	}
	
	public boolean isFind() {
		return (0 < positions.size());
	}
	
	public SearchFileInfo getFileInfo() {
		return fileInfo;
	}
	
	public void addPosition(final int start, final int end) {
		positions.add(new SearchPosition(start, end));
	}
	
	public List<SearchPosition> getPositions() {
		return positions;
	}
	
	public String toString() {
		return String.format("%s (%d matches %s)", fileInfo.getFile().getName(), positions.size(), fileInfo.getEncoding());
	}
	
	public static class SearchPosition {
		private int start;
		private int end;

		public SearchPosition(final int start, final int end) {
			this.start = start;
			this.end = end;
		}

		public int getStart() {
			return start;
		}

		public int getEnd() {
			return end;
		}
	}
}
