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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;

import javax.swing.text.AbstractDocument;
import javax.swing.text.BoxView;
import javax.swing.text.ComponentView;
import javax.swing.text.Element;
import javax.swing.text.IconView;
import javax.swing.text.LabelView;
import javax.swing.text.ParagraphView;
import javax.swing.text.Position;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

/**
 * @author Kawakicchi
 *
 */
public class MyEditorKit extends StyledEditorKit {
//public class MyEditorKit extends HTMLEditorKit {
	/** serialVersionUID */
	private static final long serialVersionUID = -329540653987410008L;

	public MyEditorKit() {
	}
	
	@Override
	public ViewFactory getViewFactory() {
		return new MyViewFactory();
	}

	public class MyViewFactory implements ViewFactory {
		@Override
		public View create(Element elem) {
			String kind = elem.getName();
			if (kind != null) {
				if (kind.equals(AbstractDocument.ContentElementName)) {
					return new WhitespaceLabelView(elem);
				} else if (kind.equals(AbstractDocument.ParagraphElementName)) {
					return new MyParagraphView(elem);
				} else if (kind.equals(AbstractDocument.SectionElementName)) {
					return new BoxView(elem, View.Y_AXIS);
				} else if (kind.equals(StyleConstants.ComponentElementName)) {
					return new ComponentView(elem);
				} else if (kind.equals(StyleConstants.IconElementName)) {
					return new IconView(elem);
				}
			}
			return new LabelView(elem);
		}
	}

	public static class MyParagraphView extends ParagraphView {

		public MyParagraphView(Element elem) {
			super(elem);
		}

		@Override
		public void paint(Graphics g, Shape allocation) {
			super.paint(g, allocation);
			paintCustomParagraph(g, allocation);
		}

		private void paintCustomParagraph(final Graphics g, final Shape shape) {
			try {
				Shape paragraph = modelToView(getEndOffset(), shape, Position.Bias.Backward);
				Rectangle r = (paragraph == null) ? shape.getBounds() : paragraph.getBounds();
				int x = r.x;
				int y = r.y;
				int h = r.height;
				
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setColor(Color.red);
				
				// paragraph mark
				g2.drawLine(x + 1, y + h / 2, x + 1, y + h - 4);
				g2.drawLine(x + 2, y + h / 2, x + 2, y + h - 5);
				g2.drawLine(x + 3, y + h - 6, x + 3, y + h - 6);
				g2.dispose();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static class WhitespaceLabelView extends LabelView {

		private static final Color pc = new Color(130, 140, 120);
		private static final BasicStroke dashed = new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, new float[] { 1f }, 0f);
		private static final BasicStroke stroke = new BasicStroke(1f);

		public WhitespaceLabelView(Element elem) {
			super(elem);
		}

		@Override
		public void paint(Graphics g, Shape a) {
			super.paint(g, a);
			Graphics2D g2 = (Graphics2D) g.create();
			Stroke strokeBackup = g2.getStroke();
			Rectangle alloc = (a instanceof Rectangle) ? (Rectangle) a : a.getBounds();

			FontMetrics fontMetrics = g.getFontMetrics();
			int spaceWidth1 = fontMetrics.stringWidth(" ");
			int spaceWidth2 = fontMetrics.stringWidth("　");
			
			int sumOfTabs = 0;

			String text = getText(getStartOffset(), getEndOffset()).toString();

			for (int i = 0; i < text.length(); i++) {
				String s = text.substring(i, i + 1);
				int previousStringWidth = fontMetrics.stringWidth(text.substring(0, i)) + sumOfTabs;
				int sx = alloc.x + previousStringWidth;
				int sy = alloc.y + alloc.height - fontMetrics.getDescent();
				
				if (" ".equals(s)) {
					// g2.setColor(Color.red);
					// g2.setStroke(stroke);
					// g2.setPaint(pc);

					// g2.drawLine(sx + 1, sy - 1, sx + spaceWidth1 - 2, sy - 1);
					// g2.drawLine(sx + 2, sy    , sx + spaceWidth1 - 2, sy    );
				} else if ("　".equals(s)) {
					g2.setColor(Color.blue);
					g2.setStroke(dashed);
					g2.setPaint(pc);

					g2.drawLine(sx + 1, sy - 1, sx + spaceWidth2 - 2, sy - 1);
					g2.drawLine(sx + 2, sy    , sx + spaceWidth2 - 2, sy    );
				} else if ("\t".equals(s)) {
					int tabWidth = (int) getTabExpander().nextTabStop((float) sx, i) - sx;
					g2.setColor(pc);
					
					g2.drawLine(sx + 2, sy - 0, sx + 2 + 2, sy - 0);
					g2.drawLine(sx + 2, sy - 1, sx + 2 + 1, sy - 1);
					g2.drawLine(sx + 2, sy - 2, sx + 2 + 0, sy - 2);

					g2.setStroke(dashed);
					g2.drawLine(sx + 2, sy, sx + tabWidth - 2, sy);
					sumOfTabs += tabWidth - 6; // TODO タブのカウントが
				}
				
				g2.setStroke(strokeBackup);
			}
			g2.dispose();
		}
	}
}
