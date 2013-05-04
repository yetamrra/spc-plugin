package com.example.helloworld;

import java.io.IOException;
import java.net.URL;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchWindow;

import com.example.helloworld.DialogManager.DialogNode;

import edu.cmu.sphinx.frontend.util.AudioFileDataSource;
import edu.cmu.sphinx.jsgf.JSGFGrammarException;
import edu.cmu.sphinx.jsgf.JSGFGrammarParseException;
import edu.cmu.sphinx.result.Result;
import edu.cmu.sphinx.util.props.ConfigurationManager;
import edu.cmu.sphinx.util.props.PropertyException;
import edu.cmu.sphinx.util.props.PropertySheet;

public class SpeechListener implements Runnable, SLResultListener {
	private IWorkbenchWindow window;
	private String configFile;
	private URL cfgURL;
	private URL audioURL;
	private ConfigurationManager configManager;
	private DialogManager dialogManager;
	private Stack<TextInsertion> previousInserts;
	
	public SpeechListener( IWorkbenchWindow w, String config )
	{
		this.window = w;
		this.configFile = config;
		this.setAudioURL(null);
		
		try {
			String filePath = "lib/" + configFile;
			URL tmp = Platform.getBundle( Activator.PLUGIN_ID ).getEntry(filePath);
			this.cfgURL = FileLocator.toFileURL( tmp );
			
			configManager = new ConfigurationManager( cfgURL );

	        dialogManager = (DialogManager)configManager.lookup("dialogManager");

	        dialogManager.addNode( "program", new SLBehavior() );
	        dialogManager.addNode( "function", new SLBehavior() );
	        dialogManager.addNode( "while", new SLBehavior() );
	        dialogManager.addNode( "if_block", new SLBehavior() );
	        dialogManager.addNode( "else_block", new SLBehavior() );
	        dialogManager.addNode( "string", new SLBehavior() );
	        
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
	        	SpeechManager.getManager().setInteractive( false );
	        } else {
	        	SpeechManager.getManager().setInteractive( true );
	        }

            System.out.println("Running  ...");
            Scope.reset();
    		this.previousInserts = new Stack<TextInsertion>();	// new one every time even though it's static
            DialogManager.clearSavedStates();
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

	private void insertText( String resultText, DialogNode context, String tag, SLBehavior behavior )
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
		tmp = tmp.replaceAll( "\\beleven\\b", "11" );
		tmp = tmp.replaceAll( "\\btwenty\\b", "20" );
		tmp = tmp.replaceAll( "\\b(is )?less than or equal to\\b", "<=" );
		tmp = tmp.replaceAll( "\\b(is )?less than\\b", "<" );
		tmp = tmp.replaceAll( "\\b(is )?greater than or equal to\\b", ">=" );
		tmp = tmp.replaceAll( "\\b(is )?greater than\\b", ">" );
		tmp = tmp.replaceAll( "\\bnot equals\\b", "!=" );
		tmp = tmp.replaceAll( "\\bequals\\b", "=" );
		tmp = tmp.replaceAll( "\\bplus\\b", "+" );
		tmp = tmp.replaceAll( "\\bminus\\b", "-" );
		tmp = tmp.replaceAll( "\\btimes\\b", "*" );
		tmp = tmp.replaceAll( "\\bdivided by\\b", "/" );
		tmp = tmp.replaceAll( "\\bover\\b", "/" );
		
		String insertText = tmp + " \n";
		
		processTextAction( tmp, behavior );
		
        //IWorkbench wb = PlatformUI.getWorkbench();
        //IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
		int depth = DialogManager.getSavedStates().size() - 1;
		if ( tag != null && (tag.equals("out") || tag.equals("else_block")) ) {
			depth--;
		}
		TextInsertOp op = new TextInsertOp( window, insertText, previousInserts, context, depth );
        Display.getDefault().asyncExec( op );
	}

	void updateBehavior( SLBehavior behavior )
	{
		try {
			behavior.updateSymbols();
		} catch (JSGFGrammarException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSGFGrammarParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	void processTextAction( String text, SLBehavior behavior )
	{
		// Function definitions create the function in the current
		// scope and then their arguments in a new scope.
		Pattern p = Pattern.compile( "^define function (\\w+)" );
		Matcher m = p.matcher(text);
		if ( m.find() ) {
			String id = m.group(1);
			Scope par = Scope.getCurrentScope();
			Scope s = Scope.newScope();
			int args = 0;
			p = Pattern.compile( " taking arguments (\\w+)" );
			m = p.matcher( text.substring(m.end(1)) );
			if ( m.find() ) {
				args += 1;
				s.define( m.group(1), SymType.UNKNOWN );
				p = Pattern.compile( " and (\\w+)" );
				m = p.matcher( text.substring(m.end(1)) );
				while ( m.find() ) {
					s.define( m.group(1), SymType.UNKNOWN );
					args += 1;
				}
			}
			SymType funcType;
			switch ( args ) {
				case 0:
					funcType = SymType.FUNCTION0;
					break;
					
				case 1:
					funcType = SymType.FUNCTION1;
					break;
					
				case 2:
					funcType = SymType.FUNCTION2;
					break;
					
				case 3:
					funcType = SymType.FUNCTION3;
					break;
					
				case 4:
					funcType = SymType.FUNCTION4;
					break;
					
				case 5:
					funcType = SymType.FUNCTION5;
					break;
					
				default:
					funcType = SymType.FUNCTION;
					break;
			}
			par.define( id, funcType );
			updateBehavior( behavior );
			return;
		}
		
		// The end of a block ends the current scope
		p = Pattern.compile( "^end (function|while|if)" );
		m = p.matcher( text );
		if ( m.find() ) {
			Scope.popScope();
			updateBehavior( behavior );
			return;
		}
		
		// An else ends the current scope and starts a new one
		p = Pattern.compile( "^else" );
		m = p.matcher( text );
		if ( m.find() ) {
			Scope.popScope();
			Scope.newScope();
			updateBehavior( behavior );
			return;
		}
		
		// Use as an array makes it an array but doesn't 
		// define a new symbol.  It also makes the index an int.
		// Hitting this doesn't terminate the checks because
		// these can also trigger other statements below.
		p = Pattern.compile( "element (.*?) of (\\w+)" );
		m = p.matcher( text );
		while ( m.find() ) {
			String lhs = m.group(1);
			String rhs = m.group(2); 
			Scope s = Scope.getCurrentScope();
			s.setType( lhs, SymType.INT, true );
			s.setType( rhs, SymType.ARRAY, true );
		}
		
		// Use in an arithmetic expression or boolean comparison
		// makes it a number but doesn't define a new symbol
		// Hitting this doesn't terminate the checks because
		// these can also trigger other statements below.
		p = Pattern.compile( "(\\w+) ([-+*/<>]=?) (\\w+)" );
		m = p.matcher( text );
		if ( m.find() ) {
			String lhs = m.group(1);
			String rhs = m.group(3); 
			Scope s = Scope.getCurrentScope();
			s.setType( lhs, SymType.INT, true );
			s.setType( rhs, SymType.INT, true );
		}
		
		// A scalar assignment creates a new variable in the current scope
		// if it doesn't already exist
		p = Pattern.compile( "^set (\\w+) to (.*)" );
		m = p.matcher( text );
		if ( m.find() ) {
			String id = m.group(1);
			Scope s = Scope.getCurrentScope();
			if ( !s.isDefined(id,true) ) {
				String val = m.group(2).trim();
				SymType type = SymType.UNKNOWN;
				if ( val.matches("\\d+") ) {
					type = SymType.INT;
				} else if ( val.matches("element .* of .*") ) {
					// FIXME: This is cheating.  It only works because none
					// of the sample programs have arrays of strings.
					type = SymType.INT;
				} else if ( val.matches("the string") ) {
					type = SymType.STRING;
				} else if ( val.matches(".*\\s[+/*-]\\s.*") ) {
					// FIXME: Can we do this better than just looking for
					// operators?
					type = SymType.INT;
				} else {
					// FIXME: Try to type this
				}
				s.define(id, type);
			}
			updateBehavior( behavior );
			return;
		}
		
		// An array assignment creates a new variable in the current scope
		// if it doesn't already exist
		p = Pattern.compile( "^set element \\w+ of (\\w+) to" );
		m = p.matcher( text );
		if ( m.find() ) {
			String id = m.group(1);
			Scope s = Scope.getCurrentScope();
			if ( !s.isDefined(id,true) ) {
				// FIXME: Try to type the array
				s.define(id, SymType.ARRAY);
			}
			updateBehavior( behavior );
			return;
		}
		
		// Reading a scalar variable creates it if it doesn't exist
		p = Pattern.compile( "^read (\\w+)" );
		m = p.matcher( text );
		if ( m.find() ) {
			String id = m.group(1);
			Scope s = Scope.getCurrentScope();
			if ( !s.isDefined(id,true) ) {
				s.define(id, SymType.UNKNOWN);
			}
			updateBehavior( behavior );
			return;
		}

		// Reading an array variable creates it if it doesn't exist
		p = Pattern.compile( "^read element \\w+ of (\\w+)" );
		m = p.matcher( text );
		if ( m.find() ) {
			String id = m.group(1);
			Scope s = Scope.getCurrentScope();
			if ( !s.isDefined(id,true) ) {
				s.define(id, SymType.ARRAY);
			}
			updateBehavior( behavior );
			return;
		}
		
		// Hitting an if-then or a while-do creates a new scope
		p = Pattern.compile( "^if .* then" );
		m = p.matcher( text );
		if ( m.find() ) {
			Scope.newScope();
			updateBehavior( behavior );
			return;
		}
		p = Pattern.compile( "^while .* do" );
		m = p.matcher( text );
		if ( m.find() ) {
			Scope.newScope();
			updateBehavior( behavior );
			return;
		}
	}
	
	@Override
	public void newProperties(PropertySheet ps) throws PropertyException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void newResult(Result result) {
		// FIXME: should this ever happen?
		assert( false );
	}

	public URL getAudioURL() {
		return audioURL;
	}

	public void setAudioURL(URL audioURL) {
		this.audioURL = audioURL;
	}

	@Override
	public String newResult(Result result, DialogNode context, String tag, SLBehavior behavior) {
		// FIXME: handle null better
		if ( result == null ) {
			return "exit";
		}
		
		boolean isFinal = result.isFinal();
		String hyp = result.getBestResultNoFiller();
		String text = (result == null) ? "" : result.getBestFinalResultNoFiller();
		
		if ( !isFinal || !text.equals(hyp) || text.equals("") ) {
            SpeechManager.getManager().setHypothesis( hyp, false );
		} else {
			SpeechManager.getManager().setHypothesis( text, true );
		}
		String nnTag = (tag == null) ? "" : tag;
		System.out.println( (isFinal ? "Final" : "Intermediate") + " hypothesis: " + text );

		String newTag;
		
		if ( nnTag.equals("exit") ) {
			newTag = "exit";
			
		} else if ( nnTag.equals("correction") ) {
			if ( previousInserts.size() > 0 ) {
				TextInsertion lastInsertion = previousInserts.pop();
				TextCorrectionOp op = new TextCorrectionOp( window, lastInsertion );
		        Display.getDefault().asyncExec( op );
		        if ( previousInserts.size() == 0 || lastInsertion.context == previousInserts.peek().context ) {
		        	newTag = "";
		        } else {
		        	newTag = "out";
		        }
		        newTag = "correction";
			} else {
				newTag = "";
			}
	      
		} else if ( nnTag.equals("reset") ) {
            TextClearOp op = new TextClearOp( window );
            Display.getDefault().asyncExec( op );

			newTag = "reset";
			
		} else {
			if ( text.length() > 0 ) {
				insertText( text, context, tag, behavior );
				if ( nnTag.length() > 0 ) {
					newTag = nnTag;
				} else {
					newTag = "inserted";
				}
			} else {
				if ( !SpeechManager.getManager().isInteractive() ) {
					insertText( "*missed utterance*", context, null, behavior );
					newTag = "inserted";
				} else {
					newTag = null;
				}
			}
		}
		
		return newTag;
	}
}
