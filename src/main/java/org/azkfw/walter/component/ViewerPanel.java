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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JViewport;
import javax.swing.plaf.TextUI;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.HighlightPainter;
import javax.swing.text.JTextComponent;

import org.azkfw.walter.searcher.SearchResult;
import org.azkfw.walter.searcher.SearchResult.SearchPosition;

/**
 * @author Kawakicchi
 *
 */
public class ViewerPanel extends JScrollPane {

	/** serialVersionUID */
	private static final long serialVersionUID = 6498259506167179295L;

	private JTextPane text;

	private DefaultStyledDocument style;

	public ViewerPanel() {
		text = new CustomTextPane();

		text.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
		// text.setFont(new Font("MS Gothic", Font.PLAIN, 16));
		text.setEditorKit(new MyEditorKit());

		style = new DefaultStyledDocument();
		text.setStyledDocument(style);

		getViewport().setView(text);
		setRowHeaderView(new LineNumberView(text));
		setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
	}

	public void set(SearchResult result) {
		String data = result.getFileInfo().getData();

		text.setText(data);
		text.setCaretPosition(0);

		text.getHighlighter().removeAllHighlights();
		try {
			Highlighter highlighter = text.getHighlighter();
			Document doc = text.getDocument();
			String text = doc.getText(0, doc.getLength());

			HighlightPainter highlightPainter = DefaultHighlighter.DefaultPainter;
			for (SearchPosition pos : result.getPositions()) {
				highlighter.addHighlight(pos.getStart(), pos.getEnd(), highlightPainter);
			}
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	private class CustomTextPane extends JTextPane {
		/** serialVersionUID */
		private static final long serialVersionUID = 3262543488486327277L;

		private final Color linecolor = new Color(230, 240, 250);
		private final DefaultCaret caret;

		public CustomTextPane() {
			super();
			setOpaque(false);
			caret = new DefaultCaret() {
				@Override
				protected synchronized void damage(Rectangle r) {
					if (r != null) {
						JTextComponent c = getComponent();
						x = 0;
						y = r.y;
						width = c.getSize().width;
						height = r.height;
						c.repaint();
					}
				}
			};
			caret.setBlinkRate(getCaret().getBlinkRate());
			setCaret(caret);
		}

		@Override
		protected void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g;
			Insets i = getInsets();

			int h = caret.height;
			int y = caret.y;
			g2.setPaint(linecolor);
			g2.fillRect(i.left, y, getSize().width - i.left - i.right, h);
			super.paintComponent(g);
		}

		@Override
		public boolean getScrollableTracksViewportWidth() {
			// 改行させない
			Object parent = getParent();
			if (parent instanceof JViewport) {
				JViewport port = (JViewport) parent;
				int w = port.getWidth();
				TextUI ui = getUI();
				Dimension sz = ui.getPreferredSize(this);
				if (sz.width < w) {
					return true;
				}
			}
			return false;
		}
	}

}
