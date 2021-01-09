package de.topobyte.javatransform;

import com.github.javaparser.ast.CompilationUnit;

public class WebContextRemoverFactory implements ModifierFactory
{

	@Override
	public Modifier create(CompilationUnit cu)
	{
		return new WebContextRemover(cu);
	}

}
