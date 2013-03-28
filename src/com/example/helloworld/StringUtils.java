package com.example.helloworld;

import java.util.List;

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
}
