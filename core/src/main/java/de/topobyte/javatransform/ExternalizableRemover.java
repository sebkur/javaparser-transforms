package de.topobyte.javatransform;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

public class ExternalizableRemover extends BaseModifier
{

	public ExternalizableRemover(CompilationUnit cu)
	{
		super(cu);
	}

	@Override
	public boolean determineWillNeedModifications()
	{
		final boolean[] hasRelevantImplements = { false };
		final boolean[] hasRelevantMethods = { false };

		cu.findAll(ClassOrInterfaceDeclaration.class).stream()
				.filter(c -> !c.isInterface()).forEach(c -> {
					for (ClassOrInterfaceType type : c.getImplementedTypes()) {
						if (type.getNameAsString().equals("Externalizable")) {
							hasRelevantImplements[0] = true;
						}
					}
					for (String methodName : Constants.RELEVANT_METHODS_FOR_EXTERNALIZABLE) {
						List<MethodDeclaration> methods = c
								.getMethodsByName(methodName);
						hasRelevantMethods[0] |= !methods.isEmpty();
					}
				});

		return hasRelevantImplements[0] || hasRelevantMethods[0];
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
		boolean modified = false;

		List<ClassOrInterfaceType> toRemove = new ArrayList<>();
		for (ClassOrInterfaceType type : c.getImplementedTypes()) {
			if (type.getNameAsString().equals("Externalizable")) {
				toRemove.add(type);
			}
		}

		for (ClassOrInterfaceType type : toRemove) {
			type.remove();
			modified = true;
		}

		List<MethodRemovalResult> results = new ArrayList<>();
		for (String methodName : Constants.RELEVANT_METHODS_FOR_EXTERNALIZABLE) {
			results.add(removeMethods(c, methodName));
		}

		for (MethodRemovalResult result : results) {
			if (result.numRemovals > 0) {
				modified = true;
			}
		}

		if (modified) {
			System.out.println(c.getName());
		}
		for (MethodRemovalResult result : results) {
			if (result.numRemovals == 0) {
				continue;
			}
			System.out.println(String.format("Removed '%s()' %d times",
					result.name, result.numRemovals));
		}

		return modified;
	}

	private MethodRemovalResult removeMethods(ClassOrInterfaceDeclaration c,
			String name)
	{
		List<MethodDeclaration> methods = c.getMethodsByName(name);
		for (MethodDeclaration method : methods) {
			c.remove(method);
		}
		return new MethodRemovalResult(name, methods.size());
	}

	private class MethodRemovalResult
	{

		private String name;
		private int numRemovals;

		public MethodRemovalResult(String name, int numRemovals)
		{
			this.name = name;
			this.numRemovals = numRemovals;
		}

	}

}