package org.bxg.spokencompiler.eclipse.builders;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

public class BuilderVisitor implements IResourceVisitor, IResourceDeltaVisitor {

	private List<IPath> myFiles = new ArrayList<IPath>();
	private IJavaProject project;
	private IPath binDir;

	public BuilderVisitor(IProject proj)
	{
		binDir = null;
		
		try {
			if (proj.hasNature(JavaCore.NATURE_ID)) {
				project = JavaCore.create(proj);
				binDir = project.getOutputLocation();
			} else {
				project = null;
			}
		} 
		catch (CoreException e) {
			System.err.println("Caught error checking project type: " + e.getMessage());
			project = null;
		}
	}
	
	@Override
	public boolean visit(IResource resource) throws CoreException
	{
		IPath filePath = resource.getFullPath();
		if (binDir != null && binDir.equals(filePath)) {
			return false;
		} else {
			String extension = filePath.getFileExtension();
			if ( extension != null && extension.equals("spk") ) {
				myFiles.add( filePath );
			}
		}
		return true;
	}

	public List<IPath> files()
	{
		return myFiles;
	}

	@Override
	public boolean visit(IResourceDelta delta) throws CoreException {
		switch (delta.getKind()) {
			case IResourceDelta.ADDED:
			case IResourceDelta.CHANGED:
			case IResourceDelta.CONTENT:
				IPath filePath = delta.getFullPath();
				if (binDir != null && binDir.equals(filePath)) {
					return false;
				} else {
					String extension = filePath.getFileExtension();
					if ( extension != null && extension.equals("spk") ) {
						myFiles.add( filePath );
					}
				}
				break;
				
			default:
				break;
		}
		
		return true;
	}
}
