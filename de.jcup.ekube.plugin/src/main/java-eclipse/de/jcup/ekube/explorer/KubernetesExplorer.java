package de.jcup.ekube.explorer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DecoratingStyledCellLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDecorationContext;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionContext;
import org.eclipse.ui.part.ViewPart;

import de.jcup.eclipse.commons.ui.EclipseUtil;
import de.jcup.ekube.EclipseEKubeContext;
import de.jcup.ekube.KubeConfigLoader;
import de.jcup.ekube.core.EKubeConfiguration;
import de.jcup.ekube.core.fabric8io.Fabric8ioEKubeModelBuilder;
import de.jcup.ekube.core.model.EKubeActionIdentifer;
import de.jcup.ekube.core.model.EKubeContainer;
import de.jcup.ekube.core.model.EKubeElement;
import de.jcup.ekube.core.model.EKubeModel;

public class KubernetesExplorer extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "de.jcup.ekube.views.KubernetesExplorer";

	@Inject
	IWorkbench workbench;

	private TreeViewer viewer;

	private EKubeElementTreeContentProvider contentPovider;
	private KubernetesExplorerActionGroup actionSet;

	private KubeConfigLoader configLoader;
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
	
	
	SimpleDateFormat getDateFormat() {
		return dateFormat;
	}
	
	private class InternalDecoratingStyledCellLabelProvider extends DecoratingStyledCellLabelProvider implements ILabelProvider{

		public InternalDecoratingStyledCellLabelProvider(IStyledLabelProvider labelProvider, ILabelDecorator decorator,
				IDecorationContext decorationContext) {
			super(labelProvider, decorator, decorationContext);
		}

		@Override
		public String getText(Object element) {
			if (element instanceof EKubeElement){
				EKubeElement eelement = (EKubeElement) element;
				return eelement.getLabel();
			}
			return null;
		}
	}
	@Override
	public void createPartControl(Composite parent) {
		configLoader = new KubeConfigLoader();

		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		
		contentPovider = new EKubeElementTreeContentProvider(this);
		viewer.setContentProvider(contentPovider);
		viewer.setInput(getViewSite());
		viewer.getControl().addKeyListener(new KeyAdapter() {
          @Override
        public void keyPressed(KeyEvent event) {
        	  if (event.keyCode == SWT.F5) {
        		  Object element = getFirstSelectedElement();
        		  if (element instanceof EKubeElement){
        			  EKubeElement eke = (EKubeElement) element;
        			  actionSet.createActionForIdentifier(eke, EKubeActionIdentifer.REFRESH).run();
        		  }
        	  }
        }  
        });
		
		ILabelDecorator decorator = PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator();
		EKubeElementLabelProvider kubernesExplorerLabelProvider = new EKubeElementLabelProvider();

		viewer.setLabelProvider(new InternalDecoratingStyledCellLabelProvider(kubernesExplorerLabelProvider, decorator, null));

		// Create the help context id for the viewer's control
		// workbench.getHelpSystem().setHelp(viewer.getControl(),
		// "de.jcup.ekube.viewer");
		getSite().setSelectionProvider(viewer);
		makeActions();
		initFrameActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();

	}

	private void initFrameActions() {
		actionSet.getUpAction().update();
		actionSet.getBackAction().update();
		actionSet.getForwardAction().update();
	}
	
	public Object getFirstSelectedElement() {
		ISelection selection = getSelection();
		if (selection instanceof IStructuredSelection){
			return ((IStructuredSelection) selection).getFirstElement();
		}
		return null;
	}
	
	private ISelection getSelection() {
		return viewer.getSelection();
	}
	
	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				actionSet.setContext(new ActionContext(getSelection()));
				actionSet.fillContextMenu(manager);
				actionSet.setContext(null);
				
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		actionSet.fillActionBars(bars);
	}

	private void makeActions() {
		actionSet = new KubernetesExplorerActionGroup(this);
	}

	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				actionSet.getShowMetaInfoAsYamlAction().run();
			}
		});
	}

	void showMessage(String message) {
		MessageDialog.openInformation(viewer.getControl().getShell(), "Kubernetes Explorer", message);
	}

	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	private class ConnectionJob extends Job {

		public ConnectionJob(EKubeConfiguration configuration) {
			super("connect to kubernetes - use context:" + configuration.getKubernetesContext());
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			monitor.beginTask("Fetch data from cluster", IProgressMonitor.UNKNOWN);
			
			Fabric8ioEKubeModelBuilder modelBuilder = new Fabric8ioEKubeModelBuilder();
			EclipseEKubeContext context = new EclipseEKubeContext(monitor);
			EKubeModel model = modelBuilder.build(context);
			
			monitor.done();

			EclipseUtil.safeAsyncExec(()-> refreshTreeInSWTThread(model));

			return Status.OK_STATUS;
		}

	}
	
	private void refreshTreeInSWTThread(EKubeModel model){
		contentPovider.inputChanged(viewer, null, model);
		viewer.refresh();
		viewer.expandToLevel(model.getCurrentContext(), 1);
	}

	public void connect(EKubeConfiguration configuration) {
		Job job = new ConnectionJob(configuration);
		job.setUser(true);
		job.schedule();
	}

	public TreeViewer getTreeViewer() {
		return viewer;
	}

	public String getFrameName(Object input) {
		if (input instanceof EKubeElement){
			EKubeElement eelement  = (EKubeElement) input;
			return eelement.getLabel();
		}else{
			return null;
		}
	}
	
	@Override
	public void dispose() {
		super.dispose();
		
		if (actionSet != null){
			actionSet.dispose();
		}
	}


	void updateToolbar() {
		IActionBars actionBars= getViewSite().getActionBars();
		actionSet.updateToolBar(actionBars.getToolBarManager());
	}

	/**
	 * Updates the title text and title tool tip.
	 * Called whenever the input of the viewer changes.
	 */
	void updateTitle() {
		Object input= viewer.getInput();
		String inputText= createTitleName(input);
		if (inputText == null) {
			setContentDescription(""); //$NON-NLS-1$
			setTitleToolTip(""); //$NON-NLS-1$
		} else {
			setContentDescription(inputText);
			setTitleToolTip(inputText);
		}
	}

	private String createTitleName(Object input) {
		if (input instanceof EKubeElement){
			EKubeElement eelement  = (EKubeElement) input;
			
			StringBuilder sb = new StringBuilder();
			List<String> list = new ArrayList<>();
			list.add(eelement.getLabel());
			EKubeContainer parent = eelement.getParent();
			while (parent!=null){
				list.add(parent.getLabel());
				parent = parent.getParent();
			}
			Collections.reverse(list);
			sb.append(list.toString());
			return sb.toString();
			
		}else{
			return "";
		}
	}

	void loadconfiguration(boolean lazy) {
		if (!configLoader.isLoaded() || !lazy) {
			configLoader.load();
		}
	}

	public String getToolTipText(Object input) {
		return getFrameName(input);
	}

	public void refreshTreeElelement(EKubeElement kubeElement) {
		viewer.refresh(kubeElement);
	}
}
