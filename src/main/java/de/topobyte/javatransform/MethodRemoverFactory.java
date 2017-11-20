package de.topobyte.javatransform;

import com.github.javaparser.ast.CompilationUnit;

public class MethodRemoverFactory implements ModifierFactory
{

	private String methodName;

	public MethodRemoverFactory(String methodName)
	{
		this.methodName = methodName;
	}

	@Override
	public Modifier create(CompilationUnit cu)
	{
		return new MethodRemover(cu, methodName);
	}

}
