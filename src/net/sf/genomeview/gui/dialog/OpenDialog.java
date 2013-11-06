/**
 * %HEADER%
 */
package net.sf.genomeview.gui.dialog;

import java.awt.HeadlessException;
import java.awt.Window;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileFilter;
import javax.xml.parsers.ParserConfigurationException;

import net.sf.genomeview.core.Configuration;
import net.sf.genomeview.core.Icons;
import net.sf.genomeview.data.DataSourceHelper;
import net.sf.genomeview.data.Model;
import net.sf.genomeview.data.NotificationTypes;
import net.sf.genomeview.data.ReadWorker;
import net.sf.genomeview.gui.MessageManager;
import net.sf.genomeview.gui.StaticUtils;
import net.sf.jannot.exception.ReadFailedException;
import net.sf.jannot.source.DataSource;
import net.sf.jannot.source.DataSourceFactory;
import net.sf.jannot.source.Locator;
import net.sf.jannot.source.das.DAS;
import net.sf.jannot.source.das.DAS.EntryPoint;

import org.genomespace.client.GsSession;
import org.genomespace.client.exceptions.AuthorizationException;
import org.genomespace.client.exceptions.ConfigurationError;
import org.genomespace.client.exceptions.InternalServerException;
import org.genomespace.client.exceptions.ServerNotFoundException;
import org.genomespace.client.ui.GSFileBrowserDialog;
import org.genomespace.client.ui.GSLoginDialog;
import org.genomespace.datamanager.core.GSFileMetadata;
import org.xml.sax.SAXException;

import be.abeel.gui.GridBagPanel;
import be.abeel.net.URIFactory;

/**
 * 
 * @author Thomas Abeel
 * 
 */
public class OpenDialog extends JDialog {

	private static final long serialVersionUID = -9176452114031190911L;

	private Model model;

	private final String[] exts = new String[] { "fasta", "fa", "fas", "embl", "fna", "gtf", "gff", "gff3", "maln",
			"syn", "wig", "mfa", "bed", "mapview", "bam", "maf", "snp", "tbl", "gb", "gbk", "pileup", "con", "peaks",
			"tdf","bw","bigwig" };


	public OpenDialog(Window parent, final Model model) {
		super(parent, "Load data", ModalityType.APPLICATION_MODAL);
		this.setIconImage(Icons.MINILOGO);
		this.model = model;
		setResizable(false);
		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				model.refresh(NotificationTypes.DIALOGCHANGE);
			}

		});
		final OpenDialog _self = this;
		// EventQueue.invokeLater(new Runnable() {
		//
		// @Override
		// public void run() {
		GridBagPanel gp = new GridBagPanel();
		_self.setContentPane(gp);
		JButton file = new JButton(MessageManager.getString("opendialog.local_files"), Icons.get("Hard Disk_48x48.png"));
		configButton(file);
		gp.add(file, gp.gc);
		gp.gc.gridx++;
		JButton url = new JButton(MessageManager.getString("opendialog.url"), Icons.get("Globe_48x48.png"));
		configButton(url);
		gp.add(url, gp.gc);
		gp.gc.gridx++;
		JButton genomespace = new JButton(MessageManager.getString("opendialog.genomespace"), Icons.get("genomespace.png"));
		configButton(genomespace);
		gp.add(genomespace, gp.gc);
		
//		JButton das = new JButton("DAS(exp)", Icons.get("das.png"));
//		configButton(das);
//		gp.add(das, gp.gc);

		/** Add logic to buttons */
		file.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				_self.dispose();
				JFileChooser chooser = new JFileChooser(Configuration.getFile("lastDirectory"));
				chooser.resetChoosableFileFilters();
				for (final String ext : exts) {
					chooser.addChoosableFileFilter(new FileFilter() {

						@Override
						public boolean accept(File f) {
							if (f.isDirectory())
								return true;

							if (f.getName().toLowerCase().endsWith(ext)
									|| f.getName().toLowerCase().endsWith(ext + ".gz")
									|| f.getName().toLowerCase().endsWith(ext + ".bgz")) {
								return true;
							}

							return false;
						}

						@Override
						public String getDescription() {
							return ext;
						}

					});
				}
				chooser.addChoosableFileFilter(new FileFilter() {

					@Override
					public boolean accept(File f) {
						if (f.isDirectory())
							return true;
						for (String ext : exts) {

							if (f.getName().toLowerCase().endsWith(ext)
									|| f.getName().toLowerCase().endsWith(ext + ".gz")
									|| f.getName().toLowerCase().endsWith(ext + ".bgz")) {
								return true;
							}
						}
						return false;
					}

					@Override
					public String getDescription() {
						return Arrays.toString(exts);
					}

				});

				chooser.setMultiSelectionEnabled(true);
				int returnVal = chooser.showOpenDialog(model.getGUIManager().getParent());
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File[] files = chooser.getSelectedFiles();
//					DataSource[] out = new DataSource[files.length];
					try {
						for (int i = 0; i < files.length; i++) {
						DataSourceHelper.load(model,new Locator(files[i].toString()));

						}
						Configuration.set("lastDirectory", files[0].getParentFile());
//						load(out);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}  catch (URISyntaxException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (ReadFailedException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
				}

			}
		});

		url.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				_self.dispose();
				try {
					String input = JOptionPane.showInputDialog(model.getGUIManager().getParent(),
							"Give the URL of the data");
					if (input != null && input.trim().length() > 0) {
						
						DataSourceHelper.load(model,new Locator(input.trim()));
					}

				}  catch (MalformedURLException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				} catch (IOException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				} catch (URISyntaxException e3) {
					// TODO Auto-generated catch block
					e3.printStackTrace();
				} catch (ReadFailedException e4) {
					// TODO Auto-generated catch block
					e4.printStackTrace();
				}

			}
		});

		genomespace.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				_self.dispose();
				try {
					    // open a GSLogin Dialog 
					GsSession session = null;
					    final GSLoginDialog loginDialog = new GSLoginDialog(null, ModalityType.APPLICATION_MODAL);
					    loginDialog.setVisible(true);
					    final String userName = loginDialog.getUsername();
					    final String password = loginDialog.getPassword();
					    if ((userName==null) || (userName.trim().length()==0))
					         JOptionPane.showMessageDialog(null, 
					        		 	MessageManager.getString("opendialog.empty_username"),
					        		 	MessageManager.getString("opendialog.login_error"),
					        		 	JOptionPane.ERROR_MESSAGE);
					    if ((password==null) || (password.trim().length()==0))
					         JOptionPane.showMessageDialog(null, 
					        		 	MessageManager.getString("opendialog.empty_password"),
					        		 	MessageManager.getString("opendialog.login_error"),
					        		 	JOptionPane.ERROR_MESSAGE);
					    	
			    	 session = loginDialog.getGsSession();
			    	 if (session==null)
			    		 session = new GsSession();
			    	 session.login(userName, password);
					//Once user is logged, list its working files
					GSFileBrowserDialog fileBrowser = new GSFileBrowserDialog(null, session.getDataManagerClient(), 
							GSFileBrowserDialog.DialogType.FILE_SELECTION_DIALOG); 
							fileBrowser.setVisible(true);
					GSFileMetadata selection = fileBrowser.getSelectedFileMetadata();
							// Do something with the selected file
					DataSourceHelper.load(model,new Locator(selection.getPath()));
			    } catch (final AuthorizationException e1) {
			         JOptionPane.showMessageDialog(null, MessageManager.getString("opendialog.invalid_username_pwd"),MessageManager.getString("opendialog.login_error"), 
			         JOptionPane.ERROR_MESSAGE);
			    }catch (ConfigurationError ce){
			         JOptionPane.showMessageDialog(null, MessageManager.getString("opendialog.connection_unavailable"),MessageManager.getString("opendialog.connection_error"), 
			         JOptionPane.ERROR_MESSAGE);			    	
				}catch (ServerNotFoundException e2) {					
			         JOptionPane.showMessageDialog(null, MessageManager.getString("opendialog.connection_error_retry"),MessageManager.getString("opendialog.connection_error"), 
			         JOptionPane.ERROR_MESSAGE);
				} catch (InternalServerException e3) {
			         JOptionPane.showMessageDialog(null, MessageManager.getString("opendialog.connection_error_retry"),MessageManager.getString("opendialog.connection_error"), 
			         JOptionPane.ERROR_MESSAGE);
				}  catch (MalformedURLException e2) {
			         JOptionPane.showMessageDialog(null, MessageManager.getString("opendialog.connection_error_retry"),MessageManager.getString("opendialog.connection_error"), 
			         JOptionPane.ERROR_MESSAGE);
				} catch (IOException e2) {
			         JOptionPane.showMessageDialog(null, MessageManager.getString("opendialog.connection_error_retry"),MessageManager.getString("opendialog.connection_error"), 
			         JOptionPane.ERROR_MESSAGE);
				} catch (URISyntaxException e3) {
			         JOptionPane.showMessageDialog(null, MessageManager.getString("opendialog.connection_error_retry"),MessageManager.getString("opendialog.connection_error"), 
			         JOptionPane.ERROR_MESSAGE);
				} catch (ReadFailedException e4) {
			         JOptionPane.showMessageDialog(null, MessageManager.getString("opendialog.connection_error_retry"),MessageManager.getString("opendialog.connection_error"), 
			         JOptionPane.ERROR_MESSAGE);
				} catch(Exception e5){
			         JOptionPane.showMessageDialog(null, MessageManager.getString("opendialog.invalid_username_pwd"),MessageManager.getString("opendialog.login_error"), 
			         JOptionPane.ERROR_MESSAGE);					
				}
			}
		});	
		
//		das.addActionListener(new ActionListener() {
//
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				_self.dispose();
//				try {
//					String url = JOptionPane.showInputDialog(model.getGUIManager().getParent(),
//							"Give the URL of the data").trim();
//					DAS das = new DAS(url);
//					List<String> refs = das.getReferences();
//					Collections.sort(refs);
//					String ref = (String) JOptionPane.showInputDialog(model.getGUIManager().getParent(),
//							"Select reference genome", "Reference selection", JOptionPane.INFORMATION_MESSAGE, null,
//							refs.toArray(), refs.get(0));
//					List<EntryPoint> eps = das.getEntryPoints(ref);
//					EntryPoint ep = (EntryPoint) JOptionPane.showInputDialog(model.getGUIManager().getParent(),
//							"Select entry point", "Entry point selection", JOptionPane.INFORMATION_MESSAGE, null,
//							eps.toArray(), eps.get(0));
//					das.setEntryPoint(ep);
//					das.setReference(ref);
//					das.
////					load(new DataSource[] { das });
//				} catch (HeadlessException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				} catch (MalformedURLException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				} catch (ParserConfigurationException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				} catch (SAXException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				} catch (IOException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				} catch (URISyntaxException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				}
//
//			}
//		});

		pack();
		StaticUtils.center(model.getGUIManager().getParent(),_self);
		setVisible(true);

		// }
		// });

	}

	private void configButton(JButton button) {
		button.setVerticalTextPosition(SwingConstants.BOTTOM);
		button.setHorizontalTextPosition(SwingConstants.CENTER);

	}

	// public void doSomething() {
	// Sources source = (Sources)
	// JOptionPane.showInputDialog(model.getGUIManager().getParent(),
	// "Select feature source", "Data selection",
	// JOptionPane.INFORMATION_MESSAGE, null, Sources.values(),
	// Sources.values()[0]);
	// if (source != null) {
	// DataSource[] data = DataMenu.create(source, model, new String[] {
	// "fasta", "fa", "fas", "embl", "fna",
	// "gtf", "gff", "gff3", "maln", "syn", "wig", "mfa", "bed", "mapview",
	// "bai", "maf", "snp", "tbl",
	// "gb", "gbk", "pileup", "con", "peaks" });
	// if (data != null) {
	// for (DataSource ds : data) {
	// final ReadWorker rw = new ReadWorker(ds, model);
	// rw.execute();
	// }
	//
	// }
	// }
	// }

}
