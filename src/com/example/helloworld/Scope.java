package com.example.helloworld;

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

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

public class Scope
{
	static Scope currentScope = new Scope( null );
	static Set<ProgSym> allSyms;
	
	static {
		allSyms = new LinkedHashSet<ProgSym>();
		allSyms.add( new ProgSym("a", SymType.UNKNOWN) );
		allSyms.add( new ProgSym("b", SymType.UNKNOWN) );
		allSyms.add( new ProgSym("c", SymType.UNKNOWN) );
		allSyms.add( new ProgSym("d", SymType.UNKNOWN) );
		allSyms.add( new ProgSym("e", SymType.UNKNOWN) );
		allSyms.add( new ProgSym("f", SymType.UNKNOWN) );
		allSyms.add( new ProgSym("g", SymType.UNKNOWN) );
		allSyms.add( new ProgSym("i", SymType.UNKNOWN) );
		allSyms.add( new ProgSym("j", SymType.UNKNOWN) );
		allSyms.add( new ProgSym("k", SymType.UNKNOWN) );
		allSyms.add( new ProgSym("l", SymType.UNKNOWN) );
		allSyms.add( new ProgSym("m", SymType.UNKNOWN) );
		allSyms.add( new ProgSym("n", SymType.UNKNOWN) );
		allSyms.add( new ProgSym("p", SymType.UNKNOWN) );
		allSyms.add( new ProgSym("q", SymType.UNKNOWN) );
		allSyms.add( new ProgSym("r", SymType.UNKNOWN) );
		allSyms.add( new ProgSym("x", SymType.UNKNOWN) );
		allSyms.add( new ProgSym("y", SymType.UNKNOWN) );
		allSyms.add( new ProgSym("z", SymType.UNKNOWN) );
		allSyms.add( new ProgSym("sort", SymType.UNKNOWN) );
		allSyms.add( new ProgSym("fact", SymType.UNKNOWN) );
		allSyms.add( new ProgSym("factor", SymType.UNKNOWN) );
		allSyms.add( new ProgSym("main", SymType.UNKNOWN) );
		allSyms.add( new ProgSym("min", SymType.UNKNOWN) );
		allSyms.add( new ProgSym("max", SymType.UNKNOWN) );
		allSyms.add( new ProgSym("average", SymType.UNKNOWN) );
		allSyms.add( new ProgSym("short", SymType.UNKNOWN) );
	}
	
	public static Set<ProgSym> getLegalSymbols() { return new LinkedHashSet<ProgSym>(allSyms); }
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
		ProgSym tmpSym = new ProgSym( name, SymType.UNKNOWN );
		while ( s != null ) {
			if ( s.symbols.contains(tmpSym) ) {
				return true;
			} else {
				s = recurse ? s.parentScope : null;
			}
		}
		
		return false;
	}
	
	public void setType( String name, SymType type, boolean recurse )
	{
		Scope s = currentScope;
		ProgSym tmpSym = new ProgSym( name, SymType.UNKNOWN );
		while ( s != null ) {
			if ( s.symbols.contains(tmpSym) ) {
				Iterator<ProgSym> i = s.symbols.iterator();
				while ( i.hasNext() ) {
					ProgSym sym = i.next();
					if ( sym.equals(name) ) {
						if ( sym.type == SymType.UNKNOWN ) {
							sym.type = type;
						}
						return;
					}
				}
			} else {
				s = recurse ? s.parentScope : null;
			}
		}
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
	
	public Set<ProgSym> getDefinedSymbolsOfType( SymType type, boolean includeUnknown )
	{
		Set<ProgSym> symbols = getSymbols( true );
		Set<ProgSym> retVal = new LinkedHashSet<ProgSym>();
		for ( ProgSym s: symbols ) {
			if ( s.type == type || (includeUnknown && s.type == SymType.UNKNOWN) ) {
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
	
	public Set<ProgSym> getUnusedSymbols()
	{
		Set<ProgSym> unused = getLegalSymbols();
		unused.removeAll( getSymbols(true) ); 
		return unused;
	}
	
	public Set<ProgSym> getPossibleSymbolsOfType( SymType type )
	{
		/*
		 * Returns symbols that have the specified type or are unused.
		 * This represents names that could be used to hold type (either
		 * because they already do or because we don't know what type
		 * they hold).
		 */
		Set<ProgSym> unusedSyms = getUnusedSymbols();
		Set<ProgSym> definedSyms = getDefinedSymbolsOfType( type, true );
		definedSyms.addAll( unusedSyms );
		return definedSyms;
	}
}
