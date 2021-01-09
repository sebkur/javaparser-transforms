package de.topobyte.javatransform.importremove;

import java.io.IOException;
import java.util.List;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.printer.lexicalpreservation.LexicalPreservingPrinter;

import de.topobyte.javatransform.ImportRemoverFactory;
import de.topobyte.javatransform.ModifierRunner;
import de.topobyte.javatransform.TestUtil;
import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;

public class TestTIntHashSet
{

	public static void main(String[] args) throws IOException
	{
		String originalText = TestUtil.load("TIntHashSet.java.txt");
		String targetText = TestUtil
				.load("TIntHashSet.java.removedimports.txt");

		CompilationUnit cu = new JavaParser().parse(originalText).getResult()
				.get();

		ImportRemoverFactory remover1 = new ImportRemoverFactory("TIntIterator",
				false);
		ImportRemoverFactory remover2 = new ImportRemoverFactory(
				"java.util.Arrays", false);
		ImportRemoverFactory remover3 = new ImportRemoverFactory(
				"com.slimjars.dist.gnu.trove.impl.hash", true);

		new ModifierRunner(remover1).transform(cu);
		new ModifierRunner(remover2).transform(cu, false);
		new ModifierRunner(remover3).transform(cu, false);
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
