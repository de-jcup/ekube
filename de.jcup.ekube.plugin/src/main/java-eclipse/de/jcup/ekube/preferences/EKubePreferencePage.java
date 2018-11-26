package de.jcup.ekube.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import de.jcup.ekube.Activator;

public class EKubePreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public EKubePreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Preferences for kubernetes communication");
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common GUI
	 * blocks needed to manipulate various types of preferences. Each field editor
	 * knows how to save and restore itself.
	 */
	public void createFieldEditors() {
		addField(
				new FileFieldEditor(EKubePreferenceConstants.KUBE_CONFIGFILE_PATH.getId(), "&KubeConfig:", getFieldEditorParent()));
		addField(new BooleanFieldEditor(EKubePreferenceConstants.FILTER_NAMESPACES_ENABLED.getId(), "&Filter namespaces enabled",
				getFieldEditorParent()));

//		addField(new RadioGroupFieldEditor(EKubePreferenceConstants.P_CHOICE, "An example of a multiple-choice preference",
//				1, new String[][] { { "&Choice 1", "choice1" }, { "C&hoice 2", "choice2" } }, getFieldEditorParent()));
		StringFieldEditor filteredNamespacesEditor = new StringFieldEditor(EKubePreferenceConstants.FILTERED_NAMESPACES.getId(), "Filtered namespaces", getFieldEditorParent());
		addField(filteredNamespacesEditor);
	}

	public void init(IWorkbench workbench) {
	}

}