package com.example.helloworld;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Scope
{
	static Scope currentScope = new Scope( null );
	
	private Scope parentScope;
	private Set<String> symbols;
	
	public static Scope getCurrentScope() { return currentScope; }
	public static void reset() { currentScope = new Scope( null ); }
	
	public static void popScope()
	{
		System.out.println( "End of block.  Defined symbols: " );
		currentScope.toSymbolTree();
		if ( currentScope.parentScope != null ) {
			currentScope = currentScope.parentScope;
		}
	}
	
	public static Scope newScope()
	{
		currentScope = new Scope( currentScope );
		return currentScope;
	}
	
	public Scope( Scope parent )
	{
		symbols = new LinkedHashSet<String>();
		parentScope = parent;
	}
	
	private int toSymbolTree()
	{
		int depth = 0;
		if ( parentScope != null ) {
			depth = parentScope.toSymbolTree();
		}
		
		for ( String s: symbols ) {
			for ( int i=0; i<=depth; i++ ) {
			    System.out.print( "  " );
			}
			System.out.println( s );
		}
		
		return depth + 1;
	}
	
	public void define( String name )
	{
		symbols.add( name );
		System.out.println( "Defined symbol " + name );
	}
	
	public boolean isDefined( String name, boolean recurse )
	{
		Scope s = currentScope;
		while ( s != null ) {
			if ( s.symbols.contains(name) ) {
				return true;
			} else {
				s = recurse ? s.parentScope : null;
			}
		}
		
		return false;
	}
	
	public Scope getParent()
	{
		return parentScope;
	}
	
	public List<String> getSymbols( boolean recurse )
	{
		List<String> retVal = new LinkedList<String>();
		retVal.addAll( symbols );
		if ( recurse && parentScope != null ) {
			retVal.addAll( parentScope.getSymbols(recurse) );
		}
		
		return retVal;
	}
}
