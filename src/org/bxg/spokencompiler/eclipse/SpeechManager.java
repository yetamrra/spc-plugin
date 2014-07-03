package org.bxg.spokencompiler.eclipse;

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

import org.bxg.spokencompiler.eclipse.views.SpokenLangView;

public class SpeechManager
{
	static private SpeechManager sManager;
	
	static {
		sManager = new SpeechManager();
	}

	public static SpeechManager getManager()
	{
		return sManager;
	}

	private SpokenLangView view;
	private String currentHypothesis;
	private String currentContext;
	private boolean listening;
	private boolean finalHypothesis;
	private SpeechListener listener;
	private boolean canceled;
	private Thread listenerThread;
	private boolean interactive;
	
	private SpeechManager()
	{
		view = null;
		currentHypothesis = "";
		currentContext = "";
		listening = false;
		finalHypothesis = false;
		listener = null;
		canceled = false;
		listenerThread = null;
		interactive = true;
	}
	
	public void setView( SpokenLangView view )
	{
		this.view = view;
	}
	
	public String getHypothesis()
	{
		return currentHypothesis;
	}
	
	public void setHypothesis( String hypothesis, boolean isFinal )
	{
		this.currentHypothesis = hypothesis;
		this.finalHypothesis = isFinal;
		if ( view != null ) {
			view.updateState();
		}
	}
	
	public boolean isFinal()
	{
		return finalHypothesis;
	}
	
	public String getContext()
	{
		return currentContext;
	}
	
	public void setContext( String context )
	{
		this.currentContext = context;
		this.currentHypothesis = "";
		if ( view != null ) {
			view.updateState();
		}		
	}
	
	public boolean isListening()
	{
		return listening;
	}
	
	public void setListening( boolean listening )
	{
		this.listening = listening;
		if ( listening ) {
			currentContext = "";
			currentHypothesis = "";
		}
		if ( view != null ) {
			view.updateState();
		}		
	}

	public SpeechListener getListener() {
		return listener;
	}

	public void setListener(SpeechListener listener, Thread t) {
		this.listener = listener;
		this.listenerThread = t;
		canceled = false;
	}
	
	public void stopListener()
	{
		if ( listener == null ) {
			return;
		}
		
		listener = null;
		canceled = true;
		if ( listenerThread.isAlive() ) {
			try {
				listenerThread.interrupt();
			} catch (SecurityException e) {
			}
		} else {
			setListening( false );
		}
		System.out.println( "Listener thread canceled" );
		listenerThread = null;
	}
	
	public boolean isCanceled()
	{
		return canceled;
	}
	
	public void setInteractive( boolean interactive )
	{
		this.interactive = interactive;
	}
	
	public boolean isInteractive()
	{
		return this.interactive;
	}
}
