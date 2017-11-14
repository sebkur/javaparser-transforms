package de.topobyte.javatransform;

import com.github.javaparser.ast.CompilationUnit;

public class MethodReplacerFactory implements ModifierFactory
{

	private String methodName;
	private String replacementText;

	public MethodReplacerFactory(String methodName, String replacementText)
	{
		this.methodName = methodName;
		this.replacementText = replacementText;
	}

	@Override
	public Modifier create(CompilationUnit cu)
	{
		return new MethodReplacer(cu, methodName, replacementText);
	}

}
