/**
 * %HEADER%
 */
package net.sf.genomeview.gui.menu.edit;

import java.awt.event.ActionEvent;
import java.util.Observable;

import net.sf.genomeview.core.AnalyzedFeature;
import net.sf.genomeview.core.Configuration;
import net.sf.genomeview.data.Model;
import net.sf.genomeview.gui.MessageManager;
import net.sf.genomeview.gui.menu.AbstractModelAction;
import net.sf.jannot.Feature;
import net.sf.jannot.Strand;
import net.sf.jannot.Type;
import net.sf.jannot.refseq.MemorySequence;
import net.sf.jannot.refseq.Sequence;
import net.sf.jannot.utils.SequenceTools;

/**
 * 
 * @author Thomas Abeel
 * 
 */
public class ExtendToStartCodonAction extends AbstractModelAction {

	public ExtendToStartCodonAction(Model model) {
		super(MessageManager.getString("editmenu.extend_to_next_start_codon"), model);
		model.addObserver(this);
		update(null, null);

	}

	/**
     * 
     */
	private static final long serialVersionUID = 2143874687832094430L;

	@Override
	public void update(Observable o, Object obj) {
		if (model.selectionModel().getFeatureSelection().size() == 1
				&& model.selectionModel().getFeatureSelection().first().type() == Type.get("CDS")) {
			AnalyzedFeature af = new AnalyzedFeature(model.vlm.getSelectedEntry().sequence(), model.selectionModel()
					.getFeatureSelection().first(), model.getAAMapping());

			setEnabled(af.hasMissingStartCodon());
		} else
			setEnabled(false);

	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		assert (model.selectionModel().getFeatureSelection() != null);
		assert (model.selectionModel().getFeatureSelection().size() == 1);
		Feature rf = model.selectionModel().getFeatureSelection().iterator().next();
		Sequence seq = model.vlm.getSelectedEntry().sequence();
		String nt = SequenceTools.extractSequence(seq, rf).stringRepresentation();
		// int rest = nt.length() % 3;
		AnalyzedFeature af = new AnalyzedFeature(model.vlm.getSelectedEntry().sequence(), rf, model.getAAMapping());
		assert (af.hasMissingStartCodon());
		if (rf.strand() == Strand.FORWARD) {
			int start = rf.start();
			while (!hasStart(start)&&start>1) {
				start--;
			}
			rf.location()[0].setStart(start);

		} else if (rf.strand() == Strand.REVERSE) {
			int start = rf.end();

			while (!hasReverseStart(start)&&start<model.vlm.getSelectedEntry().getMaximumLength()) {
				start++;
			}
			rf.location()[rf.location().length-1].setEnd(start+2);

		}

	}

	private boolean hasReverseStart(int start) {
		String codon = SequenceTools.reverseComplement(
				model.vlm.getSelectedEntry().sequence().subsequence(start, start + 3)).stringRepresentation();
		if (model.getAAMapping().isStart(codon)) {
			if (!Configuration.getBoolean("general:onlyMethionineAsStart") || model.getAAMapping().get(codon) == 'M')
				return true;
		}
		return false;

	}

	private boolean hasStart(int start) {
		String codon = model.vlm.getSelectedEntry().sequence().subsequence(start, start + 3).stringRepresentation();
		if (model.getAAMapping().isStart(codon)) {
			if (!Configuration.getBoolean("general:onlyMethionineAsStart") || model.getAAMapping().get(codon) == 'M')
				return true;
		}
		return false;

	}
}
