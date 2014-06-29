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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.bxg.spokencompiler.SpokenCompiler;
import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;

import com.example.helloworld.Activator;

public class SpokenBuilder extends IncrementalProjectBuilder
{

	public static final String BUILDER_ID = Activator.PLUGIN_ID + ".SpokenBuilder";
	
	@Override
	protected IProject[] build(int kind, @SuppressWarnings("rawtypes") Map args, IProgressMonitor progress)
			throws CoreException 
	{
		List<IPath> files = new ArrayList<IPath>();
		
		IResourceDelta delta = getDelta( getProject() );
		if ( delta != null ) {
			IResourceDelta[] children = delta.getAffectedChildren();
			for ( int i=0; i<children.length; i++ ) {
				IResourceDelta child = children[i];
				String fileName = child.getFullPath().getFileExtension();
				if ( fileName != null && fileName.equals("spk") ) {
					files.add( child.getFullPath() );
				}
			}
		}
		
		if ( files.size() > 0 ) {
			buildSpokenFiles( files, progress );
		}
		
		return null;
	}

    protected void startupOnInitialize()
    {
        // add builder init logic here
    }
   
    protected void clean(IProgressMonitor monitor)
    {
        // add builder clean logic here
    }
    
    private void buildSpokenFiles( List<IPath> files, IProgressMonitor monitor )
    {
		SpokenCompiler spc = new SpokenCompiler();

    	for ( IPath file: files ) {
    		monitor.beginTask( "Compiling " + file, 1 );
    		
    		if ( checkCancel(monitor) ) {
    			return;
    		}
    		
    		boolean success = false;
    		try {
    			spc.compileFile( file.toOSString(), "StringTemplates.stg" );
    			success = true;
    		}
    		catch ( IOException e ) {
    			System.out.println( "Error compiling " + file + ": " + e.getMessage() );
    		}
    		
    		if ( !success ) {
    			System.out.println( "Build failed for " + file );
    		} else {
    			System.out.println( "Compiled " + file );
    		}
    		monitor.worked( 1 );
    		
    		monitor.done();
    	}
    }
    
    private boolean checkCancel( IProgressMonitor monitor )
    {
    	if ( monitor.isCanceled() ) {
    		throw new OperationCanceledException();
    	}
    	
    	if ( isInterrupted() ) {
    		return true;
    	}
    	
    	return false;
    }
    
    public static void addBuilderToProject( IProject project )
    {
    	// Cannot modify closed projects
    	if ( !project.isOpen() ) {
    		return;
    	}
    	
    	IProjectDescription description;
    	try {
    		description = project.getDescription();
    	}
    	catch ( CoreException e ) {
    		// FIXME: log error e
    		return;
    	}
    	
    	// Check if builder is already associated
    	ICommand[] cmds = description.getBuildSpec();
    	for ( ICommand c: cmds ) {
    		if ( c.getBuilderName().equals(BUILDER_ID) ) {
    			return;
    		}
    	}
    	
    	// Add builder to project
    	ICommand cmd = description.newCommand();
    	cmd.setBuilderName( BUILDER_ID );
    	List<ICommand> newCmds = new ArrayList<ICommand>();
    	newCmds.addAll( Arrays.asList(cmds) );
    	newCmds.add( cmd );
    	description.setBuildSpec( newCmds.toArray(new ICommand[newCmds.size()]) );
    	try {
    		project.setDescription( description, null );
    	}
    	catch ( CoreException e ) {
    		// FIXME: log error
    	}
    }
    
    public static void removeBuilderFromProject( IProject project )
    {
    	// Cannot modify closed projects
    	if ( !project.isOpen() ) {
    		return;
    	}
    	
    	IProjectDescription description;
    	try {
    		description = project.getDescription();
    	}
    	catch ( CoreException e ) {
    		// FIXME: log error e
    		return;
    	}
    	description.toString();
    }
}
