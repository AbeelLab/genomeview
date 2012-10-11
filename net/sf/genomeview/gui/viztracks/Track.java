/**
 * This file is part of GenomeView, a genome browser and annotation curator
 * 
 * Copyright (C) 2012 Thomas Abeel
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 *
 * Project: http://genomeview.org/
 */
package net.sf.genomeview.gui.viztracks;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JViewport;

import net.sf.genomeview.core.Icons;
import net.sf.genomeview.data.Model;
import net.sf.genomeview.data.provider.Status;
import net.sf.genomeview.gui.Convert;
import net.sf.genomeview.gui.viztracks.annotation.StructureTrack;
import net.sf.jannot.DataKey;
import net.sf.jannot.Entry;
import net.sf.jannot.Location;

/**
 * Abstract class for visualization tracks.
 * 
 * @author Thomas Abeel
 *
 */
public abstract class Track {

	
	
	final protected Model model;

	final protected DataKey dataKey;
	protected Entry entry;
	
	
	/**
	 * Model for track configuration
	 */
	final protected TrackConfig config;

	public static void paintStatus(Graphics g, Iterable<Status> status, int y, int returnTrackHeight, Location visible,
			double screenWidth) {
		for (Status st : status) {
			// System.out.println("Not ready "+st.start()+"\t"+st.end());
			if (!st.isReady()) {
				int x1 = Convert.translateGenomeToScreen(st.start(), visible, screenWidth);
				int x2 = Convert.translateGenomeToScreen(st.end() + 1, visible, screenWidth);
				g.setColor(new Color(0, 255, 0, 100));

				g.fillRect(x1, y, x2 - x1 + 1, returnTrackHeight);
				if (visible.overlaps(st.start(), st.end())) {
					g.setColor(Color.BLACK);
					g.drawString("Retrieving data...", 100, y + returnTrackHeight / 2);
				}

			}
		}

	}
	@Deprecated
	protected Track(DataKey key, Model model, boolean visible, boolean collapsible) {
		this(key,model,visible,collapsible,new TrackConfig(model,key));
	}
//	private TrackConfigWindow tcw;
	protected Track(DataKey key, Model model, boolean visible, boolean collapsible,TrackConfig config) {
		this.model = model;
		this.dataKey = key;
		this.config=config;
		TrackConfigWindow tcw=new TrackConfigWindow(model,config);
		log.log(Level.INFO, "Creating track\t" + key + "\t" + visible + "\t" + collapsible);
		this.entry = model.getSelectedEntry();
		config.setCollapsible(collapsible);
//		this.collapsible = collapsible;
		config.addObserver(model);
		
		
	}

	// private boolean visible;
	private static final Logger log = Logger.getLogger(Track.class.getCanonicalName());

	/**
	 * To pass along mouse clicks from the original panel.
	 */
	public boolean mouseClicked(int x, int y, MouseEvent source) {
		if (configCog != null && configCog.contains(x, y)) {
			log.finest("Track consumes click");
			config.setConfigVisible(true);
//			this.setCollapsed(!this.isCollapsed());
			source.consume();
			return true;
		}
		return false;
	}

	/**
	 * To pass along mouse movements from the original panel.
	 */
	public boolean mouseMoved(int x, int y, MouseEvent source) {
		return false;
	}

	/**
	 * To pass along mouse enters from the original panel.
	 */
	public boolean mouseEntered(int x, int y, MouseEvent source) {
		return false;
	}

	/**
	 * To pass along mouse exits from the original panel.
	 */
	public boolean mouseExited(int x, int y, MouseEvent source) {
		return false;
	}

	/**
	 * To pass along mouse presses from the original panel.
	 */
	public boolean mousePressed(int x, int y, MouseEvent source) {
		return false;
	}

	/**
	 * To pass along mouse releases from the original panel.
	 */
	public boolean mouseReleased(int x, int y, MouseEvent source) {
		return false;
	}

	/**
	 * To pass along mouse dragging from the original panel.
	 */
	public boolean mouseDragged(int x, int y, MouseEvent source) {
		return false;
	}

	private Rectangle configCog = null;
	private Color[] background = new Color[] { new Color(204, 238, 255, 75), new Color(255, 255, 204, 75) };

	private void paintConfigCog(Graphics2D g, int yOffset, double width) {
//		config.setConfigOffset((int)width-15,yOffset+5);
//		if (isCollapsible()) {
		
		g.translate(0, yOffset);
		g.drawImage(Icons.COG, (int)width-15, 5, null);//Icons.COG
		configCog = new Rectangle((int) width - 15, 5, 12, 12);
			
//			g.setColor(Color.WHITE);
//			g.fill(collapse);
//			g.setColor(Color.BLACK);
//			g.draw(collapse);
//			g.drawLine(collapse.x + 2, 10, collapse.x + 8, 10);
//			if (isCollapsed())
//				g.drawLine(collapse.x + 5, 7, collapse.x + 5, 13);
			g.translate(0, -yOffset);
//		}
	}

	/**
	 * Paint this track in the annotation label and return the height it
	 * occupies.
	 * 
	 * @param g
	 *            graphics context to paint on
	 * @param view
	 * @param tcm
	 * @param model
	 *            the entry that is currently displayed
	 * @return the height that was painted
	 */
	public int paint(Graphics g, int yOffset, double width, int index, JViewport view, TrackCommunicationModel tcm) {
		int used = paintTrack((Graphics2D) g, yOffset, width, view, tcm);

		if (index >= 0 && !(this instanceof StructureTrack)) {
			Rectangle r = new Rectangle(0, yOffset, (int) width + 1, used);
			g.setColor(background[index % 2]);
			g.fillRect(r.x, r.y, r.width, r.height);
		}

		paintConfigCog((Graphics2D) g, yOffset, width);
		paintDisplayName((Graphics2D) g, yOffset);
		return used;
	}

	protected void paintDisplayName(Graphics2D g, int yOffset) {
//		if (Configuration.getBoolean("showTrackName")) {
//			g.setColor(Color.black);
//
//			g.drawString(ftm.displayName(), 10, lineThickness);
//		}
//		/* Draw label */
//		String name = config.displayName();
//		FontMetrics metrics = g.getFontMetrics();
//		int hgt = metrics.getHeight();
//		int adv = metrics.stringWidth(name);
//
////		g.setColor(Color.WHITE);
////		g.fillRect(10, originalYOffset, adv + 2, hgt + 2);
////
////		g.setColor(Color.BLUE);
////		g.drawString(name, 10, originalYOffset + hgt - 2);
		g.setColor(Color.BLACK);
		g.drawString(config.shortDisplayName(), 10, yOffset + 24 - 2);
		
	}
	/**
	 * Paint the actual track
	 * 
	 * @param g
	 *            graphics environment
	 * @param e
	 *            the currently visible entry
	 * @param yOffset
	 *            the yOffset that should be taken into account when painting
	 * @param width
	 *            the width of the track
	 * @param view
	 * @param tcm
	 * @return the height this track uses
	 */
	protected abstract int paintTrack(Graphics2D g, int yOffset, double width, JViewport view,
			TrackCommunicationModel tcm);

	

	public DataKey getDataKey() {
		return dataKey;
	}

	

	/**
	 * Performs clean-up of caches and buffers. After this method is called, the
	 * track is allowed to be in an inconsistent state.
	 * 
	 */
	public void clear() {
		
	}
	public TrackConfig config() {
		return config;
	}

}
