package com.example.helloworld.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import com.example.helloworld.SpeechListener;
import com.example.helloworld.SpeechManager;

public class StartDictation extends AbstractHandler
{
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		SpeechListener l = new SpeechListener( window, "helloworld.config.xml" );
		SpeechManager.getManager().setListener( l );
		Thread t = new Thread( l );
		t.start();
		return null;
	}

}
