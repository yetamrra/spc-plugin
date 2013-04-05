package com.example.helloworld;

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
}
