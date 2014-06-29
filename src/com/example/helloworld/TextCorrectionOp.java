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
