/**
 * %HEADER%
 */
package net.sf.genomeview.gui;

import java.awt.KeyEventDispatcher;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.FocusManager;

import net.sf.genomeview.data.Model;
import net.sf.genomeview.gui.menu.navigation.AnnotationMoveLeftAction;
import net.sf.genomeview.gui.menu.navigation.AnnotationMoveRightAction;
import net.sf.genomeview.gui.menu.navigation.AnnotationZoomInAction;
import net.sf.genomeview.gui.menu.navigation.AnnotationZoomOutAction;

public class Hotkeys implements KeyEventDispatcher {

	private Model model;
	public Hotkeys(Model model) {
		this.left=new AnnotationMoveLeftAction(model);
		this.right=new AnnotationMoveRightAction(model);
		this.zoomin=new AnnotationZoomInAction(model);
		this.zoomout=new AnnotationZoomOutAction(model);
		this.model=model;
	}

	private Action left,right,zoomin,zoomout;
	@Override
	public boolean dispatchKeyEvent(KeyEvent e) {
		if(!FocusManager.getCurrentManager().getActiveWindow().equals(model.getParent()))
			return false;
		switch (e.getKeyCode()) {
		case KeyEvent.VK_LEFT:
		case KeyEvent.VK_NUMPAD4:
			left.actionPerformed(null);
			return true;
		case KeyEvent.VK_RIGHT:
		case KeyEvent.VK_NUMPAD6:
			right.actionPerformed(null);
			return true;
		case KeyEvent.VK_ADD:
		case KeyEvent.VK_UP:
		case KeyEvent.VK_PLUS:
		case KeyEvent.VK_EQUALS:
			zoomin.actionPerformed(null);
		case KeyEvent.VK_SUBTRACT:
		case KeyEvent.VK_DOWN:
		case KeyEvent.VK_MINUS:
			zoomout.actionPerformed(null);
			
			
		default:
			return false;
			// do nothing
		}

	
		// /* zoom in */
		// inputs.put(KeyStroke.getKeyStroke(KeyEvent.VK_ADD, 0),
		// "customZoomIn");
		// actions.put("customZoomIn", new AnnotationZoomInAction(model));
		// inputs.put(KeyStroke.getKeyStroke(KeyEvent.VK_SUBTRACT, 0),
		// "customZoomOut");
		// actions.put("customZoomOut", new AnnotationZoomOutAction(model));
		//
		// /* select first from selection */
		// inputs.put(KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD7, 0),
		// "customSelectFirst");
		// inputs.put(KeyStroke.getKeyStroke(KeyEvent.VK_HOME, 0),
		// "customSelectFirst");
		// actions.put("customSelectFirst", new SelectFromSelectedFirst(model));
		//
		// /* select last from selection */
		// inputs.put(KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD1, 0),
		// "customSelectLast");
		// inputs.put(KeyStroke.getKeyStroke(KeyEvent.VK_END, 0),
		// "customSelectLast");
		// actions.put("customSelectLast", new SelectFromSelectedLast(model));
		//
		// /* move selection one location forward */
		// inputs.put(KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD9, 0),
		// "customSelectForward");
		// inputs.put(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP, 0),
		// "customSelectForward");
		// actions
		// .put("customSelectForward",
		// new SelectFromSelectedForward(model));
		//
		// /* move selection one location back */
		// inputs.put(KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD3, 0),
		// "customSelectBack");
		// inputs.put(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN, 0),
		// "customSelectBack");
		// actions.put("customSelectBack", new SelectFromSelectedBack(model));

	}

}
