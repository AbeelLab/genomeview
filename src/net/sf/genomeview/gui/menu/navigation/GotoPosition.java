/**
 * %HEADER%
 */
package net.sf.genomeview.gui.menu.navigation;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import net.sf.genomeview.data.Model;
import net.sf.genomeview.gui.MessageManager;
import net.sf.genomeview.gui.external.ExternalHelper;
import net.sf.genomeview.gui.menu.AbstractModelAction;


public class GotoPosition extends AbstractModelAction {

    private static final long serialVersionUID = 3533852596396875672L;

    public GotoPosition(Model model) {
        super(MessageManager.getString("navigationmenu.goto_position"), model);
        super.putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("control G"));
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        String input = JOptionPane.showInputDialog(MessageManager.getString("navigationmenu.provide_coordination"));
        if (input != null&&input.trim().length()>0) {
        	try{
        		int i = Integer.parseInt(input.trim());
        		super.model.vlm.center(i);
            }catch(NumberFormatException e){
            	ExternalHelper.setPosition(input.trim(), model);
            }
            
        }

    }

}
