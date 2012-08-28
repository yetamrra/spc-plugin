package com.example.helloworld.actions;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import com.example.helloworld.SpeechListener;

public class ProcessAudioFile implements IWorkbenchWindowActionDelegate {

	private IWorkbenchWindow window;
	private IFile selectedFile;
	
	@Override
	public void run(IAction action) {
		if ( this.selectedFile == null ) {
			return;
		}

		try {
			SpeechListener l = new SpeechListener( window, "filelistener.config.xml" );
			String path = selectedFile.getLocation().toOSString();
			File f = new File(path);
			l.setAudioURL( f.toURI().toURL() );
			Thread t = new Thread( l );
			t.start();
		} catch (IOException e) {
			System.err.println( "Caught exception while processing " + selectedFile + ": " + e.getMessage() );
		}
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		this.selectedFile = null;
		
		if ( ! (selection instanceof IStructuredSelection) ) {
			action.setEnabled( false );
			return;
		}
		IStructuredSelection sel = (IStructuredSelection)selection;
		if ( sel.size() != 1 ) {
			action.setEnabled( false );
			return;
		}
		
		if ( !(sel.getFirstElement() instanceof IFile) ) {
			action.setEnabled( false );
			return;			
		}
		IFile file = (IFile)sel.getFirstElement();
		if ( !file.getName().endsWith(".wav") ) {
			action.setEnabled( false );
			return;
		}
		
		this.selectedFile = file;
		action.setEnabled( true );
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(IWorkbenchWindow window) {
		this.window = window;
		this.selectedFile = null;
	}

}
