package de.jcup.ekube;

import java.io.File;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;

public class ResourceUtil {

	public IFile toIFile(File file) {
		IFileStore fileStore =
				              EFS.getLocalFileSystem().fromLocalFile(file);
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IFile[] fileResults = workspace.getRoot().findFilesForLocationURI(fileStore.toURI());
		if (fileResults!=null && fileResults.length>0){
			return fileResults[0];
		}
		fileResults = workspace.getRoot().findFilesForLocation(Path.fromOSString(file.getAbsolutePath())); 
		if (fileResults!=null && fileResults.length>0){
			return fileResults[0];
		}
		return null;
		
	}
}
