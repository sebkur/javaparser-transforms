package de.topobyte.javatransform;

import java.util.List;

import com.github.javaparser.ast.CompilationUnit;

public class ConstructorRemoverFactory implements ModifierFactory
{

	private List<String> parameterTypes;

	public ConstructorRemoverFactory(List<String> parameterTypes)
	{
		this.parameterTypes = parameterTypes;
	}

	@Override
	public Modifier create(CompilationUnit cu)
	{
		return new ConstructorRemover(cu, parameterTypes);
	}

}
