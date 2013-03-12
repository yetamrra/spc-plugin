package com.example.helloworld.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import com.example.helloworld.SpeechManager;

public class RestartDictation extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		SpeechManager.getManager().stopListener();
		return null;
	}

}
