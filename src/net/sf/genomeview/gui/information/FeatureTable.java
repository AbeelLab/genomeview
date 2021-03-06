/**
 * %HEADER%
 */
package net.sf.genomeview.gui.information;

import java.awt.Color;
import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.HashSet;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.DefaultListSelectionModel;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ToolTipManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

import net.sf.genomeview.data.Model;
import net.sf.genomeview.gui.components.TypeCombo;
import net.sf.genomeview.gui.viztracks.annotation.FeatureUtils;
import net.sf.jannot.Feature;
import net.sf.jannot.Location;

/**
 * Provides an overview of all gene structures.
 * 
 * @author Thomas Abeel
 * 
 */
public class FeatureTable extends JTable implements Observer, ActionListener {

	private static final long serialVersionUID = 8956245030328303086L;

	final FeatureTableModel listModel;

	private Model model;

	class FeatureTableCellRenderer extends DefaultTableCellRenderer {

		public Component getTableCellRendererComponent(JTable tbl, Object v, boolean isSelected, boolean isFocused,
				int row, int col) {
			this.setForeground(Color.DARK_GRAY);
			Component c = super.getTableCellRendererComponent(tbl, v, isSelected, isFocused, row, col);
			
			Feature f=(Feature)v;
			setText(FeatureUtils.displayName(f));
			if (tbl.getSelectionModel().isSelectedIndex(row)) {
				c.setBackground(new Color((float)0.7, (float) 0.7, (float) 1.0));
			} else {
				c.setBackground(Color.white);
			}

			return c;
		}

	}

	class FeatureTableSelectionModel extends DefaultListSelectionModel implements Observer {

		private Model model;

		public FeatureTableSelectionModel(Model model) {
			this.model = model;
			model.addObserver(this);
		}

		@Override
		public int getMaxSelectionIndex() {
			if(isSelectionEmpty())
				return -1;
			return selection.last();
		}

		@Override
		public int getMinSelectionIndex() {
			if(isSelectionEmpty())
				return -1;
			return selection.first();
		}

		@Override
		public boolean isSelectedIndex(int index) {
			return selection.contains(index);
		}

		@Override
		public boolean isSelectionEmpty() {
			return selection.size() == 0;
		}

		private SortedSet<Integer> selection = new TreeSet<Integer>();

		@Override
		public void update(Observable o, Object arg) {

			Set<Integer> oldSelection = new HashSet<Integer>();
			oldSelection.addAll(selection);

			SortedSet<Feature> fs = model.selectionModel().getFeatureSelection();
			// System.out.println(fs);
			// int prevIndex = selectedIndex;
			if (fs.size() > 0) {
				for (Feature f : fs) {
					selection.add(listModel.getRow(f));
				}

			} else {
				selection.clear();
			}
			oldSelection.retainAll(selection);
			if (oldSelection.size() != selection.size()) {
				fireValueChanged(false);
			}

			// getSelectionModel().setSelectionInterval(row, row);

		}

	}

	public FeatureTable(final Model model) {
		super(new FeatureTableModel(model));
		FeatureTableSelectionModel ftsm = new FeatureTableSelectionModel(model);
		setSelectionModel(ftsm);
		setDefaultRenderer(String.class, new FeatureTableCellRenderer());
		model.addObserver(this);
		this.model = model;
		listModel = (FeatureTableModel) this.getModel();

		// setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		// getColumnModel().getColumn(0).setPreferredWidth(200);
		for (int i = 1; i < this.getColumnCount(); i++) {
			getColumnModel().getColumn(i).setPreferredWidth(30);
			getColumnModel().getColumn(i).setMaxWidth(50);
		}
		getTableHeader().addMouseMotionListener(new ColumnHeaderToolTips(listModel));
		getTableHeader().setReorderingAllowed(false);

		ToolTipManager.sharedInstance().setInitialDelay(0);

		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() > 0) {

					Feature rf = listModel.getFeature(rowAtPoint(e.getPoint()));

					model.selectionModel().setLocationSelection(rf);

					if (e.getClickCount() > 1) {
						int min = rf.start();
						int max = rf.end();
						model.vlm.center((min+max)/2);
					}
				}

			}
		});
	}

	class ColumnHeaderToolTips extends MouseMotionAdapter {
		private int index = -1;

		private FeatureTableModel listModel;

		public ColumnHeaderToolTips(FeatureTableModel listModel) {
			this.listModel = listModel;

		}

		public void mouseMoved(MouseEvent evt) {

			JTableHeader header = (JTableHeader) evt.getSource();
			JTable table = header.getTable();
			TableColumnModel colModel = table.getColumnModel();
			int vColIndex = colModel.getColumnIndexAtX(evt.getX());
			if (vColIndex != this.index && vColIndex >= 0) {
				header.setToolTipText(listModel.getColumnName(vColIndex));
			}
			this.index = vColIndex;

		}
	}

	@Override
	public void update(Observable o, Object arg) {
		SortedSet<Feature> fs = model.selectionModel().getFeatureSelection();

		if (fs.size() == 1) {
			
			if (fs.first().type() == listModel.getType()) {
				int row = listModel.getRow(fs.first());
				// getSelectionModel().setSelectionInterval(row, row);
				if (!(getParent() instanceof JViewport)) {
					return;
				}
				JViewport viewport = (JViewport) getParent();

				// This rectangle is relative to the table where the
				// northwest corner of cell (0,0) is always (0,0).
				Rectangle rect = getCellRect(row, 0, true);

				// The location of the view relative to the table
				Rectangle viewRect = viewport.getViewRect();

				int topVisible = viewport.getViewRect().y;
				int bottomVisible = viewport.getViewRect().height + topVisible;

				/* When the cell is visible, don't do anything */
				if (rect.y > topVisible && rect.y + rect.height < bottomVisible) {
					return;
				}
				// Translate the cell location so that it is relative
				// to the view, assuming the northwest corner of the
				// view is (0,0).
				rect.setLocation(rect.x - viewRect.x, rect.y - viewRect.y);

				// Calculate location of rect if it were at the center of view
				int centerX = (viewRect.width - rect.width) / 2;
				int centerY = (viewRect.height - rect.height) / 2;

				// Fake the location of the cell so that scrollRectToVisible
				// will move the cell to the center
				if (rect.x < centerX) {
					centerX = -centerX;
				}
				if (rect.y < centerY) {
					centerY = -centerY;
				}
				rect.translate(centerX, centerY);

				// Scroll the area into view.
				viewport.scrollRectToVisible(rect);
				
			}
		}

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		listModel.setType(((TypeCombo) e.getSource()).getTerm());
	}
}
