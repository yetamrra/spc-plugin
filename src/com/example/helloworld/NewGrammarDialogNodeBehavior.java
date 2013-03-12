package com.example.helloworld;

import java.io.IOException;

import edu.cmu.sphinx.jsgf.JSGFGrammarException;
import edu.cmu.sphinx.jsgf.JSGFGrammarParseException;



/**
 * A Dialog node behavior that loads a completely new
 * grammar upon entry into the node
 */
class NewGrammarDialogNodeBehavior extends DialogNodeBehavior {

    /**
     * creates a  NewGrammarDialogNodeBehavior 
     *
     * @param grammarName the grammar name
     */
    public NewGrammarDialogNodeBehavior() {
    }

    public void onInit(DialogManager.DialogNode node) 
    {
    	super.onInit(node);
        try {
			getGrammar().loadJSGF(getGrammarName());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSGFGrammarParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSGFGrammarException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    /**
     * Called with the dialog manager enters this entry
     * @throws JSGFGrammarException 
     * @throws JSGFGrammarParseException 
     */
    public void onEntry() throws IOException, JSGFGrammarParseException, JSGFGrammarException {
        super.onEntry();
        getGrammar().loadJSGF(getGrammarName());
    }

    /**
     * Returns the name of the grammar. The name of the grammar is the same as
     * the name of the node
     *
     * @return the grammar name
     */
    public String getGrammarName() {
        return getName();
    }
}
