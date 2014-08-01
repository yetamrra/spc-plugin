package org.bxg.spokencompiler.eclipse.builders;

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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.bxg.spokencompiler.CompileException;
import org.bxg.spokencompiler.SpokenCompiler;
import org.bxg.spokencompiler.eclipse.Activator;
import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;

public class SpokenBuilder extends IncrementalProjectBuilder
{

	public static final String BUILDER_ID = Activator.PLUGIN_ID + ".SpokenBuilder";
	
	@Override
	protected IProject[] build(int kind, @SuppressWarnings("rawtypes") Map args, IProgressMonitor progress)
			throws CoreException 
	{
		BuilderVisitor bv = new BuilderVisitor( getProject() );
		IResourceDelta delta = getDelta( getProject() );
		if ( delta != null ) {
			delta.accept(bv);
		} else {
			getProject().accept(bv);
		}
		List<IPath> files = bv.files();
		
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

		IWorkspace w = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = w.getRoot();

    	for ( IPath file: files ) {
    		monitor.beginTask( "Compiling " + file, 3 );
    		
    		if ( checkCancel(monitor) ) {
    			return;
    		}
    		
    		boolean success = false;
    		try {
    			// Generate java code
    			IFile filePath = root.getFile(file);
    			spc.parseFile( filePath.getRawLocation().toOSString() );
    			monitor.worked( 1 );
    			String javaCode = spc.generateCode( "/SLJavaEmitter.stg" );
    			monitor.worked( 1 );

    			// Write out to a new .java resource and let Eclipse compile it
    			IPath javaPath = file.removeFileExtension().addFileExtension("java");
    			IFile javaFile = root.getFile(javaPath);
    			InputStream stream = new ByteArrayInputStream(javaCode.getBytes(StandardCharsets.UTF_8));
    			if ( !javaFile.exists() ) {
    				javaFile.create(stream, IResource.FORCE|IResource.DERIVED, monitor);
    			} else {
    				javaFile.setContents(stream, IResource.FORCE, monitor);
    			}
    			
    			success = true;
    		}
    		catch ( IOException e ) {
    			System.out.println( "Error compiling " + file + ": " + e.getMessage() );
    		}
    		catch ( CompileException e ) {
    			System.out.println( "Error compiling " + file + ": " + e.getMessage() );
    		}
    		catch ( CoreException e ) {
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
