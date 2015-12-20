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

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.azkfw.walter.searcher.SearchResult;

/**
 * @author Kawakicchi
 *
 */
public class FileTreePanel extends JScrollPane {

	/** serialVersionUID */
	private static final long serialVersionUID = 4817941245958620892L;

	private List<FileTreePanelListener> listeners;
	
	private JTree tree;
	private DefaultMutableTreeNode root;
	private DefaultTreeModel model;

	public FileTreePanel() {
		listeners = new ArrayList<FileTreePanel.FileTreePanelListener>();
		
		root = new DefaultMutableTreeNode();
		tree = new JTree(root);
		tree.setRootVisible(true);
		model = (DefaultTreeModel) tree.getModel();

		getViewport().setView(tree);
		setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

		tree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2) {
					JTree tree = (JTree) e.getSource();
					TreePath path = tree.getPathForLocation(e.getX(), e.getY());
					if (null != path) {
						DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
						if (node.isLeaf()) {
							synchronized (listeners) {
								for (FileTreePanelListener listener  : listeners) {
									listener.clickFile((SearchResult)node.getUserObject());
								}
							}
						}
					}
				}
			}
		});
	}

	public void addFileTreePanelListener(final FileTreePanelListener listener) {
		synchronized (listeners) {
			listeners.add(listener);
		}
	}
	
	public void setRoot(final String string) {
		root = new DefaultMutableTreeNode(string);
		model.setRoot(root);
		model.nodeChanged(root);
	}

	public synchronized void addPath(final String path, final SearchResult result) {
		String[] ss = path.split(File.separator);

		DefaultMutableTreeNode node = root;
		for (int i = 0; i < ss.length; i++) {
			String name = ss[i];

			boolean findFlag = false;
			for (int j = 0; j < node.getChildCount(); j++) {
				DefaultMutableTreeNode child = (DefaultMutableTreeNode) node.getChildAt(j);
				if (name.endsWith(child.getUserObject().toString())) {
					node = child;
					findFlag = true;
					break;
				}
			}
			if (!findFlag) {
				DefaultMutableTreeNode child = new DefaultMutableTreeNode(name);
				node.add(child);
				node = child;
				model.nodeChanged(node);
			}
		}
		
		node.setUserObject(result);
	}
	
	public static interface FileTreePanelListener {
		public void clickFile(final SearchResult result) ;
	}
}
