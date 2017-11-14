package de.topobyte;

import com.github.javaparser.ast.CompilationUnit;

public class ExternalizableRemoverFactory implements ModifierFactory
{

	@Override
	public Modifier create(CompilationUnit cu)
	{
		return new ExternalizableRemover(cu);
	}

}
