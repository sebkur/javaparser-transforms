package de.topobyte;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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

	private boolean hasRelevantMethods = false;
	private boolean modified = false;

	public void transform(Path file) throws IOException
	{
		System.out.println("working on file: " + file);

		originalCu = JavaParser.parse(file);

		determineHasRelevantMethods();
		if (!hasRelevantMethods) {
			return;
		}

		// First try to transform while preserving formatting
		try {
			transformPreserving();

			if (!modified) {
				return;
			}

			String text = LexicalPreservingPrinter.print(cu);
			Files.write(file, text.getBytes());
		} catch (Exception e) {
			// if that fails, transform discarding formatting
			originalCu = JavaParser.parse(file);

			transformSimple();

			if (!modified) {
				return;
			}

			String text = originalCu.toString();
			Files.write(file, text.getBytes());
		}
	}

	public CompilationUnit transform(CompilationUnit cu) throws IOException
	{
		originalCu = cu;
		transformPreserving();
		return this.cu;
	}

	private void determineHasRelevantMethods()
	{
		originalCu.findAll(ClassOrInterfaceDeclaration.class).stream()
				.filter(c -> !c.isInterface()).forEach(c -> {
					for (String methodName : Constants.RELEVANT_METHODS_FOR_EXTERNALIZABLE) {
						List<MethodDeclaration> methods = c
								.getMethodsByName(methodName);
						hasRelevantMethods |= !methods.isEmpty();
					}
				});
	}

	private void transformPreserving() throws IOException
	{
		cu = LexicalPreservingPrinter.setup(originalCu);
		ExternalizableRemoverInternal remover = new ExternalizableRemoverInternal(
				cu);
		remover.transform();
		modified = remover.isModified();
	}

	private void transformSimple() throws IOException
	{
		ExternalizableRemoverInternal remover = new ExternalizableRemoverInternal(
				originalCu);
		remover.transform();
		modified = remover.isModified();
	}

}