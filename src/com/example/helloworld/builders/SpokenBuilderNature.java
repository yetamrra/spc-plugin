package com.example.helloworld.builders;

/*
 * Copyright 2012-2014 Benjamin M. Gordon
 * 
 * This file is part of the spoken compiler Eclipse plugin.
 *
 * The spoken compiler Eclipse plugin is free software: 
 * you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The spoken compiler Eclipse plugin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the spoken compiler Eclipse plugin.
 * If not, see <http://www.gnu.org/licenses/>.
 */

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
