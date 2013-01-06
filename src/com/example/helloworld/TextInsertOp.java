package com.example.helloworld;

import java.util.Stack;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

import com.example.helloworld.DialogManager.DialogNode;

public class TextInsertOp implements Runnable
{
	private IWorkbenchWindow window;
	private String insertText;
	private Stack<TextInsertion> previousInserts;
	private DialogNode context;
	
	public TextInsertOp( IWorkbenchWindow window, String insertText, Stack<TextInsertion> previousInserts, DialogNode context )
	{
		this.window = window;
		this.insertText = insertText;
		this.previousInserts = previousInserts;
		this.context = context;
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
		try {
			int offset = selection.getOffset(); //doc.getLineOffset(doc.getNumberOfLines()-4);
			doc.replace(offset, selection.getLength(), insertText);
			editor.selectAndReveal( offset+insertText.length(), 0 );
			TextInsertion t = new TextInsertion( offset, insertText.length(), context );
			previousInserts.push( t );
			//editor.setHighlightRange( offset+insertText.length(), 0, true );
			System.out.println( "Insert: " + previousInserts );
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
