package com.example.helloworld.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

import com.example.helloworld.SpeechManager;

public class RestartDictation extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		SpeechManager.getManager().stopListener();
		
		IEditorPart part = HandlerUtil.getActiveEditor(event);
		if ( part != null && part instanceof AbstractTextEditor ) {
			ITextEditor editor = (ITextEditor)part;
			IDocumentProvider dp = editor.getDocumentProvider();
			IDocument doc = dp.getDocument(editor.getEditorInput());
			doc.set( "" );
		}
		
		return null;
		/*
		String commandId = "com.example.helloworld.commands.startDictation";
		IWorkbenchSite site = HandlerUtil.getActiveSite(event);
		Command command = ((ICommandService) site.getService(ICommandService.class)).getCommand(commandId);
		final Event trigger = new Event();
		ExecutionEvent executionEvent = ((IHandlerService) site.getService(IHandlerService.class)).createExecutionEvent(command, trigger);
		try {
			command.executeWithChecks(executionEvent);
		} catch (NotDefinedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotEnabledException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotHandledException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;*/
	}

}
