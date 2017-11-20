package de.topobyte.javatransform;

import com.github.javaparser.ast.CompilationUnit;

public class StringFormatReplacerFactory implements ModifierFactory
{

	@Override
	public Modifier create(CompilationUnit cu)
	{
		return new StringFormatReplacer(cu);
	}

}
