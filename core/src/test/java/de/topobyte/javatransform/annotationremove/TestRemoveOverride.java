package de.topobyte.javatransform.annotationremove;

import java.io.IOException;
import java.util.List;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.printer.lexicalpreservation.LexicalPreservingPrinter;

import de.topobyte.javatransform.MethodAnnotationRemoverFactory;
import de.topobyte.javatransform.ModifierRunner;
import de.topobyte.javatransform.TestUtil;
import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;

public class TestRemoveOverride
{

	public static void main(String[] args) throws IOException
	{
		String originalText = TestUtil.load("OverriddenMethods.java.txt");
		String targetText = TestUtil.load("OverriddenMethods.java.mod.txt");

		CompilationUnit cu = new JavaParser().parse(originalText).getResult()
				.get();

		MethodAnnotationRemoverFactory remover = new MethodAnnotationRemoverFactory(
				"clone", "Override");

		new ModifierRunner(remover).transform(cu);
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
