package com.example.helloworld;

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
	
	public boolean equals( Object rhs )
	{
		if ( rhs instanceof ProgSym ) {
			return this.name.equals(((ProgSym)rhs).name);
		} else {
			return this.name.equals(rhs.toString());
		}
	}
}
