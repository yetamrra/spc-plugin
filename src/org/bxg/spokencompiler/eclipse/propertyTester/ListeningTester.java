package org.bxg.spokencompiler.eclipse.propertyTester;

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

import org.bxg.spokencompiler.eclipse.SpeechManager;
import org.eclipse.core.expressions.PropertyTester;

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
