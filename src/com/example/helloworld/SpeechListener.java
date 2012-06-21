package com.example.helloworld;

import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

import edu.cmu.sphinx.decoder.ResultListener;
import edu.cmu.sphinx.frontend.util.Microphone;
import edu.cmu.sphinx.recognizer.Recognizer;
import edu.cmu.sphinx.result.Result;
import edu.cmu.sphinx.util.props.ConfigurationManager;
import edu.cmu.sphinx.util.props.PropertyException;
import edu.cmu.sphinx.util.props.PropertySheet;

public class SpeechListener implements Runnable, ResultListener {
	private IWorkbenchWindow window;

	public SpeechListener( IWorkbenchWindow w )
	{
		this.window = w;
	}
	
	public void run()
	{
		URL cfgPath;
		
		try {
			URL cfgURL = Platform.getBundle( Activator.PLUGIN_ID ).getEntry("lib/helloworld.config.xml");
			cfgPath = FileLocator.toFileURL( cfgURL );
		}
		catch ( IOException e ) {
			System.out.println( "Cannot find helloworld.config.xml: " + e.getMessage() );
			return;
		}
        
        ConfigurationManager cm = new ConfigurationManager( cfgPath );

        Recognizer recognizer = (Recognizer) cm.lookup("recognizer");
        recognizer.allocate();
        recognizer.addResultListener( this );

        // start the microphone or exit if the program if this is not possible
        Microphone microphone = (Microphone) cm.lookup("microphone");
        if (!microphone.startRecording()) {
            System.out.println("Cannot start microphone.");
            recognizer.deallocate();
            System.exit(1);
        }

        Display.getDefault().asyncExec( new Runnable() {
        	public void run() {
        		MessageDialog.openInformation(
        			window.getShell(),
        			"Listener is ready",
        			"Begin speaking"
        		);
        	}
        }
        );
        
		//MessageDialog.openInformation(
		//		window.getShell(),
		//		"Listener is ready",
		//		"Begin speaking");

        //System.out.println("Say: (Good morning | Hello) ( Bhiksha | Evandro | Paul | Philip | Rita | Will )");

        // loop the recognition until the program exits.
        while (true) {
            System.out.println("Start speaking. Press Ctrl-C to quit.\n");

            Result result = recognizer.recognize();

            if (result != null) {
                String resultText = result.getBestFinalResultNoFiller();

                insertText( window, resultText );


                System.out.println("You said: " + resultText + '\n');
            } else {
                System.out.println("I can't hear what you said.\n");
            }
        }
	}

	private static void insertText( final IWorkbenchWindow window, final String resultText )
	{
		if ( resultText.length() == 0 ) {
			return;
		}
		
		String tmp = resultText;
		tmp = tmp.replaceAll( "\\bzero\\b", "0" );
		tmp = tmp.replaceAll( "\\bone\\b", "1" );
		tmp = tmp.replaceAll( "\\btwo\\b", "2" );
		tmp = tmp.replaceAll( "\\bthree\\b", "3" );
		tmp = tmp.replaceAll( "\\bfour\\b", "4" );
		tmp = tmp.replaceAll( "\\bfive\\b", "5" );
		tmp = tmp.replaceAll( "\\bsix\\b", "6" );
		tmp = tmp.replaceAll( "\\bseven\\b", "7" );
		tmp = tmp.replaceAll( "\\beight\\b", "8" );
		tmp = tmp.replaceAll( "\\bnine\\b", "9" );
		tmp = tmp.replaceAll( "\\bten\\b", "10" );
		tmp = tmp.replaceAll( "\\bless than\\b", "<" );
		tmp = tmp.replaceAll( "\\bgreater than\\b", ">" );
		tmp = tmp.replaceAll( "\\bnot equals\\b", "!=" );
		tmp = tmp.replaceAll( "\\bequals\\b", "=" );
		
		final String insertText = tmp + " \n";
		
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
        	            doc.replace(offset, 0, insertText);
        	            editor.selectAndReveal( offset+insertText.length(), 0 );
        	            //editor.setHighlightRange( offset+insertText.length(), 0, true );
        			} catch (BadLocationException e) {
        				// TODO Auto-generated catch block
        				e.printStackTrace();
        			}
        	 }
        });
	}

	@Override
	public void newProperties(PropertySheet ps) throws PropertyException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void newResult(Result result) {
		String text = result.getBestResultNoFiller(); 
		
		System.out.println( "Hypothesis: " + text );
		
	}
}
