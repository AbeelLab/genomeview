/**
 * %HEADER%
 */
package net.sf.genomeview.gui.annotation.track;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;

import net.sf.genomeview.data.Model;
import net.sf.genomeview.gui.Convert;
import net.sf.jannot.Entry;
import net.sf.jannot.Location;

public class TickmarkTrack extends Track {

	public TickmarkTrack(Model model) {
		super(model,true);
	}

	@Override
	public int paint(Graphics g, Entry entry,int yOffset,double width) {
		Location r=model.getAnnotationLocationVisible();
		g.setColor(Color.BLACK);
        g.drawLine(0, yOffset + 15, g.getClipBounds().width, yOffset + 15);

        if (r.start() == r.end()) {
            return 32;
        }
        // determine the tickDistance, we aim for 10 ticks on screen.
        int length = r.length();
        int scale = (int) Math.log10(length / 10.0);
        int multiplier = (int) (length / Math.pow(10, scale + 1));
        int tickDistance = (int) (Math.pow(10, scale) * multiplier);
        if (tickDistance == 0)
            tickDistance = 1;
        // paint the ticks
        int currentTick = (r.start() - r.start() % tickDistance) + 1;
        boolean up = true;
        while (currentTick < r.end()) {
            int xpos = Convert.translateGenomeToScreen(currentTick, r,width);
            String s = "" + currentTick;

            if (up) {
                g.drawLine(xpos, yOffset + 2, xpos, yOffset + 28);
                g.drawString(s, xpos + 2, yOffset + 14);
            } else {
                g.drawLine(xpos, yOffset + 2, xpos, yOffset + 28);
                g.drawString(s, xpos + 2, yOffset + 26);
            }
            up = !up;

            currentTick += tickDistance;

        }
      return 32;
	}

	@Override
	public String displayName() {
		return "Ruler";
	}

	

}
