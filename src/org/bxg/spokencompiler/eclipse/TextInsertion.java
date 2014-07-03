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

import org.bxg.spokencompiler.eclipse.DialogManager.DialogNode;

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
