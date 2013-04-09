package com.example.helloworld;

import com.example.helloworld.views.SpokenLangView;

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
