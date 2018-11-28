package de.jcup.ekube.explorer;

import java.io.File;
import java.io.FileWriter;
import java.util.Date;
import java.util.List;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.Path;
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
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.handlers.CollapseAllHandler;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.ide.IDE;
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
import de.jcup.ekube.core.model.EKubeActionIdentifer;
import de.jcup.ekube.core.model.EKubeContainer;
import de.jcup.ekube.core.model.EKubeElement;

/* adopted from PackageExplorerActionGroup*/
class KubernetesExplorerActionGroup extends CompositeActionGroup {

	private static final String FRAME_ACTION_SEPARATOR_ID = "FRAME_ACTION_SEPARATOR_ID"; //$NON-NLS-1$
	private static final String FRAME_ACTION_GROUP_ID = "FRAME_ACTION_GROUP_ID"; //$NON-NLS-1$

	private KubernetesExplorer explorer;

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

	private Action doubleClickAction;

	public KubernetesExplorerActionGroup(KubernetesExplorer part) {
		super();
		explorer = part;
		fFrameActionsShown = false;
		TreeViewer viewer = part.getTreeViewer();

		// IPropertyChangeListener workingSetListener= new
		// IPropertyChangeListener() {
		// @Override
		// public void propertyChange(PropertyChangeEvent event) {
		// doWorkingSetChanged(event);
		// }
		// };

		// IWorkbenchPartSite site = explorer.getSite();
		// setGroups(new ActionGroup[] {
		// new NewWizardsActionGroup(site),
		// fNavigateActionGroup= new NavigateActionGroup(explorer),
		// new CCPActionGroup(explorer),
		// new GenerateBuildPathActionGroup(explorer),
		// new GenerateActionGroup(explorer),
		// fRefactorActionGroup= new RefactorActionGroup(explorer),
		// new ImportActionGroup(explorer),
		// new BuildActionGroup(explorer),
		// new JavaSearchActionGroup(explorer),
		// fProjectActionGroup= new ProjectActionGroup(explorer),
		// fViewActionGroup= new ViewActionGroup(explorer.getRootMode(),
		// workingSetListener, site),
		// fCustomFiltersActionGroup= new CustomFiltersActionGroup(explorer,
		// viewer),
		// new LayoutActionGroup(explorer)
		// });

		// fViewActionGroup.fillFilters(viewer);

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
		createDoubleClickAction();

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
		expandAllAction
				.setImageDescriptor(EclipseUtil.createImageDescriptor("/icons/expandall.png", Activator.PLUGIN_ID));
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
		collapseAllAction
				.setImageDescriptor(EclipseUtil.createImageDescriptor("/icons/collapseall.png", Activator.PLUGIN_ID));
	}

	protected void createSwitchContextAction(TreeViewer viewer) {
		switchContextAction = new Action() {
			public void run() {
				explorer.loadconfiguration(true);

				EKubeConfiguration configuration = Activator.getDefault().getConfiguration();
				List<EKubeContextConfigurationEntry> data = configuration.getConfigurationContextList();
				if (data.isEmpty()) {
					MessageDialog.openWarning(viewer.getControl().getShell(), "Not connected",
							"No information about contexts to choose available.\n\nPlease connect to kubernetes before!");
					return;
				}
				ElementListSelectionDialog dialog = new ElementListSelectionDialog(
						Display.getCurrent().getActiveShell(), new EKubeSwitchContextConfigurationLabelProvider());
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
		switchContextAction
				.setToolTipText("Switch kubernetes current context for ekube.\nWill NOT change your kube config file!");
		switchContextAction.setImageDescriptor(
				EclipseUtil.createImageDescriptor("/icons/switch-context.png", Activator.PLUGIN_ID));
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
		reloadKubeConfigAction.setImageDescriptor(
				EclipseUtil.createImageDescriptor("/icons/reload-kube-config.gif", Activator.PLUGIN_ID));
	}

	protected void createInfoAction() {
		infoAction = new Action() {
			public void run() {
				StringBuilder sb = new StringBuilder();
				EKubeConfiguration config = Activator.getDefault().getConfiguration();
				sb.append("Info:\n-current context:" + config.getKubernetesContext());
				sb.append("\nContexts available:");
				for (EKubeContextConfigurationEntry contextConfig : config.getConfigurationContextList()) {
					sb.append("\n+").append(contextConfig.getName() + ", cluster=" + contextConfig.getCluster()
							+ ", user:" + contextConfig.getUser());
				}
				explorer.showMessage(sb.toString());
			}
		};
		infoAction.setText("Info");
		infoAction.setToolTipText("Info about kubernetes configuration");
		infoAction.setImageDescriptor(
				PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
	}

	protected void createDoubleClickAction() {
		doubleClickAction = new Action() {
			public void run() {
				Object obj = explorer.getFirstSelectedElement();
				StringBuilder sb = new StringBuilder();
				sb.append("Double-click detected on " + obj.toString());
				sb.append("\n+++++++++++++++++++++++++++++++++++++++++++++++++++++\n");
				if (obj instanceof EKubeElement) {
					EKubeElement eelement = (EKubeElement) obj;
					String info = eelement.execute(EKubeActionIdentifer.GRAB_STRING_INFO);
					if (info != null) {
						try {

							File tmpfile = File.createTempFile("ekube_info_", ".yaml");
							tmpfile.deleteOnExit();

							try (FileWriter fw = new FileWriter(tmpfile)) {
								StringBuilder yaml = new StringBuilder();
								yaml.append("# ---------------------------------------------------------------------------\n");
								yaml.append("# EKube info about : ").append(eelement.getClass().getSimpleName()).append(" '").append(eelement.getLabel()).append("'\n");
								yaml.append("# Timestamp        : ").append(explorer.getDateFormat().format(new Date())).append("\n");
								yaml.append("# ---------------------------------------------------------------------------\n");
								yaml.append(info);
								fw.write(yaml.toString());
								fw.close();
								/* loading temporary file from outside eclipse workspace :*/
								IFileStore fileStore =  EFS.getLocalFileSystem().getStore(new Path(tmpfile.getAbsolutePath()));
								
								IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
										.getActivePage();
								IDE.openEditorOnFileStore(page, fileStore);
								return;
							}
						} catch (Exception e) {
							Activator.getDefault().getErrorHandler().logError("Was not able to get yaml", e);
							sb.append("Failed:");
							sb.append(e.getClass());
							sb.append("\nMessage:");
							sb.append(e.getMessage());
							sb.append("\n");
						}
					} else {
						sb.append("Element/Container does NOT contain meta information");
					}
					explorer.showMessage(sb.toString());
				}

			}
		};
	}

	public Action getDoubleClickAction() {
		return doubleClickAction;
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
		if (backAction.isEnabled() || upAction.isEnabled() || forwardAction.isEnabled()) {
			toolBar.add(backAction);
			toolBar.add(forwardAction);
			toolBar.add(upAction);
			toolBar.add(new Separator(FRAME_ACTION_SEPARATOR_ID));
			fFrameActionsShown = true;
		}
		toolBar.add(new GroupMarker(FRAME_ACTION_GROUP_ID));

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

	// ---- Context menu
	// -------------------------------------------------------------------------

	@Override
	public void fillContextMenu(IMenuManager manager) {

		IStructuredSelection selection = (IStructuredSelection) getContext().getSelection();
		int size = selection.size();
		Object element = selection.getFirstElement();

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

	private void addGotoMenu(IMenuManager menu, Object element, int size) {
		boolean enabled = size == 1 && explorer.getTreeViewer().isExpandable(element)
				&& (isGoIntoTarget(element) || element instanceof EKubeContainer);
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
