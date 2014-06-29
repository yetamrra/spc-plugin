package com.example.helloworld;

/*
 * Copyright 2013-2014 Benjamin M. Gordon
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
	private int depth;
	
	public TextInsertOp( IWorkbenchWindow window, String insertText, Stack<TextInsertion> previousInserts, DialogNode context, int depth )
	{
		this.window = window;
		this.insertText = insertText;
		this.previousInserts = previousInserts;
		this.context = context;
		this.depth = depth;
	}
	
	@Override
	public void run()
	{
		boolean doGC = false;
		if ( insertText.equals("end function") ) {
			doGC = true;
		}
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
			StringBuilder b = new StringBuilder();
			for ( int i=0; i<depth; i++ ) {
				b.append( "  " );
			}
			b.append( insertText );
			insertText = b.toString();
			doc.replace(offset, selection.getLength(), insertText);
			editor.selectAndReveal( offset+insertText.length(), 0 );
			TextInsertion t = new TextInsertion( offset, insertText.length(), context );
			previousInserts.push( t );
			//editor.setHighlightRange( offset+insertText.length(), 0, true );
			System.out.println( "Insert: " + previousInserts );
			if ( doGC ) {
	        	// Try to request garbage collection after functions to reduce
	        	// large pauses
	        	System.gc();
			}
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
