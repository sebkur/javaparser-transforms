package de.topobyte.javatransform;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.printer.lexicalpreservation.LexicalPreservingPrinter;

public class ModifierRunner
{

	private ModifierFactory factory;

	private CompilationUnit cu;

	private boolean willNeedModifications = false;
	private boolean modified = false;

	public ModifierRunner(ModifierFactory factory)
	{
		this.factory = factory;
	}

	public void transform(Path file) throws IOException
	{
		System.out.println("working on file: " + file);

		cu = new JavaParser().parse(file).getResult().get();
		LexicalPreservingPrinter.setup(cu);

		determineWillNeedModifications();
		if (!willNeedModifications) {
			return;
		}

		// First try to transform while preserving formatting
		try {
			transformPreserving();

			if (!modified) {
				return;
			}

			String text = LexicalPreservingPrinter.print(cu);
			text = postTransform(text);
			Files.write(file, text.getBytes());
		} catch (Exception e) {
			System.out
					.println("failed to transform preserving formatting: " + e);
			e.printStackTrace();
			// if that fails, transform discarding formatting
			cu = new JavaParser().parse(file).getResult().get();

			transformSimple();

			if (!modified) {
				return;
			}

			String text = cu.toString();
			text = postTransform(text);
			Files.write(file, text.getBytes());
		}
	}

	public String postTransform(String text)
	{
		Modifier modifier = factory.create(cu);
		return modifier.postTransform(text);
	}

	public boolean willNeedModifications(CompilationUnit cu)
	{
		this.cu = cu;
		determineWillNeedModifications();
		return willNeedModifications;
	}

	public void transform(CompilationUnit cu) throws IOException
	{
		transform(cu, true);
	}

	public void transform(CompilationUnit cu, boolean setup) throws IOException
	{
		this.cu = cu;
		if (setup) {
			LexicalPreservingPrinter.setup(cu);
		}
		transformPreserving();
	}

	private void determineWillNeedModifications()
	{
		Modifier modifier = factory.create(cu);
		willNeedModifications = modifier.determineWillNeedModifications();
	}

	private void transformPreserving() throws IOException
	{
		Modifier modifier = factory.create(cu);
		modifier.transform();
		modified = modifier.isModified();
	}

	private void transformSimple() throws IOException
	{
		Modifier modifier = factory.create(cu);
		modifier.transform();
		modified = modifier.isModified();
	}

}