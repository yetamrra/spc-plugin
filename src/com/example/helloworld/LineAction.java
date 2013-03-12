package com.example.helloworld;

public class LineAction
{
	public enum ActionType {
		UNCHANGED,
		ADD,
		REMOVE,
	};
	
	public ActionType actionType;
	public DialogManager.DialogNode node;
	
	public LineAction( ActionType action, DialogManager.DialogNode node )
	{
		this.actionType = action;
		this.node = node;
	}
}
