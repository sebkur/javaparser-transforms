package de.topobyte.javatransform.web;

import java.io.IOException;
import java.util.List;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.printer.lexicalpreservation.LexicalPreservingPrinter;

import de.topobyte.javatransform.ModifierRunner;
import de.topobyte.javatransform.TestUtil;
import de.topobyte.javatransform.WebContextRemoverFactory;
import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;

public class TryPageGenerator
{

	public static void main(String[] args) throws IOException
	{
		String originalText = TestUtil.load("PageGenerator.txt");

		CompilationUnit cu = JavaParser.parse(originalText);

		WebContextRemoverFactory remover = new WebContextRemoverFactory();

		ModifierRunner modifierRunner = new ModifierRunner(remover);

		if (!modifierRunner.willNeedModifications(cu)) {
			return;
		}
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
