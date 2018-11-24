package de.jcup.ekube.explorer;

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
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.DrillDownAdapter;
import org.eclipse.ui.part.ViewPart;

import de.jcup.ekube.Activator;
import de.jcup.ekube.EclipseKubernetesErrorHandler;
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
	private Action action1;
	private Action action2;
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
		manager.add(action1);
		manager.add(new Separator());
		manager.add(action2);
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(action1);
		manager.add(action2);
		manager.add(new Separator());
		drillDownAdapter.addNavigationActions(manager);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(action1);
		manager.add(action2);
		manager.add(new Separator());
		drillDownAdapter.addNavigationActions(manager);
	}

	private void makeActions() {
		action1 = new Action() {
			public void run() {
				viewer.refresh();
			}
		};
		action1.setText("Refresh");
		action1.setToolTipText("Full refresh");
		action1.setImageDescriptor(
				PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_ETOOL_CLEAR));

		action2 = new Action() {
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
		action2.setText("Action 2");
		action2.setToolTipText("Action 2 tooltip");
		action2.setImageDescriptor(
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
			
			Display display = Display.getCurrent();
			if (display==null){
				display=Display.getDefault();
			}
			display.asyncExec(()->viewer.refresh());
			
			return Status.OK_STATUS;
		}
		
	}

	public void connect(EKubeConfiguration configuration) {
		Job job = new ConnectionJob(configuration);
		job.setUser(true);
		job.schedule();
	}

}
