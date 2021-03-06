package org.bxg.spokencompiler.eclipse.handlers;

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

import java.io.File;
import java.io.IOException;

import org.bxg.spokencompiler.eclipse.SpeechListener;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

public class ProcessAudioFile extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if ( ! (selection instanceof IStructuredSelection) ) {
			return null;
		}
		IStructuredSelection sel = (IStructuredSelection)selection;
		if ( sel.size() != 1 ) {
			return null;
		}
		
		if ( !(sel.getFirstElement() instanceof IFile) ) {
			return null;			
		}
		IFile file = (IFile)sel.getFirstElement();
		if ( !file.getName().endsWith(".wav") ) {
			return null;
		}
		
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		try {
			SpeechListener l = new SpeechListener( window, "filelistener.config.xml" );
			String path = file.getLocation().toOSString();
			File f = new File(path);
			l.setAudioURL( f.toURI().toURL() );
			Thread t = new Thread( l );
			t.start();
		} catch (IOException e) {
			System.err.println( "Caught exception while processing " + file + ": " + e.getMessage() );
		}

		
		// TODO Auto-generated method stub
		return null;
	}

}
