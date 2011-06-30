package com.example.helloworld;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import edu.cmu.sphinx.frontend.util.Microphone;
import edu.cmu.sphinx.recognizer.Recognizer;
import edu.cmu.sphinx.result.Result;
import edu.cmu.sphinx.util.props.ConfigurationManager;

public class SpeechListener implements Runnable {
	private IWorkbenchWindow window;

	public SpeechListener( IWorkbenchWindow w )
	{
		this.window = w;
	}
	
	public void run()
	{
        ConfigurationManager cm;

        cm = new ConfigurationManager( System.getenv("HOME") + "/workspace/com.example.helloworld/helloworld.config.xml" /*HelloWorld.class.getResource("helloworld.config.xml")*/ );

        System.out.println( "Here ");
        
        Recognizer recognizer = (Recognizer) cm.lookup("recognizer");
        recognizer.allocate();

        System.out.println( "Here2");

        // start the microphone or exit if the programm if this is not possible
        Microphone microphone = (Microphone) cm.lookup("microphone");
        if (!microphone.startRecording()) {
            System.out.println("Cannot start microphone.");
            recognizer.deallocate();
            System.exit(1);
        }

        System.out.println("Say: (Good morning | Hello) ( Bhiksha | Evandro | Paul | Philip | Rita | Will )");

        // loop the recognition until the programm exits.
        while (true) {
            System.out.println("Start speaking. Press Ctrl-C to quit.\n");

            Result result = recognizer.recognize();

            if (result != null) {
                String resultText = result.getBestFinalResultNoFiller();

                insertText( window, resultText );


                //System.out.println("You said: " + resultText + '\n');
            } else {
                System.out.println("I can't hear what you said.\n");
            }
        }
	}

	private static void insertText( final IWorkbenchWindow window, final String resultText )
	{
        //IWorkbench wb = PlatformUI.getWorkbench();
        //IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
        Display.getDefault().asyncExec(new Runnable() {
        	 public void run() {
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
        	            doc.replace(offset, 0, resultText+"\n");
        			} catch (BadLocationException e) {
        				// TODO Auto-generated catch block
        				e.printStackTrace();
        			}
        	 }
        });
	}
}
