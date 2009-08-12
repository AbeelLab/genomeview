/**
 * %HEADER%
 */
package net.sf.genomeview.plugin;

import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Iterator;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import net.sf.genomeview.core.Configuration;
import net.sf.genomeview.data.IModel;

import org.java.plugin.JpfException;
import org.java.plugin.ObjectFactory;
import org.java.plugin.PluginLifecycleException;
import org.java.plugin.PluginManager;
import org.java.plugin.PluginManager.PluginLocation;
import org.java.plugin.registry.Extension;
import org.java.plugin.registry.PluginDescriptor;
import org.java.plugin.standard.StandardPluginLocation;


public class PluginLoader {

    private static Logger log = Logger.getLogger(PluginLoader.class.getCanonicalName());

    public static PluginManager pluginManager = null;

    public static void load(IModel model) {
        pluginManager = ObjectFactory.newInstance().createManager();
        try {

            /* Load core plugin */
            PluginLocation coreLocation;
            URL manifest = PluginLoader.class.getResource("/plugin.xml");
            System.err.println("Core plugin manifest: " + manifest);
            String jar;

            if (manifest.toString().startsWith("jar")) { // normal usage
                jar = manifest.toString().substring(4, manifest.toString().lastIndexOf('/') - 1);
            } else
                // developer usage
                jar = manifest.toString().substring(0, manifest.toString().lastIndexOf('/'));
            URL context = new URI(jar).toURL();
            System.err.println("Core plugin context: " + context);
            coreLocation = new StandardPluginLocation(context, manifest);
            pluginManager.publishPlugins(new PluginLocation[] { coreLocation });

            /* Load all other plugins */
            File pluginsDir = Configuration.getPluginDirectory();

            File[] plugins = pluginsDir.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.toLowerCase().endsWith(".zip") || name.toLowerCase().endsWith(".jar");
                }

            });
            try {

                PluginLocation[] locations = new PluginLocation[plugins.length];

                for (int i = 0; i < plugins.length; i++) {
                    locations[i] = StandardPluginLocation.create(plugins[i]);
                }

                pluginManager.publishPlugins(locations);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            StringBuffer errorMessage = new StringBuffer();
            for (PluginDescriptor pd : pluginManager.getRegistry().getPluginDescriptors()) {
                try {
                    log.info("Loading plugin " + pd);
                    pluginManager.activatePlugin(pd.getId());

                    Iterator<Extension> it = pd.getExtensions().iterator();
                    while (it.hasNext()) {

                        Extension ext = (Extension) it.next();
                        ClassLoader classLoader = pluginManager
                                .getPluginClassLoader(ext.getDeclaringPluginDescriptor());

                        Class<?> toolCls = classLoader.loadClass(ext.getParameter("class").valueAsString());

                        IPlugin tool = (IPlugin) toolCls.newInstance();

                        tool.init(model);
                    }
                    // }
                } catch (PluginLifecycleException e) {
                    String name=pd.getPluginClassName();
                    name=name.substring(name.lastIndexOf('.')+1);
                    if (e.getMessage().contains("incompatible version")) {
                        errorMessage.append("The " + name
                                + " plugin is not compatible with the current version of GenomeView\n");
                    }
                    if (e.getMessage().contains("can't start")) {
                        errorMessage.append("The " + name + " plugin can't be started.\n");
                    }
                    log.severe("Cannot load " + pd + " " + e.getMessage());
                } catch (InstantiationException e) {
					throw new RuntimeException(e);
				} catch (IllegalAccessException e) {
					throw new RuntimeException(e);
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

            }
            if (errorMessage.length() > 0) {
                errorMessage.append("\nTo fix this, please update your plugins to the latest version");
                JOptionPane.showMessageDialog(model.getParent(), errorMessage,"Plugin error!",JOptionPane.ERROR_MESSAGE);
            }

        
        } catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JpfException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
