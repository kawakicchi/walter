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

import java.awt.Insets;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.TransferHandler;
import javax.swing.TransferHandler.TransferSupport;

import org.azkfw.walter.SearchOption;

/**
 * @author Kawakicchi
 *
 */
public class SearchPanel extends JPanel {

	/** serialVersionUID */
	private static final long serialVersionUID = -7030174679922755501L;

	private static final int MARGIN = 6;
	private static final int COMPONENT_HEIGHT = 24;
	private static final int COMPONENT_MARGIN = 4;

	private JLabel lblKeyword;
	private JTextField txtKeyword;
	private JLabel lblDirectory;
	private JTextField txtDirectory;
	private JButton btnDirectory;
	private JButton btnSearch;
	private JLabel lblExclude;
	private JTextField txtExclude;

	private List<SearchPanelListener> listeners;

	public SearchPanel() {
		setLayout(null);

		listeners = new ArrayList<SearchPanelListener>();

		lblKeyword = new JLabel("Keyword");
		add(lblKeyword);
		txtKeyword = new JTextField("class");
		add(txtKeyword);

		lblDirectory = new JLabel("Target directory");
		add(lblDirectory);
		txtDirectory = new JTextField("/Users/Kawakicchi/git/walter");
		txtDirectory.setTransferHandler(new DropFileHandler());
		add(txtDirectory);
		btnDirectory = new JButton("参照");
		add(btnDirectory);

		lblExclude = new JLabel("Exclude file");
		add(lblExclude);
		txtExclude = new JTextField("*.zip, *.jar, *.war");
		add(txtExclude);

		btnSearch = new JButton("Search");
		add(btnSearch);

		btnSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				doSearch();
			}
		});

		addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				Insets insets = getInsets();
				int width = getWidth() - (insets.left + insets.right);
				//int height = getHeight() - (insets.top + insets.bottom);

				int x = MARGIN;
				int y = MARGIN;

				lblKeyword.setBounds(x, y, width - (MARGIN * 2), COMPONENT_HEIGHT);
				y += COMPONENT_HEIGHT;
				txtKeyword.setBounds(x, y, width - (MARGIN * 2), COMPONENT_HEIGHT);
				y += COMPONENT_HEIGHT + COMPONENT_MARGIN;

				lblDirectory.setBounds(x, y, width - (MARGIN * 2), COMPONENT_HEIGHT);
				y += COMPONENT_HEIGHT;
				txtDirectory.setBounds(x, y, width - (MARGIN * 2 + COMPONENT_MARGIN + COMPONENT_HEIGHT), COMPONENT_HEIGHT);
				btnDirectory.setBounds(width - (COMPONENT_HEIGHT + MARGIN), y, COMPONENT_HEIGHT, COMPONENT_HEIGHT);
				y += COMPONENT_HEIGHT + COMPONENT_MARGIN;

				lblExclude.setBounds(x, y, width - (MARGIN * 2), COMPONENT_HEIGHT);
				y += COMPONENT_HEIGHT;
				txtExclude.setBounds(x, y, width - (MARGIN * 2), COMPONENT_HEIGHT);
				y += COMPONENT_HEIGHT + COMPONENT_MARGIN;

				btnSearch.setBounds(MARGIN, y, width - MARGIN*2, COMPONENT_HEIGHT+6);
			}
		});
	}

	public void addSearchPanelListener(final SearchPanelListener listener) {
		synchronized (listeners) {
			listeners.add(listener);
		}
	}

	private void doSearch() {
		SearchOption option = new SearchOption();
		option.setKeyword(txtKeyword.getText());
		option.setTargetDirectory(txtDirectory.getText());

		synchronized (listeners) {
			for (SearchPanelListener listener : listeners) {
				listener.search(option);
			}
		}
	}
	
	public class DropFileHandler extends TransferHandler {

		/** serialVersionUID */
		private static final long serialVersionUID = 1885386084507241096L;

		/**
		 * ドロップされたものを受け取るか判断 (ファイルのときだけ受け取る)
		 */
		@Override
		public boolean canImport(TransferSupport support) {
			if (!support.isDrop()) {
				// ドロップ操作でない場合は受け取らない
		        return false;
		    }

			if (!support.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
				// ドロップされたのがファイルでない場合は受け取らない
		        return false;
		    }

			return true;
		}

		/**
		 * ドロップされたファイルを受け取る
		 */
		@Override
		public boolean importData(TransferSupport support) {
			// 受け取っていいものか確認する
			if (!canImport(support)) {
		        return false;
		    }

			// ドロップ処理
			Transferable t = support.getTransferable();
			try {
				// ファイルを受け取る
				List<File> files = (List<File>) t.getTransferData(DataFlavor.javaFileListFlavor);

				// テキストエリアに表示するファイル名リストを作成する
				StringBuffer fileList = new StringBuffer();
				for (File file : files){
					if (0 < fileList.length()) {
						fileList.append(";");
					}
					fileList.append(file.getAbsolutePath());
				}

				// テキストエリアにファイル名のリストを表示する
				txtDirectory.setText(fileList.toString());
			} catch (UnsupportedFlavorException | IOException e) {
				e.printStackTrace();
			}
			return true;
		}
	}
}
