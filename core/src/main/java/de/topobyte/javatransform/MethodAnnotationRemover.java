package de.topobyte.javatransform;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;

public class MethodAnnotationRemover extends BaseModifier
{

	private String methodName;
	private String annotationName;

	public MethodAnnotationRemover(CompilationUnit cu, String methodName,
			String annotationName)
	{
		super(cu);
		this.methodName = methodName;
		this.annotationName = annotationName;
	}

	@Override
	public boolean determineWillNeedModifications()
	{
		final boolean[] hasRelevantMethods = { false };

		cu.findAll(ClassOrInterfaceDeclaration.class).stream()
				.filter(c -> !c.isInterface()).forEach(c -> {
					List<MethodDeclaration> methods = c
							.getMethodsByName(methodName);
					for (MethodDeclaration method : methods) {
						Optional<AnnotationExpr> annotation = method
								.getAnnotationByName(annotationName);
						if (annotation.isPresent()) {
							hasRelevantMethods[0] = true;
						}
					}
				});

		return hasRelevantMethods[0];
	}

	@Override
	public void transform() throws IOException
	{
		cu.findAll(ClassOrInterfaceDeclaration.class).stream()
				.filter(c -> !c.isInterface()).forEach(c -> {
					modified |= transform(c);
				});
	}

	private boolean transform(ClassOrInterfaceDeclaration c)
	{
		replaceMethods(c, methodName);

		if (modified) {
			System.out.println(c.getName());
		}

		return modified;
	}

	private void replaceMethods(ClassOrInterfaceDeclaration c, String name)
	{
		List<MethodDeclaration> methods = c.getMethodsByName(name);
		for (MethodDeclaration method : methods) {
			Optional<AnnotationExpr> annotation = method
					.getAnnotationByName(annotationName);
			if (annotation.isPresent()) {
				method.remove(annotation.get());
				modified = true;
			}
		}
	}

}