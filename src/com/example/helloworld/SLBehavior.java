package com.example.helloworld;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
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
        String undefSym;
        if ( System.getProperty("org.bxg.spokencompiler.UseScoping") != null ) {
        	Set<ProgSym> curSyms = Scope.getCurrentScope().getSymbols(true);
	        symbols = StringUtils.setToAlternates( curSyms );
	        Set<ProgSym> s = Scope.getCurrentScope().getUnusedSymbols();
	        undefSym = StringUtils.setToAlternates( s );
        } else {
        	symbols = "<id>";
        	undefSym = "<id>";
        }

        // Define rules for functions of various arity
        String function0args;
        String function1args;
        String function2args;
        String function3args;
        String unusedFunction;
        if ( System.getProperty("org.bxg.spokencompiler.UseTyping") != null ) {
        	function0args = StringUtils.join( Scope.getCurrentScope().getDefinedSymbolsOfType(SymType.FUNCTION0,false), " | " );
        	function1args = StringUtils.join( Scope.getCurrentScope().getDefinedSymbolsOfType(SymType.FUNCTION1,false), " | " );
        	function2args = StringUtils.join( Scope.getCurrentScope().getDefinedSymbolsOfType(SymType.FUNCTION2,false), " | " );
        	function3args = StringUtils.join( Scope.getCurrentScope().getDefinedSymbolsOfType(SymType.FUNCTION3,false), " | " );

        	// List of unused function symbols
        	Set<ProgSym> s = Scope.getLegalSymbols();
        	Set<ProgSym> f = Scope.getCurrentScope().getFunctions(-1) ;
        	s.removeAll( f );
        	unusedFunction = StringUtils.join( s, " | " );
        } else {
        	function0args = symbols;
        	function1args = symbols;
        	function2args = symbols;
        	function3args = symbols;
        	unusedFunction = symbols;
        }

        
        // Define rules for variable expressions
        String nonFunction;
        String possibleInt;
        String possibleString;
        String definedInts;
        String definedStrings;
        String possibleArray;
        String definedArrays;
        if ( System.getProperty("org.bxg.spokencompiler.UseTyping") != null ) {
        	nonFunction = unusedFunction;
        	Scope s = Scope.getCurrentScope();
        	definedInts = StringUtils.setToAlternates( s.getDefinedSymbolsOfType(SymType.INT,true) );
        	possibleInt = StringUtils.setToAlternates( s.getPossibleSymbolsOfType(SymType.INT) );
        	definedStrings = StringUtils.setToAlternates( s.getDefinedSymbolsOfType(SymType.STRING,true) );
        	possibleString = StringUtils.setToAlternates( s.getPossibleSymbolsOfType(SymType.STRING) );
        	definedArrays = StringUtils.setToAlternates( s.getDefinedSymbolsOfType(SymType.ARRAY,true) );
        	possibleArray = StringUtils.setToAlternates( s.getPossibleSymbolsOfType(SymType.ARRAY) );
        } else {
        	nonFunction = symbols;
            possibleInt = symbols;
            possibleString = symbols;
            definedInts = symbols;
            definedStrings = symbols;
            possibleArray = symbols;
            definedArrays = symbols;
        }
        
        makeRule( ruleGrammar, "definedSymbols", symbols );
        makeRule( ruleGrammar, "function0args", function0args );
        makeRule( ruleGrammar, "function1args", function1args );
        makeRule( ruleGrammar, "function2args", function2args );
        makeRule( ruleGrammar, "function3args", function3args );
        makeRule( ruleGrammar, "unusedFunction", unusedFunction );
        makeRule( ruleGrammar, "nonFunction", nonFunction );
        makeRule( ruleGrammar, "undefSym", undefSym );
        makeRule( ruleGrammar, "possibleInt", possibleInt );
        makeRule( ruleGrammar, "possibleString", possibleString );
        makeRule( ruleGrammar, "definedInts", definedInts );
        makeRule( ruleGrammar, "definedStrings", definedStrings );
        makeRule( ruleGrammar, "possibleArray", possibleArray );
        makeRule( ruleGrammar, "definedArrays", definedArrays );
        
        getGrammar().commitChanges();
        grammarChanged();
        System.out.println( ruleGrammar );
        
        if ( !doImports ) {
        	// Try to request garbage collection after functions to reduce
        	// large pauses
        	System.gc();
        }
    }
    
    void makeRule( RuleGrammar ruleGrammar, String ruleName, String ruleText ) throws JSGFGrammarException
    {
        Rule newRule;
		try {
	        if ( ruleText.length() > 0 ) {
				newRule = ruleGrammar.ruleForJSGF( ruleText );
	        } else {
	        	newRule = ruleGrammar.ruleForJSGF( "<VOID>" );
	        } 
		} catch (GrammarException e) {
			throw new JSGFGrammarException( e.toString() );
		}
        ruleGrammar.setRule( ruleName, newRule, false );
        ruleGrammar.setEnabled( ruleName, true );
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

