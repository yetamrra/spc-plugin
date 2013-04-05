package com.example.helloworld;

import java.util.LinkedHashSet;
import java.util.Set;

public class Scope
{
	static Scope currentScope = new Scope( null );
	static Set<String> allSyms;
	
	static {
		allSyms = new LinkedHashSet<String>();
		allSyms.add( "a" );
		allSyms.add( "b" );
		allSyms.add( "c" );
		allSyms.add( "d" );
		allSyms.add( "e" );
		allSyms.add( "f" );
		allSyms.add( "g" );
		allSyms.add( "i" );
		allSyms.add( "j" );
		allSyms.add( "k" );
		allSyms.add( "l" );
		allSyms.add( "m" );
		allSyms.add( "n" );
		allSyms.add( "p" );
		allSyms.add( "q" );
		allSyms.add( "r" );
		allSyms.add( "x" );
		allSyms.add( "y" );
		allSyms.add( "z" );
		allSyms.add( "sort" );
		allSyms.add( "fact" );
		allSyms.add( "factor" );
		allSyms.add( "main" );
		allSyms.add( "min" );
		allSyms.add( "max" );
		allSyms.add( "average" );
		allSyms.add( "short" );
	}
	
	public static Set<String> getLegalSymbols() { return allSyms; }
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
	
	private Scope parentScope;
	private Set<ProgSym> symbols;
	
	public Scope( Scope parent )
	{
		symbols = new LinkedHashSet<ProgSym>();
		parentScope = parent;
	}
	
	private int toSymbolTree()
	{
		int depth = 0;
		if ( parentScope != null ) {
			depth = parentScope.toSymbolTree();
		}
		
		for ( ProgSym s: symbols ) {
			for ( int i=0; i<=depth; i++ ) {
			    System.out.print( "  " );
			}
			System.out.println( s );
		}
		
		return depth + 1;
	}
	
	public void define( String name, SymType type )
	{
		symbols.add( new ProgSym(name,type) );
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
	
	public Set<ProgSym> getSymbols( boolean recurse )
	{
		Set<ProgSym> retVal = new LinkedHashSet<ProgSym>();
		retVal.addAll( symbols );
		if ( recurse && parentScope != null ) {
			retVal.addAll( parentScope.getSymbols(recurse) );
		}
		
		return retVal;
	}
	
	public Set<ProgSym> getSymType( SymType type )
	{
		Set<ProgSym> symbols = getSymbols( true );
		Set<ProgSym> retVal = new LinkedHashSet<ProgSym>();
		for ( ProgSym s: symbols ) {
			if ( s.type == type ) {
				retVal.add( s );
			}
		}
		return retVal;
	}
	
	public Set<ProgSym> getFunctions( int argLimit )
	{
		// Functions are only defined at the top level,
		// and nothing else is, so we'll cheat by
		// just returning the top-level symbols
		Scope scope = this;
		while ( scope.parentScope != null ) {
			scope = scope.parentScope;
		}
		Set<ProgSym> retVal = new LinkedHashSet<ProgSym>();
		retVal.addAll( scope.symbols );
		return retVal;
	}
}
