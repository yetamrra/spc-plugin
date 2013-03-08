package com.example.helloworld.handlers;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import com.example.helloworld.SpeechListener;

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
