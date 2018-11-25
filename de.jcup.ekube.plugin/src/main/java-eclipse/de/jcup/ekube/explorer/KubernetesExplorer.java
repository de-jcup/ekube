package de.jcup.ekube.explorer;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.part.DrillDownAdapter;
import org.eclipse.ui.part.ViewPart;

import de.jcup.eclipse.commons.ui.EclipseUtil;
import de.jcup.ekube.Activator;
import de.jcup.ekube.core.EKubeConfiguration;
import de.jcup.ekube.core.EKubeConfigurationContext;
import de.jcup.ekube.core.fabric8io.Fabric8ioEKubeModelBuilder;
import de.jcup.ekube.core.model.EKubeModel;

/**
 * This sample class demonstrates how to plug-in a new workbench view. The view
 * shows data obtained from the model. The sample creates a dummy model on the
 * fly, but a real implementation would connect to the model available either in
 * this or another plug-in (e.g. the workspace). The view is connected to the
 * model using a content provider.
 * <p>
 * The view uses a label provider to define how model objects should be
 * presented in the view. Each view can present the same model objects using
 * different labels and icons, if needed. Alternatively, a single label provider
 * can be shared between views in order to ensure that objects of the same type
 * are presented in the same way everywhere.
 * <p>
 */

public class KubernetesExplorer extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "de.jcup.ekube.views.KubernetesExplorer";

	@Inject
	IWorkbench workbench;
	
	private TreeViewer viewer;
	private DrillDownAdapter drillDownAdapter;
	private Action switchContextAction;
	private Action infoAction;
	
	private Action expandAllAction;
	private Action collapseAllAction;
	
	private Action doubleClickAction;

	private KubernetesExplorerContentProvider contentPovider;

	@Override
	public void createPartControl(Composite parent) {
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		drillDownAdapter = new DrillDownAdapter(viewer);
		contentPovider = new KubernetesExplorerContentProvider(this);
		viewer.setContentProvider(contentPovider);
		viewer.setInput(getViewSite());
		viewer.setLabelProvider(new DelegatingStyledCellLabelProvider(new KubernetesExplorerViewLabelProvider()));

		// Create the help context id for the viewer's control
		// workbench.getHelpSystem().setHelp(viewer.getControl(),
		// "de.jcup.ekube.viewer");
		getSite().setSelectionProvider(viewer);
		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				KubernetesExplorer.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(switchContextAction);
		manager.add(new Separator());
		manager.add(infoAction);
		manager.add(new Separator());
		manager.add(expandAllAction);
		manager.add(collapseAllAction);

	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(switchContextAction);
		manager.add(infoAction);
		manager.add(new Separator());
		manager.add(expandAllAction);
		manager.add(collapseAllAction);
		manager.add(new Separator());
		drillDownAdapter.addNavigationActions(manager);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(switchContextAction);
		manager.add(infoAction);
		manager.add(new Separator());
		manager.add(expandAllAction);
		manager.add(collapseAllAction);
		drillDownAdapter.addNavigationActions(manager);
	}

	private void makeActions() {
		expandAllAction = new Action() {
			@Override
			public void run() {
				viewer.expandAll();
			}
		};
		expandAllAction.setText("Expand all");
		expandAllAction.setToolTipText("Expand all tree elements");
		expandAllAction.setImageDescriptor(EclipseUtil.createImageDescriptor("/icons/expandall.png",Activator.PLUGIN_ID));
		
		collapseAllAction = new Action() {
			@Override
			public void run() {
				viewer.collapseAll();
			}
		};
		collapseAllAction.setText("Collpase all");
		collapseAllAction.setToolTipText("Collapse all tree elements");
		collapseAllAction.setImageDescriptor(EclipseUtil.createImageDescriptor("/icons/collapseall.png",Activator.PLUGIN_ID));
		
		switchContextAction = new Action() {
			public void run() {
				
				EKubeConfiguration configuration = Activator.getDefault().getConfiguration();
				List<EKubeConfigurationContext> data = configuration.getConfigurationContextList();
				ElementListSelectionDialog dialog =
					    new ElementListSelectionDialog(Display.getCurrent().getActiveShell(), new EKubeConfigurationLabelProvider());
					dialog.setElements(data.toArray());
					dialog.setMultipleSelection(false);
					dialog.setTitle("Which context do you want to use ?");
					// user pressed cancel
					if (dialog.open() != Window.OK) {
					        return;
					}
					Object[] result = dialog.getResult();
					EKubeConfigurationContext context = (EKubeConfigurationContext) result[0];
					configuration.setCurrentContext(context.getName());
					
					connect(configuration);
			}
		};
		switchContextAction.setText("Switch");
		switchContextAction.setToolTipText("Switch context");
		switchContextAction.setImageDescriptor(
				PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_ETOOL_CLEAR));

		infoAction = new Action() {
			public void run() {
				StringBuilder sb = new StringBuilder();
				EKubeConfiguration config = Activator.getDefault().getConfiguration();
				sb.append("Info:\n-current context:"+config.getCurrentContext());
				sb.append("\nContexts available:");
				for (EKubeConfigurationContext contextConfig: config.getConfigurationContextList()){
					sb.append("\n+").append(contextConfig.getName()+", cluster="+contextConfig.getCluster()+", user:"+contextConfig.getUser());
				}
				showMessage(sb.toString());
			}
		};
		infoAction.setText("Info");
		infoAction.setToolTipText("Info about kubernetes configuration");
		infoAction.setImageDescriptor(
				PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		doubleClickAction = new Action() {
			public void run() {
				IStructuredSelection selection = viewer.getStructuredSelection();
				Object obj = selection.getFirstElement();
				showMessage("Double-click detected on " + obj.toString());
			}
		};
	}

	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				doubleClickAction.run();
			}
		});
	}

	private void showMessage(String message) {
		MessageDialog.openInformation(viewer.getControl().getShell(), "Kubernetes Explorer", message);
	}

	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}
	
	private class ConnectionJob extends Job{

		private EKubeConfiguration configuration;

		public ConnectionJob(EKubeConfiguration configuration ) {
			super("connect to kubernetes. contextg="+configuration.getCurrentContext());
			this.configuration=configuration;
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			monitor.beginTask("Fetch data from cluster", IProgressMonitor.UNKNOWN);
			Fabric8ioEKubeModelBuilder modelBuilder = new Fabric8ioEKubeModelBuilder(configuration, Activator.getDefault().getErrorHandler());
			EKubeModel model = modelBuilder.build();
			monitor.done();
				
			contentPovider.inputChanged(viewer, null, model);
			
			EclipseUtil.safeAsyncExec(()->viewer.refresh());
			
			return Status.OK_STATUS;
		}
		
	}

	public void connect(EKubeConfiguration configuration) {
		Job job = new ConnectionJob(configuration);
		job.setUser(true);
		job.schedule();
	}

}
