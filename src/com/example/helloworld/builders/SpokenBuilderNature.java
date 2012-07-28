package com.example.helloworld.builders;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;

public class SpokenBuilderNature implements IProjectNature
{
	private IProject project;
	
	public IProject getProject()
	{
		return project;
	}
	
	public void setProject( IProject project )
	{
		this.project = project;
	}

	@Override
	public void configure() throws CoreException
	{
		SpokenBuilder.addBuilderToProject( project );
	}

	@Override
	public void deconfigure() throws CoreException {
		SpokenBuilder.removeBuilderFromProject( project );
		// SpokenBuilder.removeMarkers(project);
	}
	
	
}
