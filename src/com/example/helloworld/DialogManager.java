package com.example.helloworld;

/*
 * Copyright 1999-2004 Carnegie Mellon University.
 * Portions Copyright 2004 Sun Microsystems, Inc.
 * Portions Copyright 2004 Mitsubishi Electric Research Laboratories.
 * All Rights Reserved.  Use is subject to license terms.
 *
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.
 *
 */

/*
 * Additional changes Copyright 2012-2014 Benjamin M. Gordon
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.logging.Logger;

import javax.speech.recognition.GrammarException;
import edu.cmu.sphinx.decoder.ResultListener;

import edu.cmu.sphinx.frontend.DataProcessingException;
import edu.cmu.sphinx.frontend.util.Microphone;
import edu.cmu.sphinx.jsgf.JSGFGrammar;
import edu.cmu.sphinx.jsgf.JSGFGrammarException;
import edu.cmu.sphinx.jsgf.JSGFGrammarParseException;
import edu.cmu.sphinx.recognizer.Recognizer;
import edu.cmu.sphinx.result.Result;
import edu.cmu.sphinx.util.props.Configurable;
import edu.cmu.sphinx.util.props.PropertyException;
import edu.cmu.sphinx.util.props.PropertySheet;
import edu.cmu.sphinx.util.props.S4Component;

/**
 * The DialogManager is a component that is used to manage speech dialogs. A
 * speech dialog is represented as a graph of dialog nodes. The dialog manager
 * maintains an active node. When a node is active it is directing the
 * recognition process. Typically a dialog node will define the current active
 * grammar. The recognition result is typically used to direct the dialog
 * manager to select the next active node. An application can easily customize
 * the behavior at each active node.
 */
public class DialogManager implements Configurable {
	/**
	 * The property that defines the name of the grammar component to be used by
	 * this dialog manager
	 */
	@S4Component(type = JSGFGrammar.class)
	public final static String PROP_JSGF_GRAMMAR = "jsgfGrammar";

	/**
	 * The property that defines the name of the microphone to be used by this
	 * dialog manager
	 */
	@S4Component(type = Microphone.class)
	public final static String PROP_MICROPHONE = "microphone";

	/**
	 * The property that defines the name of the recognizer to be used by this
	 * dialog manager
	 */
	@S4Component(type = Recognizer.class)
	public final static String PROP_RECOGNIZER = "recognizer";

	// ------------------------------------
	// Configuration data
	// ------------------------------------
	private JSGFGrammar grammar;
	private Logger logger;
	private Recognizer recognizer;
	private Microphone microphone;

	// ------------------------------------
	// local data
	// ------------------------------------
	private DialogNode initialNode;
	private Map<String, DialogNode> nodeMap = new HashMap<String, DialogNode>();
	private String name;
	private List<ResultListener> resultListeners = new ArrayList<ResultListener>();

	public static Stack<DialogNode> savedStates = new Stack<DialogNode>();
	public static Stack<LineAction> lineActions = new Stack<LineAction>();
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.cmu.sphinx.util.props.Configurable#newProperties(edu.cmu.sphinx.util
	 * .props.PropertySheet)
	 */
	public void newProperties(PropertySheet ps) throws PropertyException {
		logger = ps.getLogger();
		grammar = (JSGFGrammar) ps.getComponent(PROP_JSGF_GRAMMAR);
		recognizer = (Recognizer) ps.getComponent(PROP_RECOGNIZER);
		try {
			microphone = (Microphone) ps.getComponent(PROP_MICROPHONE);
		} catch (PropertyException e) {
			microphone = null;
		}
	}

	/**
	 * Adds a new node to the dialog manager. The dialog manager maintains a set
	 * of dialog nodes. When a new node is added the application specific behavior
	 * 
	 * @param name
	 *            the name of the node
	 * @param behavior
	 *            the application specified behavior for the node
	 */
	public void addNode(String name, DialogNodeBehavior behavior) {
		DialogNode node = new DialogNode(name, behavior);
		putNode(node);
	}

	/**
	 * Sets the name of the initial node for the dialog manager
	 * 
	 * @param name
	 *            the name of the initial node. Must be the name of a previously
	 *            added dialog node.
	 */
	public void setInitialNode(String name) {
		if (getNode(name) == null) {
			throw new IllegalArgumentException("Unknown node " + name);
		}
		initialNode = getNode(name);
	}

	/**
	 * Gets the recognizer and the dialog nodes ready to run
	 * 
	 * @throws IOException
	 *             if an error occurs while allocating the recognizer.
	 */
	public void allocate() throws IOException {
		recognizer.allocate();

		for (DialogNode node : nodeMap.values()) {
			node.init();
		}
	}

	/**
	 * Releases all resources allocated by the dialog manager
	 */
	public void deallocate() {
		recognizer.deallocate();
	}

	/**
	 * Invokes the dialog manager. The dialog manager begin to process the
	 * dialog states starting at the initial node. This method will not return
	 * until the dialog manager is finished processing states
	 * 
	 * @throws JSGFGrammarException
	 * @throws JSGFGrammarParseException
	 */
	public void go() throws JSGFGrammarParseException, JSGFGrammarException {
		DialogNode lastNode = null;
		DialogNode curNode = initialNode;
		savedStates.push( curNode );
		lineActions.clear();
		lineActions.push( new LineAction(LineAction.ActionType.UNCHANGED,null) );

		try {
			if (microphone == null || microphone.startRecording()) {
	            SpeechManager.getManager().setListening( true );

				while (true) {

					if (curNode != lastNode) {
						if (lastNode != null) {
							lastNode.exit();
						}
						curNode.enter();
						lastNode = curNode;
			            SpeechManager.getManager().setContext( curNode.getName() );
					}
					if ( Thread.interrupted() ) {
						System.out.println( "Listening canceled" );
						return;
					}
					String nextStateName = curNode.recognize();
					if ( Thread.interrupted() ) {
						System.out.println( "Listening canceled" );
						return;
					}
					if (nextStateName == null || nextStateName.isEmpty()) {
						continue;
					} else if ( nextStateName.equals("inserted") ) {
						lineActions.push( new LineAction(LineAction.ActionType.UNCHANGED,null) );
						continue;
					} else if ( nextStateName.equals("exit") ) {
						break;
					} else if ( nextStateName.equals("out") ) {
						DialogNode n = savedStates.pop();
						curNode = savedStates.peek();
						lineActions.push( new LineAction(LineAction.ActionType.ADD,n) );
						System.out.println( "Context out: " + savedStates );
					} else if ( nextStateName.equals("correction") ) {
						// The result listener has already made the correction, but
						// we might have to adjust the context if the line had triggered
						// the addition or removal of a saved state.
						if ( lineActions.size() > 0 ) {
							LineAction action = lineActions.pop();
							switch ( action.actionType ) {
								case UNCHANGED:
									System.out.println( "Correction made.  No context change." );
									break;
								
								case ADD:
									savedStates.push( action.node );
									System.out.println( "Correction made.  Added " + action.node.getName() );
									break;
									
								case REMOVE:
									DialogNode n = savedStates.pop();
									if ( action.node != null ) {
										savedStates.push( action.node );
										System.out.println( "Correction made.  Removed " + n.getName() + " and added " + action.node.getName() );
									} else {
										System.out.println( "Correction made.  Removed " + n.getName() );
									}
									break;
							}
							curNode = savedStates.peek();
							System.out.println( "Context after correction: " + savedStates );
						}
						continue;
					} else if ( nextStateName.equals("reset") ) {
						// Clear our state and start over
						savedStates.clear();
						lineActions.clear();
						Scope.reset();
						curNode = initialNode;
						savedStates.push( curNode );
						lineActions.push( new LineAction(LineAction.ActionType.UNCHANGED,null) );
						SpeechManager.getManager().setContext( curNode.getName() );
					} else {
						DialogNode node = nodeMap.get(nextStateName);
						if (node == null) {
							warn("Can't transition to unknown state "
									+ nextStateName);
						} else {
							if ( nextStateName.equals("else_block") ) {
								// else block replaces the if, not nests.
								DialogNode n = savedStates.peek();
								lineActions.push( new LineAction(LineAction.ActionType.REMOVE,n) );
								savedStates.pop();
							} else {
								lineActions.push( new LineAction(LineAction.ActionType.REMOVE,null) );
							}
							savedStates.push( node );
							curNode = node;
							System.out.println( "New context: " + savedStates );
				            SpeechManager.getManager().setContext( node.getName() );
				        }
					}
				}
			} else {
				error("Can't start the microphone");
			}
		} catch (GrammarException ge) {
			error("grammar problem in state " + curNode.getName() + ' ' + ge);
		} catch (IOException ioe) {
			error("problem loading grammar in state " + curNode.getName() + ' '
					+ ioe);
		} catch ( DataProcessingException e ) {
			if ( e.getCause() instanceof InterruptedException ) {
				System.out.println( "Listening canceled" );
			} else {
				error("problem processing data in state " + curNode.getName() + ": " + e );
			}
		} finally {
			SpeechManager.getManager().setListening( false );
		}
	}

	/**
	 * Returns the name of this component
	 * 
	 * @return the name of the component.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the dialog node with the given name
	 * 
	 * @param name
	 *            the name of the node
	 */
	private DialogNode getNode(String name) {
		return nodeMap.get(name);
	}

	/**
	 * Puts a node into the node map
	 * 
	 * @param node
	 *            the node to place into the node map
	 */
	private void putNode(DialogNode node) {
		nodeMap.put(node.getName(), node);
	}

	/**
	 * Issues a warning message
	 * 
	 * @param s
	 *            the message
	 */
	private void warn(String s) {
		System.out.println("Warning: " + s);
	}

	/**
	 * Issues an error message
	 * 
	 * @param s
	 *            the message
	 */
	private void error(String s) {
		System.out.println("Error: " + s);
	}

	/**
	 * Issues a tracing message
	 * 
	 * @parma s the message
	 */
	private void trace(String s) {
		logger.info(s);
	}

	public Recognizer getRecognizer() {
		return recognizer;
	}

	/**
	 * Sets the recognizer
	 * 
	 * @param recognizer
	 *            the recognizer
	 */
	public void setRecognizer(Recognizer recognizer) {
		this.recognizer = recognizer;
	}

	public void addResultListener(ResultListener listener) {
		this.resultListeners.add(listener);
	}

	public static Stack<DialogNode> getSavedStates()
	{
		return savedStates;
	}
	
	public static void clearSavedStates()
	{
		savedStates = new Stack<DialogNode>();
	}
	
	/**
	 * Represents a node in the dialog
	 */
	class DialogNode {
		private DialogNodeBehavior behavior;
		private String name;

		/**
		 * Creates a dialog node with the given name an application behavior
		 * 
		 * @param name
		 *            the name of the node
		 * 
		 * @param behavior
		 *            the application behavor for the node
		 * 
		 */
		DialogNode(String name, DialogNodeBehavior behavior ) {
			this.behavior = behavior;
			this.name = name;
		}

		/**
		 * Initializes the node
		 */

		void init() {
			behavior.onInit(this);
		}

		/**
		 * Enters the node, prepares it for recognition
		 * 
		 * @throws JSGFGrammarException
		 * @throws JSGFGrammarParseException
		 */
		void enter() throws IOException, JSGFGrammarParseException,
				JSGFGrammarException {
			trace("Entering " + name);
			behavior.onEntry();
			behavior.onReady();
		}

		/**
		 * Performs recognition at the node.
		 * 
		 * @return the result tag
		 */
		String recognize() throws GrammarException {
			trace("Recognize " + name);
			Result result = recognizer.recognize();
			String tag = behavior.onRecognize(result);
			for (ResultListener l : resultListeners) {
				if ( l instanceof SLResultListener ) {
					tag = ((SLResultListener)l).newResult( result, savedStates.peek(), tag, (SLBehavior)behavior );
				} else {
					l.newResult(result);
				}
			}
			return tag;
		}

		/**
		 * Exits the node
		 */
		void exit() {
			trace("Exiting " + name);
			behavior.onExit();
		}

		/**
		 * Gets the name of the node
		 * 
		 * @return the name of the node
		 */
		public String getName() {
			return name;
		}

		/**
		 * Returns the JSGF Grammar for the dialog manager that contains this
		 * node
		 * 
		 * @return the grammar
		 */
		public JSGFGrammar getGrammar() {
			return grammar;
		}

		/**
		 * Traces a message
		 * 
		 * @param msg
		 *            the message to trace
		 */
		public void trace(String msg) {
			DialogManager.this.trace(msg);
		}

		public DialogManager getDialogManager() {
			return DialogManager.this;
		}
		
		public String toString()
		{
			return getName();
		}
	}
}
