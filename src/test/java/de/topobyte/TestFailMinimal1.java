package de.topobyte;

import java.io.IOException;
import java.util.List;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.printer.lexicalpreservation.LexicalPreservingPrinter;

import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;

public class TestFailMinimal1
{

	public static void main(String[] args) throws IOException
	{
		String originalText = TestUtil.load("Fail.txt");
		String targetText = TestUtil.load("Fail.mod.txt");

		CompilationUnit cu = JavaParser.parse(originalText);
		new ModifierRunner(new ExternalizableRemoverFactory()).transform(cu);
		String modifiedText = LexicalPreservingPrinter.print(cu);

		List<String> targetLines = TestUtil.lines(targetText);
		List<String> modifiedLines = TestUtil.lines(modifiedText);

		Patch<String> diffs = DiffUtils.diff(targetLines, modifiedLines);
		for (Delta<String> delta : diffs.getDeltas()) {
			System.out.println(delta.getType() + " " + delta);
		}

		System.out.println(targetText.equals(modifiedText));
	}

}
