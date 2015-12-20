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
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JTextPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import javax.swing.text.Element;

/**
 * @author Kawakicchi
 *
 */
public class LineNumberView extends JComponent {

	private static final int MARGIN = 5;
	private final JTextPane textPane;
	private final FontMetrics fontMetrics;

	// private final int topInset;
	private final int fontAscent;
	private final int fontHeight;
	private final int fontDescent;
	private final int fontLeading;
	
	private Color background;

	public LineNumberView(JTextPane textPane) {
		this.textPane = textPane;
		Font font = textPane.getFont();
		fontMetrics = getFontMetrics(font);
		fontHeight = fontMetrics.getHeight();
		fontAscent = fontMetrics.getAscent();
		fontDescent = fontMetrics.getDescent();
		fontLeading = fontMetrics.getLeading();
		// topInset = textArea.getInsets().top;

		background = new Color(240, 240, 240);
		
		textPane.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				repaint();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				repaint();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
			}
		});
		textPane.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				revalidate();
				repaint();
			}
		});
		Insets i = textPane.getInsets();
		setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.GRAY),
				BorderFactory.createEmptyBorder(i.top, MARGIN, i.bottom, MARGIN - 1)));
		setOpaque(true);
		setBackground(Color.WHITE);
		setFont(font);
	}

	private int getComponentWidth() {
		Document doc = textPane.getDocument();
		Element root = doc.getDefaultRootElement();
		int lineCount = root.getElementIndex(doc.getLength());
		int maxDigits = Math.max(3, String.valueOf(lineCount).length());
		Insets i = getBorder().getBorderInsets(this);
		return maxDigits * fontMetrics.stringWidth("0") + i.left + i.right;
		// return 48;
	}

	private int getLineAtPoint(int y) {
		Element root = textPane.getDocument().getDefaultRootElement();
		int pos = textPane.viewToModel(new Point(0, y));
		return root.getElementIndex(pos);
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(getComponentWidth(), textPane.getHeight());
	}

	@Override
	public void paintComponent(Graphics g) {
		//g.setColor(getBackground());
		g.setColor(background);
		Rectangle clip = g.getClipBounds();
		g.fillRect(clip.x, clip.y, clip.width, clip.height);

		g.setColor(getForeground());
		int base = clip.y;
		int start = getLineAtPoint(base);
		int end = getLineAtPoint(base + clip.height);
		int y = start * fontHeight;
		int rmg = getBorder().getBorderInsets(this).right;
		for (int i = start; i <= end; i++) {
			String text = String.valueOf(i + 1);
			int x = getComponentWidth() - rmg - fontMetrics.stringWidth(text);
			y += fontAscent;
			g.drawString(text, x, y);
			y += fontDescent + fontLeading;
		}
	}
}
