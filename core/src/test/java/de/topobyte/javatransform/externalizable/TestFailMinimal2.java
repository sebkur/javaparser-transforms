package de.topobyte.javatransform.externalizable;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.printer.lexicalpreservation.LexicalPreservingPrinter;

import de.topobyte.javatransform.TestUtil;
import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;

public class TestFailMinimal2
{

	private static String text = "public class Foo {\n\n"
			+ "// Some comment\n\n" // does work with only one \n
			+ "public void writeExternal() {}\n" + "}";

	private static String targetText = "public class Foo {\n"
			+ "// Some comment\n\n" + "}";

	@Test
	public void test()
	{
		CompilationUnit originalCu = new JavaParser().parse(text).getResult()
				.get();
		CompilationUnit cu = LexicalPreservingPrinter.setup(originalCu);

		cu.findAll(ClassOrInterfaceDeclaration.class).stream().forEach(c -> {
			transform(c);
		});

		String modifiedText = LexicalPreservingPrinter.print(cu);

		List<String> targetLines = TestUtil.lines(targetText);
		List<String> modifiedLines = TestUtil.lines(modifiedText);

		Patch<String> diffs = DiffUtils.diff(targetLines, modifiedLines);
		for (Delta<String> delta : diffs.getDeltas()) {
			System.out.println(delta.getType() + " " + delta);
		}

		Assert.assertEquals(targetText, modifiedText);
	}

	private void transform(ClassOrInterfaceDeclaration c)
	{
		List<MethodDeclaration> methods = c.getMethodsByName("writeExternal");
		for (MethodDeclaration method : methods) {
			c.remove(method);
		}
	}

}
