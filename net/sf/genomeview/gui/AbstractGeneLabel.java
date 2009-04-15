/**
 * %HEADER%
 */
package net.sf.genomeview.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JLabel;

import net.sf.genomeview.core.Configuration;
import net.sf.genomeview.data.Model;
import net.sf.genomeview.gui.components.CollisionMap;
import net.sf.jannot.Location;

/**
 * Parent class for GeneEvidencelabel and GeneStructureLabel
 * 
 * @author Thomas Abeel
 * 
 */
public abstract class AbstractGeneLabel extends JLabel implements Observer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5579565140607328154L;

	protected Model model;

	/**
	 * Map that contains all TrackKey,ItemName pairs and their corresponding
	 * rectangle.
	 */
	protected CollisionMap collisionMap;

	public AbstractGeneLabel(final Model model) {
		this.model = model;
		this.collisionMap = new CollisionMap(model);
		this.addMouseWheelListener(new MouseWheelListener() {

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				double rot = e.getWheelRotation() / 10.0;
				double center = translateScreenToGenome(e.getX(), model
						.getAnnotationLocationVisible());
				double start = model.getAnnotationLocationVisible().start();
				double end = model.getAnnotationLocationVisible().end();
				double length = end - start + 1;
				double fractionL = (center - start) / length;
				double fractionR = (end - center) / length;
				// System.out.println(fractionL+"\t"+fractionR);
				if (rot < 0
						&& length < Configuration.getInt("minimumNucleotides")) {
					return;
				}
				double sizeChange = rot * length;
				// System.out.println("SC:"+sizeChange);
				int newStart = (int) (start - fractionL * sizeChange);
				int newEnd = (int) (end + fractionR * sizeChange);

				model.setAnnotationLocationVisible(new Location(newStart,
						newEnd));

			}

		});
	}

	protected boolean drag = false;

	/**
	 * Keeps track of how many pixels are already used in the Y direction
	 */
	protected int framePixelsUsed = 0;

	protected void paintVisibilityFrame(Graphics g, Location frame,
			Location real) {

		g.setColor(Color.RED);

		int start = translateGenomeToScreen(frame.start(), real);
		int end = translateGenomeToScreen(frame.end(), real);
		g
				.drawRect(start, 0, end - start, framePixelsUsed > g
						.getClipBounds().height ? framePixelsUsed - 1 : g
						.getClipBounds().height - 1);

	}

	protected boolean modifier(MouseEvent e) {
		return e.isAltDown() || e.isShiftDown() || e.isControlDown();
	}

	protected boolean button1(MouseEvent e) {
		return e.getButton() == MouseEvent.BUTTON1;
	}

	protected boolean button2(MouseEvent e) {
		return e.getButton() == MouseEvent.BUTTON2;
	}

	protected boolean button3(MouseEvent e) {
		return e.getButton() == MouseEvent.BUTTON3;
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);

	}

	/**
	 * Translates a coordinate on the genome to a screen coordinate. This is
	 * only needed for the X coordinate
	 * 
	 * @param g
	 *            the graphics context for the visible Location
	 * @param pos
	 *            the position to translate, this may be outside the visible
	 *            Location
	 * @return
	 */
	protected int translateGenomeToScreen(int pos, Location r) {
		int genomeWidth = r.length();
		int relativePos = pos - r.start();
		return (int) (screenWidth / genomeWidth * relativePos);

	}

	/**
	 * Translates a coordinate on the screen to a genome coordinate. This is
	 * only needed for the X coordinate
	 * 
	 * @param g
	 *            the graphics context for the visible Location
	 * @param pos
	 *            the position to translate,
	 * 
	 * @return
	 */
	protected int translateScreenToGenome(int pos, Location r) {
		// Location r = model.getAnnotationLocationVisible();
		int genomeWidth = r.length();
		return (int) (pos / screenWidth * genomeWidth) + r.start();

	}

	protected double screenWidth = 0;

	public void update(Observable arg0, Object arg1) {
		repaint();
	}

}