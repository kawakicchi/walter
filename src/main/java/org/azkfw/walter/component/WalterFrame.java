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
package org.azkfw.walter.component;

import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JSplitPane;

import org.azkfw.walter.SearchFileInfo;
import org.azkfw.walter.SearchOption;
import org.azkfw.walter.engine.WalterEngine;
import org.azkfw.walter.engine.WalterEngineEvent;
import org.azkfw.walter.engine.WalterEngineListener;
import org.azkfw.walter.searcher.SearchResult;

/**
 * @author Kawakicchi
 *
 */
public class WalterFrame extends JFrame {

	/** serialVersionUID */
	private static final long serialVersionUID = 1573289008832957479L;

	private StatusBar statusBar;
	private JSplitPane pnl1;
	private JSplitPane pnl2;
	private SearchPanel searchPanel;
	private FileTreePanel fileTreePanel;
	
	private ViewerTabbedPanel viewerTabPanel;

	private int searchFileCount;

	public WalterFrame() {
		setTitle("Walter");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLayout(null);

		searchPanel = new SearchPanel();
		viewerTabPanel = new ViewerTabbedPanel();
		fileTreePanel = new FileTreePanel();
		statusBar = new StatusBar();

		pnl1 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);
		pnl2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true);
		pnl2.setTopComponent(searchPanel);
		pnl2.setBottomComponent(fileTreePanel);
		pnl2.setDividerLocation(240);
		pnl1.setLeftComponent(pnl2);
		pnl1.setRightComponent(viewerTabPanel);
		pnl1.setDividerLocation(320);

		add(pnl1);
		add(statusBar);

		searchPanel.addSearchPanelListener(new SearchPanelListener() {
			public void search(final SearchOption option) {
				fileTreePanel.setRoot(option.getTargetDirectory());

				searchFileCount = 0;

				WalterEngine app = new WalterEngine(option);
				app.addWalterEngineListener(new WalterEngineListener() {
					public void start(WalterEngineEvent event) {
						
					}
					public void end(WalterEngineEvent event) {
						statusBar.setMessage(String.format("%d 件見つかりました", searchFileCount));
					}
					public void findFile(SearchResult result, WalterEngineEvent event) {
						doFindFile(option, result);
					}
				});
				app.start();
			}
		});
		fileTreePanel.addFileTreePanelListener(new FileTreePanel.FileTreePanelListener() {
			public void clickFile(final SearchResult result) {
				ViewerPanel viewerPanel = new ViewerPanel();
				viewerPanel.set(result);
				
				viewerTabPanel.add(result.getFileInfo().getFile().getName(), viewerPanel);
				viewerTabPanel.setSelectedIndex( viewerTabPanel.getTabCount()-1 );
			}
		});

		addComponentListener(new ComponentAdapter() {
			public void componentResized(final ComponentEvent e) {
				Insets insets = getInsets();
				int width = getWidth() - (insets.left + insets.right);
				int height = getHeight() - (insets.top + insets.bottom);
				pnl1.setSize(width, height - 30);
				statusBar.setBounds(0, height - 30, width, 30);
			}
		});

		statusBar.setMessage("");

		GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
		Rectangle rect = env.getMaximumWindowBounds();
		setBounds(rect);
	}

	private void doFindFile(final SearchOption option, final SearchResult result) {
		SearchFileInfo fileInfo = result.getFileInfo();
		String path = fileInfo.getFile().getAbsolutePath().substring(option.getTargetDirectory().length());
		if (path.startsWith(File.separator)) {
			path = path.substring(1);
		}
		fileTreePanel.addPath(path, result);

		searchFileCount++;
		statusBar.setMessage(String.format("検索中・・・ %d 件見つかりました", searchFileCount));
	}
}
