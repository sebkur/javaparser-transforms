package de.topobyte.javatransform.externalizable;

import java.io.IOException;
import java.util.List;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.printer.lexicalpreservation.LexicalPreservingPrinter;

public class TestFailMinimal2
{

	private static String text = "public class Foo {\n\n"
			+ "// Some comment\n\n" // does work with only one \n
			+ "public void writeExternal() {}\n" + "}";

	public static void main(String[] args) throws IOException
	{
		CompilationUnit originalCu = new JavaParser().parse(text).getResult()
				.get();
		CompilationUnit cu = LexicalPreservingPrinter.setup(originalCu);

		cu.findAll(ClassOrInterfaceDeclaration.class).stream().forEach(c -> {
			transform(c);
		});
	}

	private static void transform(ClassOrInterfaceDeclaration c)
	{
		List<MethodDeclaration> methods = c.getMethodsByName("writeExternal");
		for (MethodDeclaration method : methods) {
			c.remove(method);
		}
	}

}
