/**
 * %HEADER%
 */
package net.sf.genomeview.gui.explorer;

import java.awt.EventQueue;
import java.util.Observable;
import java.util.Observer;

import net.sf.genomeview.data.Model;

/**
 * 
 * @author Thomas Abeel
 * 
 */
public class GenomeExplorerManager implements Observer {
	GenomeExplorer bg;
	private Model model;

	private boolean autoMode = true;

	public GenomeExplorerManager(Model model) {
		bg = new GenomeExplorer(model);
		this.model = model;
		model.getGUIManager().registerGenomeExplorer(this);
		model.addObserver(this);
		model.getWorkerManager().addObserver(this);
	}

	public void setVisible(final boolean vis) {
		autoMode = vis;
		visi(vis);

	}

	private boolean firstUse = true;

	private void visi(final boolean vis) {
		EventQueue.invokeLater(new Runnable() {

			@Override
			public void run() {
				bg.setVisible(vis);
			}
		});

		if (vis && firstUse) {
			firstUse = false;
			EventQueue.invokeLater(new Runnable() {

				@Override
				public void run() {
					bg.scollToTop();
				}
			});

		}
	}

	@Override
	public void update(Observable o, Object arg) {
		if (!autoMode)
			return;

		if (model.getWorkerManager().runningJobs()>0||model.entries().size() > 0) {
			if (bg.isVisible())
				visi(false);
		} else {
			if (!bg.isVisible()) {
				visi(true);

			}
		}

	}
}