package de.topobyte.javatransform.stringformat;

import java.io.IOException;
import java.util.List;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.printer.lexicalpreservation.LexicalPreservingPrinter;

import de.topobyte.javatransform.ModifierRunner;
import de.topobyte.javatransform.StringFormatReplacerFactory;
import de.topobyte.javatransform.TestUtil;
import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;

public class TestSomeClass
{

	public static void main(String[] args) throws IOException
	{
		String originalText = TestUtil.load("SomeClass.java.txt");

		CompilationUnit cu = JavaParser.parse(originalText);

		ModifierRunner modifierRunner = new ModifierRunner(
				new StringFormatReplacerFactory());

		modifierRunner.willNeedModifications(cu);
		modifierRunner.transform(cu);
		String modifiedText = LexicalPreservingPrinter.print(cu);

		List<String> originalLines = TestUtil.lines(originalText);
		List<String> modifiedLines = TestUtil.lines(modifiedText);

		Patch<String> diffs = DiffUtils.diff(originalLines, modifiedLines);
		for (Delta<String> delta : diffs.getDeltas()) {
			System.out.println(delta.getType() + " " + delta);
		}

		System.out.println(modifiedText);
	}

}
