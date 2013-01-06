package com.example.helloworld;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.ITextEditor;

public class TextCorrectionOp implements Runnable
{
	private IWorkbenchWindow window;
	private TextInsertion oldOp;
	
	public TextCorrectionOp( IWorkbenchWindow window, TextInsertion oldOp )
	{
		this.window = window;
		this.oldOp = oldOp;
	}

	@Override
	public void run()
	{
		IWorkbenchPage page = window.getActivePage();
		IEditorPart part = page.getActiveEditor();
		if (!(part instanceof AbstractTextEditor) )
			return;
		ITextEditor editor = (ITextEditor)part;
		if ( oldOp != null ) {
			editor.selectAndReveal( oldOp.offset, oldOp.length );
			System.out.println( "Selected " + oldOp );
		} else {
			System.out.println( "Attempted correction with no previous insert" );
		}
	}

}
