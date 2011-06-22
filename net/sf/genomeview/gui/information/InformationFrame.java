/**
 * %HEADER%
 */
package net.sf.genomeview.gui.information;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;

import net.sf.genomeview.data.Model;
import net.sf.genomeview.gui.components.TypeCombo;
import net.sf.genomeview.gui.dialog.HelpButton;
import net.sf.jannot.Type;
import be.abeel.gui.GridBagPanel;
import be.abeel.gui.MemoryWidget;

/**
 * Panel with an overview of all available tracks and where they should be
 * visible. Also provides additional information regarding selected items.
 * 
 * This frame spans the right side of the application.
 * 
 * 
 * @author Thomas Abeel
 * 
 */
public class InformationFrame extends GridBagPanel {

	private static final long serialVersionUID = -8504854026653847566L;
	private Model model;

	public InformationFrame(final Model model) {
		this.model = model;
		gc.fill = GridBagConstraints.BOTH;
		gc.weightx = 1;
		gc.weighty = 1;
		gc.gridwidth = 3;
		
		final TrackTable featureTrackList = new TrackTable(model);

		gc.gridy++;
		gc.gridwidth=1;
		gc.weighty = 0;
		gc.weightx = 0;
		add(new HelpButton(model.getGUIManager().getParent(), "Clicking the eye will hide that track.<br><br>Clicking the trash can will unload the data<br><br>You can drag tracks up and down to reorganize them."),gc);
		gc.gridx++;
		gc.gridwidth=2;
		gc.weightx =1;
		add(new JLabel("Track list"), gc);
		
	
		gc.gridwidth=3;
		gc.gridx=0;
		gc.gridy++;
		gc.weighty = 1;
		add(new JScrollPane(featureTrackList), gc);
		gc.gridy++;

		FeatureTable annotationTrackList = new FeatureTable(model);

		gc.weighty = 0;
		gc.gridwidth = 1;
		gc.weightx=0;
		add(new HelpButton(model.getGUIManager().getParent(), "You can select which type of items you want in the table below with the drop down list to the right."),gc);
		gc.gridx++;
		add(new JLabel("Features"), gc);
		TypeCombo type = new TypeCombo(model, false);
		type.setSelectedItem(Type.get("CDS"));
		type.addActionListener(annotationTrackList);
		gc.gridx++;
		gc.weightx=1;
		add(type, gc);

		gc.gridwidth = 3;
		gc.gridx=0;
		gc.gridy++;
		gc.weighty = 1;
		add(new JScrollPane(annotationTrackList), gc);
		
		
		
		gc.gridy++;
		gc.weighty = 0;
		gc.gridwidth=1;
		add(new HelpButton(model.getGUIManager().getParent(), "If you select an item in the view panel, details on that item will be displayed here. <br><br>You can select any text in this window and directly query it at a number the knowledge repositories with the buttons above the text panel."),gc);
		gc.gridx++;
		gc.gridwidth=2;
		add(new JLabel("Details on selected items:"), gc);
		gc.gridy++;
		gc.weightx = 0;
		gc.gridx=0;
		gc.gridwidth=3;
		
		add(new SearchButtons(), gc);
		gc.weightx = 1;
		gc.gridy++;

		gc.weighty = 1;
		JPanel detail = new FeatureDetailPanel(model);
		add(new JScrollPane(detail), gc);

		gc.gridy++;
		gc.weighty = 0.3;

		add(new GeneStructureView(model), gc);

		gc.gridy++;
		gc.weighty = 0.1;
		this.add(new MemoryWidget(),gc);
		
		setPreferredSize(new Dimension(180,50));


	}

	class SearchButtons extends JToolBar {

		/**
		 * 
		 */
		private static final long serialVersionUID = -1886328161085622722L;

		public SearchButtons() {
			super.setFloatable(false);
			add(buttonate(Query.google));
			add(buttonate(Query.ncbiQuery));
			add(buttonate(Query.ensemblQuery));
			add(buttonate(Query.plaza));
			super.addSeparator();

		}

		private JButton buttonate(final Query q) {
			final JButton out;

			if (q.getIcon() != null)
				out = new JButton(q.getIcon());
			else
				out = new JButton(q.getLabel());
			model.addObserver(new Observer() {

				@Override
				public void update(Observable o, Object arg) {
					out.setEnabled(model.selectionModel().isFeatureSelected());

				}
			});

			out.setToolTipText(q.getLabel());
			out.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					q.query(model.selectionModel().getFeatureSelection().first().toString());

				}
			});
			return out;
		}
	}

}
