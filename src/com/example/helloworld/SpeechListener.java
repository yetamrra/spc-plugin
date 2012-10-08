package com.example.helloworld;

import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
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
import edu.cmu.sphinx.frontend.util.AudioFileDataSource;
import edu.cmu.sphinx.jsgf.JSGFGrammarException;
import edu.cmu.sphinx.jsgf.JSGFGrammarParseException;
import edu.cmu.sphinx.result.Result;
import edu.cmu.sphinx.util.props.ConfigurationManager;
import edu.cmu.sphinx.util.props.PropertyException;
import edu.cmu.sphinx.util.props.PropertySheet;

public class SpeechListener implements Runnable, ResultListener {
	private IWorkbenchWindow window;
	private String configFile;
	private URL cfgURL;
	private URL audioURL;
	private ConfigurationManager configManager;
	private DialogManager dialogManager;

	public SpeechListener( IWorkbenchWindow w, String config )
	{
		this.window = w;
		this.configFile = config;
		this.setAudioURL(null);
		
		try {
			String filePath = "lib/" + configFile;
			URL cfgURL = Platform.getBundle( Activator.PLUGIN_ID ).getEntry(filePath);
			this.cfgURL = FileLocator.toFileURL( cfgURL );
			
			configManager = new ConfigurationManager( cfgURL );

	        dialogManager = (DialogManager)configManager.lookup("dialogManager");

	        dialogManager.addNode( "program", new SLBehavior() );
	        dialogManager.addNode( "function", new SLBehavior() );
	        
	        dialogManager.setInitialNode("program");

	        dialogManager.addResultListener( this );
	        
			dialogManager.allocate();

		}
		catch ( IOException e ) {
			System.out.println( "Cannot find " + configFile + ": " + e.getMessage() );
			return;
		}
	}
	
	public void run()
	{        

		try {	
	        if ( this.audioURL != null ) {
	        	AudioFileDataSource dataSource = (AudioFileDataSource) configManager.lookup("audioFileDataSource");
	        	dataSource.setAudioFile(audioURL, null);
	        }

            System.out.println("Running  ...");
            dialogManager.go();
            System.out.println("Cleaning up  ...");
			dialogManager.deallocate();
		} catch ( JSGFGrammarParseException e ) {
			System.err.println( "Caught parse exception while listening: " + e );
		} catch ( JSGFGrammarException e ) {
			System.err.println( "Caught grammar exception while listening: " + e );
		}
		return;
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
		String text = result.getBestFinalResultNoFiller(); 
		
		System.out.println( "Hypothesis: " + text );
		// FIXME: handle this better
		if ( text.length() > 0 && !text.equals("stop listening") ) {
			insertText( window, text );
		}
		
	}

	public URL getAudioURL() {
		return audioURL;
	}

	public void setAudioURL(URL audioURL) {
		this.audioURL = audioURL;
	}
}
