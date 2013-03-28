package br.com.orionsoft;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {
	
	/*
	 * Para importar o projeto framework.jar da Orion e 
	 * construir o plugin, deve-se seguir os seguintes passos:
	 * 1. Importar o framework.jar no projeto (Build Path -> Configure Build Path -> Add Jar)
	 * 2. Abrir o plugin.xml (com editor do Eclipse) e adicionar o jar na aba Runtime -> Classpath
	 * 3. Na aba Runtime -> Classpath, clicar em 'New' e adicionar o local "."
	 * 4. Na aba Build, marcar o framework.jar em Binary Build
	 * 5. Salvar alterações, clicar com o botão direito no projeto do plugin, selecionar PDE Tolls -> Update Classpath
	 */

	// The plug-in ID
	public static final String PLUGIN_ID = "orion-metadata"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;
	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

}
