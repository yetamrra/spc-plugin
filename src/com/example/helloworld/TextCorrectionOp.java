package com.example.helloworld;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.IDocumentProvider;
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
		IDocumentProvider dp = editor.getDocumentProvider();
		IDocument doc = dp.getDocument(editor.getEditorInput());
		ITextSelection selection =
				(ITextSelection) editor.getSelectionProvider().getSelection();

		if ( oldOp != null ) 
		{
			// Erase the old selection first if needed
			if ( selection.getLength() > 0 ) {
				try {
					doc.replace( selection.getOffset(), selection.getLength(), "" );
				} catch (BadLocationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			editor.selectAndReveal( oldOp.offset, oldOp.length );
			System.out.println( "Selected " + oldOp );
		} else {
			System.out.println( "Attempted correction with no previous insert" );
		}
	}

}
