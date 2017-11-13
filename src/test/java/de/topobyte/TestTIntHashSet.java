package de.topobyte;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.printer.lexicalpreservation.LexicalPreservingPrinter;

public class TestTIntHashSet
{

	public static void main(String[] args) throws IOException
	{
		InputStream input = TestTIntHashSet.class.getClassLoader()
				.getResourceAsStream("TIntHashSet.java.txt");
		String originalText = IOUtils.toString(input, StandardCharsets.UTF_8);

		CompilationUnit originalCu = JavaParser.parse(originalText);
		CompilationUnit cu = new ExternalizableRemover().transform(originalCu);
		String text = LexicalPreservingPrinter.print(cu);
		System.out.println(text);
		// TODO: compare to other file
	}

}
