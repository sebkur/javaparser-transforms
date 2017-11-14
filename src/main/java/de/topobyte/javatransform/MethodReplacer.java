package de.topobyte.javatransform;

import java.io.IOException;
import java.util.EnumSet;
import java.util.List;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

public class MethodReplacer implements Modifier
{

	private CompilationUnit cu;
	private String methodName;
	private String replacementText;

	private boolean modified = false;

	public MethodReplacer(CompilationUnit cu, String methodName,
			String replacementText)
	{
		this.cu = cu;
		this.methodName = methodName;
		this.replacementText = replacementText;
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
			method.replace(new FieldDeclaration(
					EnumSet.noneOf(com.github.javaparser.ast.Modifier.class),
					new ClassOrInterfaceType("REPLACE"), "ME!"));
		}
	}

	@Override
	public String postTransform(String text)
	{
		return text.replaceAll("REPLACE ME!;", replacementText);
	}

}