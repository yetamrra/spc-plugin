package com.example.helloworld;

import com.example.helloworld.DialogManager.DialogNode;

public class TextInsertion
{
	public int offset;
	public int length;
	public DialogNode context;
	
	public TextInsertion( int offset, int length, DialogNode context )
	{
		this.offset = offset;
		this.length = length;
		this.context = context;
	}
	
	public String toString()
	{
		return "inserted text at " + offset + " with length " + length + " in context " + context.getName();
	}
}
