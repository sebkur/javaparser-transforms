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

public class ModifierRunner
{

	private ModifierFactory factory;

	private CompilationUnit cu;

	private boolean hasRelevantMethods = false;
	private boolean modified = false;

	public ModifierRunner(ModifierFactory factory)
	{
		this.factory = factory;
	}

	public void transform(Path file) throws IOException
	{
		System.out.println("working on file: " + file);

		cu = JavaParser.parse(file);

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
			cu = JavaParser.parse(file);

			transformSimple();

			if (!modified) {
				return;
			}

			String text = cu.toString();
			Files.write(file, text.getBytes());
		}
	}

	public void transform(CompilationUnit cu) throws IOException
	{
		this.cu = cu;
		transformPreserving();
	}

	// TODO: this needs to be generalized
	private void determineHasRelevantMethods()
	{
		cu.findAll(ClassOrInterfaceDeclaration.class).stream()
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
		LexicalPreservingPrinter.setup(cu);
		Modifier remover = factory.create(cu);
		remover.transform();
		modified = remover.isModified();
	}

	private void transformSimple() throws IOException
	{
		Modifier remover = factory.create(cu);
		remover.transform();
		modified = remover.isModified();
	}

}