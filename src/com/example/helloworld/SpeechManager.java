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
	
	private SpeechManager()
	{
		view = null;
		currentHypothesis = "";
		currentContext = "";
		listening = false;
		finalHypothesis = false;
		listener = null;
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

	public void setListener(SpeechListener listener) {
		this.listener = listener;
	}
	
	public void stopListener()
	{
		if ( listener == null ) {
			return;
		}
		
		// FIXME: Stop the listener
	}
}
