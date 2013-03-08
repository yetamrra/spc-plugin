package com.example.helloworld.propertyTester;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IFile;

public class WavPropertyTester extends PropertyTester {

	public WavPropertyTester() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) 
	{
		if ( property.equals("isWav") ) {
			if ( receiver instanceof IFile ) {
				IFile file = (IFile)receiver;
				if ( !file.getName().endsWith(".wav") ) {
					return false;
				} else {
					return true;
				}
			} else {
				return false;
			}
		} else if ( property.equals("notWav") ) {
			return true;
		}

		return false;
	}

}
