package de.topobyte;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.printer.lexicalpreservation.LexicalPreservingPrinter;

public class ExternalizableRemover
{

	private CompilationUnit originalCu;
	private CompilationUnit cu;

	private boolean modified = false;

	public void transform(Path file) throws IOException
	{
		System.out.println("working on file: " + file);

		originalCu = JavaParser.parse(file);
		transform();

		if (!modified) {
			return;
		}

		String text = LexicalPreservingPrinter.print(cu);
		Files.write(file, text.getBytes());
	}

	public CompilationUnit transform(CompilationUnit cu) throws IOException
	{
		originalCu = cu;
		transform();
		return this.cu;
	}

	private void transform()
	{
		cu = LexicalPreservingPrinter.setup(originalCu);

		cu.findAll(ClassOrInterfaceDeclaration.class).stream()
				.filter(c -> !c.isInterface()).forEach(c -> {
					modified |= transform(c);
				});
	}

	private boolean transform(ClassOrInterfaceDeclaration c)
	{
		List<MethodRemovalResult> results = new ArrayList<>();
		results.add(removeMethods(c, "writeExternal"));
		results.add(removeMethods(c, "readExternal"));

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