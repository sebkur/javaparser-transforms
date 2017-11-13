package de.topobyte;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

public class ExternalizableRemoverInternal
{

	private CompilationUnit cu;

	private boolean modified = false;

	public ExternalizableRemoverInternal(CompilationUnit cu)
	{
		this.cu = cu;
	}

	public boolean isModified()
	{
		return modified;
	}

	public void transform() throws IOException
	{
		cu.findAll(ClassOrInterfaceDeclaration.class).stream()
				.filter(c -> !c.isInterface()).forEach(c -> {
					modified |= transform(c);
				});
	}

	private boolean transform(ClassOrInterfaceDeclaration c)
	{
		List<MethodRemovalResult> results = new ArrayList<>();
		for (String methodName : Constants.RELEVANT_METHODS_FOR_EXTERNALIZABLE) {
			results.add(removeMethods(c, methodName));
		}

		boolean modified = false;
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

		return true;
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