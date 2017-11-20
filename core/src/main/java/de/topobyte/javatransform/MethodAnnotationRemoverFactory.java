package de.topobyte.javatransform;

import com.github.javaparser.ast.CompilationUnit;

public class MethodAnnotationRemoverFactory implements ModifierFactory
{

	private String methodName;
	private String annotationName;

	public MethodAnnotationRemoverFactory(String methodName,
			String annotationName)
	{
		this.methodName = methodName;
		this.annotationName = annotationName;
	}

	@Override
	public Modifier create(CompilationUnit cu)
	{
		return new MethodAnnotationRemover(cu, methodName, annotationName);
	}

}
