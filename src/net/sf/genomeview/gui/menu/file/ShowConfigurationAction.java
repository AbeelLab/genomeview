/**
 * %HEADER%
 */
package net.sf.genomeview.gui.menu.file;

import java.awt.event.ActionEvent;

import net.sf.genomeview.data.Model;
import net.sf.genomeview.gui.MessageManager;
import net.sf.genomeview.gui.config.ConfigurationDialog;
import net.sf.genomeview.gui.menu.AbstractModelAction;


public class ShowConfigurationAction extends AbstractModelAction {

    private static final long serialVersionUID = -3394758959206359717L;

    public ShowConfigurationAction(Model model) {
        super(MessageManager.getString("filemenu.configuration"), model);
    }

    public void actionPerformed(ActionEvent arg0) {
        ConfigurationDialog.showConfigurationDialog(model);
        model.refresh();
    }

}
