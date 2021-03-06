/*
 * Copyright 2019 Albert Tregnaghi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 */
package de.jcup.ekube;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import de.jcup.eclipse.commons.PluginContextProvider;
import de.jcup.ekube.core.EKubeConfiguration;
import de.jcup.ekube.core.ErrorHandler;
import de.jcup.ekube.explorer.KubernetesExplorer;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin implements PluginContextProvider {

    // The plug-in ID
    public static final String PLUGIN_ID = "de.jcup.ekube"; //$NON-NLS-1$

    // The shared instance
    private static Activator plugin;

    private ColorManager colorManager;

    private ErrorHandler errorHandler;

    private EKubeConfiguration configuration;

    /**
     * The constructor
     */
    public Activator() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.
     * BundleContext)
     */
    public void start(BundleContext context) throws Exception {
        super.start(context);
        colorManager = new ColorManager();
        errorHandler = new EclipseKubernetesErrorHandler();
        configuration = new EclipseEKubeConfiguration();
        
        plugin = this;
    }

    public ColorManager getColorManager() {
        return colorManager;
    }

    public EKubeConfiguration getConfiguration() {
        return configuration;
    }

    public KubernetesExplorer getExplorer() {
        IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        IViewPart view = page.findView(KubernetesExplorer.ID);
        if (view instanceof KubernetesExplorer) {
            return (KubernetesExplorer) view;
        }
        throw new IllegalStateException("Did not find kubernetes explorer but:" + view);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.
     * BundleContext)
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

    /**
     * Returns an image descriptor for the image file at the given plug-in
     * relative path
     *
     * @param path
     *            the path
     * @return the image descriptor
     */
    public static ImageDescriptor getImageDescriptor(String path) {
        return imageDescriptorFromPlugin(PLUGIN_ID, path);
    }

    public ErrorHandler getErrorHandler() {
        return errorHandler;
    }

    @Override
    public AbstractUIPlugin getActivator() {
        return this;
    }

    @Override
    public String getPluginID() {
        return PLUGIN_ID;
    }

}
