/**
 * %HEADER%
 */
package net.sf.genomeview.gui.viztracks.hts;

import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JViewport;

import net.sf.genomeview.data.Model;
import net.sf.genomeview.data.provider.PileProvider;
import net.sf.genomeview.gui.viztracks.Track;
import net.sf.genomeview.gui.viztracks.TrackCommunicationModel;
import net.sf.jannot.DataKey;
import net.sf.jannot.Location;

/**
 * 
 * @author Thomas Abeel
 * 
 */
public class PileupTrack extends Track {

	private NumberFormat nf = NumberFormat.getInstance(Locale.US);

	private PileProvider provider;
	
	
	protected int cogOffset(){
		return 10;
	}
	// private String label;
	static class PTMObserver implements Observer {

		private Model model;
		private PileupTrackConfig ptm;

		private PTMObserver(PileupTrackConfig ptm,Model model){
			this.ptm=ptm;
			ptm.addObserver(this);
			this.model=model;
		}
		
		@Override
		public void update(Observable o, Object arg) {
			System.out.println("\tInvalidating track vizbuffers");
			ptm.lastQuery = null;
			/* Force repaint */
			model.refresh();

		}
	}

	public PileupTrack(DataKey key, PileProvider provider, final Model model) {
		super(key, model, true, new PileupTrackConfig(model,key,provider));
		ptm = (PileupTrackConfig)config;
		tooltip = new PileupTooltip(ptm);
		this.provider=provider;
//		provider.addObserver(new PTMObserver(ptm, model));
		nf.setMaximumFractionDigits(0);

	}

	private PileupTrackConfig ptm;

	private PileupTooltip tooltip;

	@Override
	public boolean mouseExited(int x, int y, MouseEvent source) {
		tooltip.setVisible(false);
		return false;
	}

	@Override
	public boolean mouseMoved(int x, int y, MouseEvent source) {
		tooltip.set(source);
		return false;
	}

	private Logger log = LoggerFactory.getLogger(PileupTrack.class.toString());

	@Override
	public int paintTrack(Graphics2D g, int yOffset, double screenWidth, JViewport view, TrackCommunicationModel tcm) {

		ptm.setScreenWidth(screenWidth);
		ptm.setTrackCommunication(tcm);
		// System.out.println("- drawing track "+this);
		Location visible = model.vlm.getAnnotationLocationVisible();
		/* Status messages for data queuing an retrieval */
		// Iterable<Status> status = provider.getStatus(visible.start,
		// visible.end);
//		Iterable<Status> status = provider.getStatus(visible.start, visible.end);
		/* Only retrieve data when location changed */
		if (ptm.lastQuery == null || !ptm.lastQuery.equals(visible)) {
			// System.out.println("--Using fresh data from provider in track");
			/* The actual data */
			// Iterable<Pile> piles = provider.get(visible.start, visible.end);

//			if (model.vlm.getAnnotationLocationVisible().length() < Configuration.getInt("pileup:switchBarLine")) {
				// System.out.println("Track: "+this+"\t"+provider);
				ptm.setVizBuffer(new BarChartBuffer(model,visible, provider, ptm));
//			} else
//				ptm.setVizBuffer(new LineChartBuffer(visible, provider, ptm));

			ptm.lastQuery = model.vlm.getAnnotationLocationVisible();
		}

		/* Do the actual painting */
		int graphLineHeigh = ptm.getVizBuffer().draw(g, yOffset, screenWidth);

		return graphLineHeigh;

	}

	

	public PileupTrackConfig getTrackModel() {
		return ptm;

	}

}
