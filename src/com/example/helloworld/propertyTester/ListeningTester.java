package com.example.helloworld.propertyTester;

import org.eclipse.core.expressions.PropertyTester;

import com.example.helloworld.SpeechManager;

public class ListeningTester extends PropertyTester {

	public ListeningTester() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean test(Object receiver, String property, Object[] args,Object expectedValue)
	{
		if ( property.equals("isListening") ) {
			return SpeechManager.getManager().isListening();
		} else if ( property.equals("notListening") ) {
			return !SpeechManager.getManager().isListening();
		}
		return false;
	}

}
