package de.topobyte.javatransform;

import java.io.IOException;
import java.util.List;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

public class MethodRemover implements Modifier
{

	private CompilationUnit cu;
	private String methodName;

	private boolean modified = false;

	public MethodRemover(CompilationUnit cu, String methodName)
	{
		this.cu = cu;
		this.methodName = methodName;
	}

	@Override
	public boolean isModified()
	{
		return modified;
	}

	@Override
	public boolean determineWillNeedModifications()
	{
		final boolean[] hasRelevantMethods = { false };

		cu.findAll(ClassOrInterfaceDeclaration.class).stream()
				.filter(c -> !c.isInterface()).forEach(c -> {
					List<MethodDeclaration> methods = c
							.getMethodsByName(methodName);
					hasRelevantMethods[0] |= !methods.isEmpty();
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
			c.remove(method);
			modified = true;
		}
	}

	@Override
	public String postTransform(String text)
	{
		return text;
	}

}