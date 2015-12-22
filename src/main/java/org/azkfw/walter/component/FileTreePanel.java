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

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
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

	private RowSelectionTree tree;
	private DefaultMutableTreeNode root;
	private DefaultTreeModel model;

	public FileTreePanel() {
		listeners = new ArrayList<FileTreePanel.FileTreePanelListener>();

		root = new DefaultMutableTreeNode();
		tree = new RowSelectionTree(root);
		tree.setRootVisible(true);
		model = (DefaultTreeModel) tree.getModel();

		getViewport().setView(tree);
		setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

		tree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (SwingUtilities.isLeftMouseButton(e)
						&& e.getClickCount() == 2) {
					JTree tree = (JTree) e.getSource();
					TreePath path = tree.getPathForLocation(e.getX(), e.getY());
					if (null != path) {
						DefaultMutableTreeNode node = (DefaultMutableTreeNode) path
								.getLastPathComponent();
						if (node.isLeaf()) {
							synchronized (listeners) {
								for (FileTreePanelListener listener : listeners) {
									listener.clickFile((SearchResult) node
											.getUserObject());
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

	public synchronized void addPath(final String path,
			final SearchResult result) {
		String[] ss = path.split(File.separator + File.separator);

		DefaultMutableTreeNode node = root;
		for (int i = 0; i < ss.length; i++) {
			String name = ss[i];

			boolean findFlag = false;
			for (int j = 0; j < node.getChildCount(); j++) {
				DefaultMutableTreeNode child = (DefaultMutableTreeNode) node
						.getChildAt(j);
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
		public void clickFile(final SearchResult result);
	}

	private static class RowSelectionTree extends JTree {
		
		/** serialVersionUID */
		private static final long serialVersionUID = 5530784218999030134L;
		
		private static final Color SELC = new Color(100, 150, 200);
		private Handler handler;

		public RowSelectionTree(TreeNode treeNode) {
			super(treeNode);
		}
		@Override
		public void paintComponent(Graphics g) {
			g.setColor(getBackground());
			g.fillRect(0, 0, getWidth(), getHeight());
			if (getSelectionCount() > 0) {
				g.setColor(SELC);
				for (int i : getSelectionRows()) {
					Rectangle r = getRowBounds(i);
					g.fillRect(0, r.y, getWidth(), r.height);
				}
			}
			super.paintComponent(g);
			if (getLeadSelectionPath() != null) {
				Rectangle r = getRowBounds(getRowForPath(getLeadSelectionPath()));
				g.setColor(hasFocus() ? SELC.darker() : SELC);
				g.drawRect(0, r.y, getWidth() - 1, r.height - 1);
			}
		}

		@Override
		public void updateUI() {
			removeFocusListener(handler);
			super.updateUI();
			setUI(new BasicTreeUI() {
				@Override
				public Rectangle getPathBounds(JTree tree, TreePath path) {
					if (tree != null && treeState != null) {
						return getPathBounds(path, tree.getInsets(),
								new Rectangle());
					}
					return null;
				}

				private Rectangle getPathBounds(TreePath path, Insets insets,
						Rectangle bounds) {
					Rectangle rect = treeState.getBounds(path, bounds);
					if (rect != null) {
						rect.width = tree.getWidth();
						rect.y += insets.top;
					}
					return rect;
				}
			});
			handler = new Handler();
			addFocusListener(handler);
			setCellRenderer(handler);
			setOpaque(false);
		}

		static class Handler extends DefaultTreeCellRenderer implements
				FocusListener {
			@Override
			public Component getTreeCellRendererComponent(JTree tree,
					Object value, boolean selected, boolean expanded,
					boolean leaf, int row, boolean hasFocus) {
				JLabel l = (JLabel) super.getTreeCellRendererComponent(tree,
						value, selected, expanded, leaf, row, hasFocus);
				l.setBackground(selected ? SELC : tree.getBackground());
				l.setOpaque(true);
				return l;
			}

			@Override
			public void focusGained(FocusEvent e) {
				e.getComponent().repaint();
			}

			@Override
			public void focusLost(FocusEvent e) {
				e.getComponent().repaint();
			}
		}
	}
}
