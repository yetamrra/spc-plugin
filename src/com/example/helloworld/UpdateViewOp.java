package com.example.helloworld;

import com.example.helloworld.views.SpokenLangView;

public class UpdateViewOp implements Runnable
{
	private SpokenLangView view;
	
	public UpdateViewOp( SpokenLangView view )
	{
		this.view = view;
	}
	
	@Override
	public void run() {
		view.updateStateSafe();
	}

}
