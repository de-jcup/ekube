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
package de.jcup.ekube.explorer.action;

import java.util.List;
import java.util.Set;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.commands.ActionHandler;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.handlers.CollapseAllHandler;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.views.framelist.BackAction;
import org.eclipse.ui.views.framelist.ForwardAction;
import org.eclipse.ui.views.framelist.FrameAction;
import org.eclipse.ui.views.framelist.FrameList;
import org.eclipse.ui.views.framelist.GoIntoAction;
import org.eclipse.ui.views.framelist.UpAction;

import de.jcup.eclipse.commons.ui.EclipseUtil;
import de.jcup.ekube.Activator;
import de.jcup.ekube.core.EKubeConfiguration;
import de.jcup.ekube.core.EKubeContextConfigurationEntry;
import de.jcup.ekube.core.model.CurrentContextContainer;
import de.jcup.ekube.core.model.EKubeActionIdentifer;
import de.jcup.ekube.core.model.EKubeContainer;
import de.jcup.ekube.core.model.EKubeElement;
import de.jcup.ekube.core.model.SecretElement;
import de.jcup.ekube.explorer.CompositeActionGroup;
import de.jcup.ekube.explorer.EKubeSwitchContextConfigurationLabelProvider;
import de.jcup.ekube.explorer.EclipseDebugSettings;
import de.jcup.ekube.explorer.KubernetesExplorer;

/* adopted from PackageExplorerActionGroup*/
public class KubernetesExplorerActionGroup extends CompositeActionGroup {

    private static final String FRAME_ACTION_SEPARATOR_ID = "FRAME_ACTION_SEPARATOR_ID"; //$NON-NLS-1$
    private static final String FRAME_ACTION_GROUP_ID = "FRAME_ACTION_GROUP_ID"; //$NON-NLS-1$

    KubernetesExplorer explorer;

    private FrameList frameList;
    private GoIntoAction fZoomInAction;
    private BackAction backAction;
    private ForwardAction forwardAction;
    private UpAction upAction;
    private boolean fFrameActionsShown;

    private Action switchContextAction;
    private Action reloadKubeConfigAction;
    private Action infoAction;

    private Action expandAllAction;
    private Action collapseAllAction;

    private Action showMetaInfoAsYamlAction;
    private CommonDeleteAction commonDeleteAction;
    private ShowPodLogAction showLogOutputAction;
    private ShowSecretBase64DecodedAction showSecretdata;
    private ShowPlantUMLAction showPlantUMLAction;

    public KubernetesExplorerActionGroup(KubernetesExplorer part) {
        super();
        explorer = part;
        fFrameActionsShown = false;
        TreeViewer viewer = part.getTreeViewer();
        showMetaInfoAsYamlAction = new ShowYamlInfoAction(this);
        showMetaInfoAsYamlAction.setText(EKubeActionIdentifer.SHOW_YAML.getLabel() + " (double click)");

        showPlantUMLAction = new ShowPlantUMLAction(this);
        showPlantUMLAction.setText("Show plantuml");
        showPlantUMLAction.setImageDescriptor(EclipseUtil.createImageDescriptor("/icons/show-plantuml.png", Activator.PLUGIN_ID));

        showSecretdata = new ShowSecretBase64DecodedAction(this);
        showSecretdata.setText("Show secret data");
        showSecretdata.setImageDescriptor(EclipseUtil.createImageDescriptor("/icons/model/secret.gif", Activator.PLUGIN_ID));

        showLogOutputAction = new ShowPodLogAction(this);
        showLogOutputAction.setText(EKubeActionIdentifer.FETCH_LOGS.getLabel());

        commonDeleteAction = new CommonDeleteAction(this);
        commonDeleteAction.setText(EKubeActionIdentifer.DELETE.getLabel());
        commonDeleteAction.setImageDescriptor(EclipseUtil.createImageDescriptor("/icons/delete.png", Activator.PLUGIN_ID));
        
        KubernetesFrameSource frameSource = new KubernetesFrameSource(explorer);
        frameList = new FrameList(frameSource);
        frameSource.connectTo(frameList);
        fZoomInAction = new GoIntoAction(frameList);
        explorer.getSite().getSelectionProvider().addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                fZoomInAction.update();
            }
        });

        backAction = new BackAction(frameList);
        forwardAction = new ForwardAction(frameList);
        upAction = new UpAction(frameList);
        frameList.addPropertyChangeListener(new IPropertyChangeListener() { // connect
                                                                            // after
                                                                            // the
                                                                            // actions
                                                                            // (order
                                                                            // of
                                                                            // property
                                                                            // listener)
            @Override
            public void propertyChange(PropertyChangeEvent event) {
                explorer.updateTitle();
                explorer.updateToolbar();
            }
        });

        createExpandAllAction(viewer);
        createCollapseAllAction(viewer);
        createSwitchContextAction(viewer);
        createReloadAction();
        createInfoAction();

    }

    protected void createExpandAllAction(TreeViewer viewer) {
        expandAllAction = new Action() {
            @Override
            public void run() {
                Object element = explorer.getFirstSelectedElement();
                if (element == null) {
                    viewer.expandAll();
                } else {
                    viewer.expandToLevel(element, TreeViewer.ALL_LEVELS);
                }
            }
        };
        expandAllAction.setText("Expand all");
        expandAllAction.setToolTipText("Expand all tree elements");
        expandAllAction.setImageDescriptor(EclipseUtil.createImageDescriptor("/icons/expandall.png", Activator.PLUGIN_ID));
    }

    protected void createCollapseAllAction(TreeViewer viewer) {
        collapseAllAction = new Action() {
            @Override
            public void run() {
                Object element = explorer.getFirstSelectedElement();
                if (element == null) {
                    viewer.collapseAll();
                } else {
                    viewer.collapseToLevel(element, TreeViewer.ALL_LEVELS);
                }
            }
        };
        collapseAllAction.setText("Collpase all");
        collapseAllAction.setToolTipText("Collapse all tree elements");
        collapseAllAction.setImageDescriptor(EclipseUtil.createImageDescriptor("/icons/collapseall.png", Activator.PLUGIN_ID));
    }

    protected void createSwitchContextAction(TreeViewer viewer) {
        switchContextAction = new Action() {
            public void run() {
                explorer.loadconfiguration(true);

                EKubeConfiguration configuration = Activator.getDefault().getConfiguration();
                List<EKubeContextConfigurationEntry> data = configuration.getConfigurationContextList();
                if (data.isEmpty()) {
                    MessageDialog.openWarning(viewer.getControl().getShell(), "Not connected", "No information about contexts to choose available.\n\nPlease connect to kubernetes before!");
                    return;
                }
                ElementListSelectionDialog dialog = new ElementListSelectionDialog(Display.getCurrent().getActiveShell(), new EKubeSwitchContextConfigurationLabelProvider());
                dialog.setElements(data.toArray());
                dialog.setMultipleSelection(false);
                dialog.setTitle("Which context do you want to use ?");
                // user pressed cancel
                if (dialog.open() != Window.OK) {
                    return;
                }
                Object[] result = dialog.getResult();
                EKubeContextConfigurationEntry context = (EKubeContextConfigurationEntry) result[0];
                configuration.setKubernetesContext(context.getName());

                explorer.connect(configuration);
            }
        };
        switchContextAction.setText("Switch context");
        switchContextAction.setToolTipText("Switch kubernetes current context for ekube.\nWill NOT change your kube config file!");
        switchContextAction.setImageDescriptor(EclipseUtil.createImageDescriptor("/icons/switch-context.png", Activator.PLUGIN_ID));
    }

    protected void createReloadAction() {
        reloadKubeConfigAction = new Action() {
            public void run() {
                /* always load - no matter if already loaded or not */
                explorer.loadconfiguration(false);
            }
        };
        reloadKubeConfigAction.setText("Reload kube config");
        reloadKubeConfigAction.setToolTipText("Reloads kube config file from configured location (see preferences)");
        reloadKubeConfigAction.setImageDescriptor(EclipseUtil.createImageDescriptor("/icons/reload-kube-config.gif", Activator.PLUGIN_ID));
    }

    protected void createInfoAction() {
        infoAction = new Action() {
            public void run() {
                StringBuilder sb = new StringBuilder();
                EKubeConfiguration config = Activator.getDefault().getConfiguration();
                sb.append("Info:\n-current context:" + config.getKubernetesContext());
                sb.append("\nContexts available:");
                for (EKubeContextConfigurationEntry contextConfig : config.getConfigurationContextList()) {
                    sb.append("\n+").append(contextConfig.getName() + ", cluster=" + contextConfig.getCluster() + ", user:" + contextConfig.getUser());
                }
                explorer.showMessage(sb.toString());
            }
        };
        infoAction.setText("Info");
        infoAction.setToolTipText("Info about kubernetes configuration");
        infoAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
    }

    public Action getShowMetaInfoAsYamlAction() {
        return showMetaInfoAsYamlAction;
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    @Override
    public void fillActionBars(IActionBars actionBars) {
        super.fillActionBars(actionBars);
        setGlobalActionHandlers(actionBars);
        fillToolBar(actionBars.getToolBarManager());
        fillLocalPullDown(actionBars.getMenuManager());
    }

    private void fillLocalPullDown(IMenuManager manager) {
        manager.add(switchContextAction);
        manager.add(reloadKubeConfigAction);
        manager.add(new Separator());
        manager.add(infoAction);
        manager.add(new Separator());
        manager.add(expandAllAction);
        manager.add(collapseAllAction);

    }

    private void setGlobalActionHandlers(IActionBars actionBars) {
        // Navigate Go Into and Go To actions.
        actionBars.setGlobalActionHandler(IWorkbenchActionConstants.GO_INTO, fZoomInAction);
        actionBars.setGlobalActionHandler(ActionFactory.BACK.getId(), backAction);
        actionBars.setGlobalActionHandler(ActionFactory.FORWARD.getId(), forwardAction);
        actionBars.setGlobalActionHandler(IWorkbenchActionConstants.UP, upAction);
        // actionBars.setGlobalActionHandler(IWorkbenchActionConstants.GO_TO_RESOURCE,
        // fGotoResourceAction);

        IHandlerService handlerService = explorer.getViewSite().getService(IHandlerService.class);
        // handlerService.activateHandler(IWorkbenchCommandConstants.NAVIGATE_TOGGLE_LINK_WITH_EDITOR,
        // new ActionHandler(fToggleLinkingAction));
        handlerService.activateHandler(CollapseAllHandler.COMMAND_ID, new ActionHandler(collapseAllAction));
    }

    /* package */ void fillToolBar(IToolBarManager toolBar) {
        toolBar.add(switchContextAction);
        toolBar.add(reloadKubeConfigAction);
        toolBar.add(infoAction);
        toolBar.add(new Separator());
        toolBar.add(expandAllAction);
        toolBar.add(collapseAllAction);
        toolBar.add(new Separator());
        toolBar.add(showPlantUMLAction);
        toolBar.add(new Separator());

        if (backAction.isEnabled() || upAction.isEnabled() || forwardAction.isEnabled()) {
            toolBar.add(backAction);
            toolBar.add(forwardAction);
            toolBar.add(upAction);
            toolBar.add(new Separator(FRAME_ACTION_SEPARATOR_ID));
            fFrameActionsShown = true;
        }
        toolBar.add(new GroupMarker(FRAME_ACTION_GROUP_ID));

        if (EclipseDebugSettings.isShowingDebugActions()) {
            Action refreshTreeUIAction = new Action("DEBUG: Refresh complete tree ui") {

                @Override
                public void run() {
                    explorer.getTreeViewer().refresh();
                }
            };
            refreshTreeUIAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_ETOOL_CLEAR));
            toolBar.add(refreshTreeUIAction);
        }
        // toolBar.add(fToggleLinkingAction);
        toolBar.update(true);
    }

    public void updateToolBar(IToolBarManager toolBar) {

        boolean hasBeenFrameActionsShown = fFrameActionsShown;
        fFrameActionsShown = backAction.isEnabled() || upAction.isEnabled() || forwardAction.isEnabled();
        if (fFrameActionsShown != hasBeenFrameActionsShown) {
            if (hasBeenFrameActionsShown) {
                toolBar.remove(backAction.getId());
                toolBar.remove(forwardAction.getId());
                toolBar.remove(upAction.getId());
                toolBar.remove(FRAME_ACTION_SEPARATOR_ID);
            } else {
                toolBar.prependToGroup(FRAME_ACTION_GROUP_ID, new Separator(FRAME_ACTION_SEPARATOR_ID));
                toolBar.prependToGroup(FRAME_ACTION_GROUP_ID, upAction);
                toolBar.prependToGroup(FRAME_ACTION_GROUP_ID, forwardAction);
                toolBar.prependToGroup(FRAME_ACTION_GROUP_ID, backAction);
            }
            toolBar.update(true);
        }
    }

    @Override
    public void fillContextMenu(IMenuManager manager) {

        IStructuredSelection selection = (IStructuredSelection) getContext().getSelection();
        int size = selection.size();
        Object element = selection.getFirstElement();

        manager.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));

        if (!(element instanceof EKubeElement)) {
            return;
        }
        EKubeElement eke = (EKubeElement) element;
        if (element instanceof SecretElement) {
            manager.add(showSecretdata);
        }
        if (commonDeleteAction.canDelete(eke)) {
            manager.add(commonDeleteAction);
        }
        Set<EKubeActionIdentifer<?>> actions = eke.getExecutableActionIdentifiers();
        for (EKubeActionIdentifer<?> action : actions) {
            if (!action.isVisibleForUser()) {
                continue;
            }
            if (EKubeActionIdentifer.SHOW_YAML.equals(action)) {
                manager.add(showMetaInfoAsYamlAction);
            } else if (EKubeActionIdentifer.FETCH_LOGS.equals(action)) {
                manager.add(showLogOutputAction);
            } else {
                Action uiAction = createActionForIdentifier(eke, action);
                manager.add(uiAction);
            }

        }
        manager.add(switchContextAction);
        manager.add(reloadKubeConfigAction);
        manager.add(infoAction);
        manager.add(new Separator());
        manager.add(upAction);
        manager.add(backAction);
        manager.add(forwardAction);
        // Other plug-ins can contribute there actions here
        manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));

        addGotoMenu(manager, element, size);

        manager.add(new Separator());
        manager.add(expandAllAction);
        manager.add(collapseAllAction);

        super.fillContextMenu(manager);
    }

    public Action createActionForIdentifier(EKubeElement eke, EKubeActionIdentifer<?> action) {
        Action uiAction = new Action() {
            @Override
            public void run() {
                Object result = eke.execute(action);
                if (action.isRefreshNecessary()) {
                    if (eke.getParent() == null && !(eke instanceof CurrentContextContainer)) {
                        /*
                         * in this case the element is removed from tree - so just do an refresh on ui
                         */
                        explorer.getTreeViewer().refresh(true);
                    }
                    explorer.refreshTreeElelement(eke);
                }
                if (result instanceof String) {

                }
            }
        };
        uiAction.setText(action.getLabel());
        uiAction.setImageDescriptor(action.getImageDescriptor());
        return uiAction;
    }

    private void addGotoMenu(IMenuManager menu, Object element, int size) {
        boolean enabled = size == 1 && explorer.getTreeViewer().isExpandable(element) && (isGoIntoTarget(element) || element instanceof EKubeContainer);
        fZoomInAction.setEnabled(enabled);
        if (enabled) {
            menu.appendToGroup(IWorkbenchActionConstants.MB_ADDITIONS, fZoomInAction);
        }
    }

    private boolean isGoIntoTarget(Object element) {
        if (element instanceof EKubeContainer) {
            return true;
        }
        return false;
    }

    public FrameAction getUpAction() {
        return upAction;
    }

    public FrameAction getBackAction() {
        return backAction;
    }

    public FrameAction getForwardAction() {
        return forwardAction;
    }

    public FrameList getFrameList() {
        return frameList;
    }

}
