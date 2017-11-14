package de.topobyte.javatransform;

import com.github.javaparser.ast.CompilationUnit;

public class ExternalizableRemoverFactory implements ModifierFactory
{

	@Override
	public Modifier create(CompilationUnit cu)
	{
		return new ExternalizableRemover(cu);
	}

}
