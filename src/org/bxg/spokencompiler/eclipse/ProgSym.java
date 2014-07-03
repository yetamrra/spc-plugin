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

public class ProgSym
{
	public String name;
	public SymType type;
	
	public ProgSym( String name, SymType type )
	{
		this.name = name;
		this.type = type;
	}
	
	public String toString()
	{
		return this.name + "<" + this.type + ">";
	}
	
	@Override
	public boolean equals( Object rhs )
	{
		if ( rhs instanceof ProgSym ) {
			return this.name.equals(((ProgSym)rhs).name);
		} else {
			return this.name.equals(rhs.toString());
		}
	}
	
	@Override
	public int hashCode()
	{
		return name.hashCode();
	}
}
