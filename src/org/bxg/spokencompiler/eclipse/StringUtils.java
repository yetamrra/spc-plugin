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

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class StringUtils
{
	static public String join( List<String> list, String conjunction )
	{
	   StringBuilder sb = new StringBuilder();
	   boolean first = true;
	   for (String item : list)
	   {
	      if (first)
	         first = false;
	      else
	         sb.append(conjunction);
	      sb.append(item);
	   }
	   return sb.toString();
	}
	
	static public String join( Set<ProgSym> list, String conjunction )
	{
		List<String> names = new LinkedList<String>();
		for ( ProgSym s: list ) {
			names.add( s.name );
		}
		
		return join( names, conjunction );
	}
	
	static public String setToAlternates( Set<ProgSym> list )
	{
		return join(list," | ");
	}
}
