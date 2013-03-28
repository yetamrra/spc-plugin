package com.example.helloworld;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.speech.recognition.GrammarException;
import javax.speech.recognition.Rule;
import javax.speech.recognition.RuleGrammar;

import com.sun.speech.engine.recognition.BaseRecognizer;
import com.sun.speech.engine.recognition.BaseRuleGrammar;

import edu.cmu.sphinx.jsgf.JSGFGrammarException;
import edu.cmu.sphinx.jsgf.JSGFGrammarParseException;
import edu.cmu.sphinx.jsgf.JSGFRuleGrammar;
import edu.cmu.sphinx.jsgf.rule.JSGFRuleName;
import edu.cmu.sphinx.result.Result;
import edu.cmu.sphinx.util.TimerPool;

/**
 * Defines the standard behavior for a node. The standard behavior is: <ul> <li> On entry the set of sentences that can
 * be spoken is displayed. <li> On recognition if a tag returned contains the prefix 'dialog_' it indicates that control
 * should transfer to another dialog node. </ul>
 */
class SLBehavior extends NewGrammarDialogNodeBehavior {

    private Collection<String> sampleUtterances;


    /** Executed when we are ready to recognize */
    public void onReady() {
        super.onReady();
        help();
    }

    public void updateSymbols() throws JSGFGrammarException, IOException, JSGFGrammarParseException
    {
        BaseRecognizer recognizer = new BaseRecognizer(getGrammar().getGrammarManager());
        try {
            recognizer.allocate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        List<JSGFRuleName> imports = getGrammar().getRuleGrammar().getImports();
        boolean doImports = false;
        for ( JSGFRuleName name: imports ) {
        	if ( name.getRuleName().equals("statement.standard_statements") ) {
        		doImports = true;
        		break;
        	}
        }
        JSGFRuleGrammar jGram;
        if ( doImports ) {
        	jGram = getGrammar().getGrammarManager().retrieveGrammar( "statement" );
        } else {
        	jGram = getGrammar().getRuleGrammar();
        }
        RuleGrammar ruleGrammar = new BaseRuleGrammar(recognizer, jGram);
        
        // Replace the definedSymbols rule with a list of our defined symbols
        String symbols;
        if ( System.getProperty("org.bxg.spokencompiler.UseScoping") != null ) {
	        symbols = StringUtils.join( Scope.getCurrentScope().getSymbols(true), " | " );
        } else {
        	symbols = "<id>";
        }
        
        Rule newRule;
		try {
	        if ( symbols.length() > 0 ) {
				newRule = ruleGrammar.ruleForJSGF( symbols );
	        } else {
	        	newRule = ruleGrammar.ruleForJSGF( "<VOID>" );
	        } 
		} catch (GrammarException e) {
			throw new JSGFGrammarException( e.toString() );
		}
        ruleGrammar.setRule( "definedSymbols", newRule, false );
        ruleGrammar.setEnabled( "definedSymbols", true );
        getGrammar().commitChanges();
        grammarChanged();
        System.out.println( ruleGrammar );    	
    }
    
    @Override
    public void onEntry() throws IOException, JSGFGrammarParseException, JSGFGrammarException
    {
    	super.onEntry();
    	
    	updateSymbols();
    }
    
    /**
     * Displays the help message for this node. Currently we display the name of the node and the list of sentences that
     * can be spoken.
     */
    protected void help() {
        System.out.println(" ======== " + getGrammarName() + " =======");
        //dumpSampleUtterances();
        //System.out.println(" =================================");
    }


    /**
     * Executed when the recognizer generates a result. Returns the name of the next dialog node to become active, or
     * null if we should stay in this node
     *
     * @param result the recongition result
     * @return the name of the next dialog node or null if control should remain in the current node.
     */
    public String onRecognize(Result result) throws GrammarException {
    	if ( result == null ) {
    		// FIXME: handle properly
    		System.out.println( "Received null result" );
    		return "exit";
    	}
    	
        String tag = super.onRecognize(result);

        if (tag != null) {
            System.out.println("\n "
                    + result.getBestFinalResultNoFiller() + '\n');
            if (tag.equals("exit")) {
                System.out.println("Goodbye! Thanks for visiting!\n");
                return tag;
                //System.exit(0);
            }
            if (tag.equals("help")) {
                help();
            } else if (tag.equals("stats")) {
                TimerPool.dumpAll();
            } else if (tag.startsWith("goto_")) {
                return tag.replaceFirst("goto_", "");
            } else if (tag.startsWith("browse")) {
                execute(tag);
            } else if (tag.equals("out") ) {
            	return "out";
            } else if ( tag.equals("correction") ) {
            	return "correction";
            } else if (tag.equals("reset") ) {
            	return "reset";
            }
        } else {
            java.awt.Toolkit.getDefaultToolkit().beep();
            System.out.println("\n Oops! didn't hear you.\n");
            SpeechManager.getManager().setHypothesis( "*error*", false );
        }
        return null;
    }


    /**
     * execute the given command
     *
     * @param cmd the command to execute
     */
    private void execute(String cmd) {
        try {
            Runtime.getRuntime().exec(cmd);
        } catch (IOException e) {
            // if we can't run the command, just fall back to
            // a non-working demo.
        }
    }


    /**
     * Collects the set of possible utterances.
     * <p/>
     * TODO: Note the current implementation just generates a large set of random utterances and tosses away any
     * duplicates. There's no guarantee that this will generate all of the possible utterances. (yep, this is a hack)
     *
     * @return the set of sample utterances
     */
    private Collection<String> collectSampleUtterances() {
        Set<String> set = new HashSet<String>();
        for (int i = 0; i < 100; i++) {
            String s = getGrammar().getRandomSentence();
            if (!set.contains(s)) {
                set.add(s);
            }
        }

        List<String> sampleList = new ArrayList<String>(set);
        Collections.sort(sampleList);
        return sampleList;
    }


    /** Dumps out the set of sample utterances for this node */
    @SuppressWarnings("unused")
	private void dumpSampleUtterances() {
        if (sampleUtterances == null) {
            sampleUtterances = collectSampleUtterances();
        }

        for (String sampleUtterance : sampleUtterances) {
            System.out.println("  " + sampleUtterance);
        }
    }


    /** Indicated that the grammar has changed and the collection of sample utterances should be regenerated. */
    protected void grammarChanged() {
        sampleUtterances = null;
    }
}

