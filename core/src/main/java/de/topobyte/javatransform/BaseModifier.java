package de.topobyte.javatransform;

import com.github.javaparser.ast.CompilationUnit;

public abstract class BaseModifier implements Modifier
{

	protected CompilationUnit cu;
	protected boolean modified = false;

	public BaseModifier(CompilationUnit cu)
	{
		this.cu = cu;
	}

	@Override
	public boolean isModified()
	{
		return modified;
	}

	@Override
	public String postTransform(String text)
	{
		return text;
	}

}
