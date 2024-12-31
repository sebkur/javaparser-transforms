package de.topobyte.javatransform.constructorremove;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.printer.lexicalpreservation.LexicalPreservingPrinter;

import de.topobyte.javatransform.ConstructorRemoverFactory;
import de.topobyte.javatransform.ModifierRunner;
import de.topobyte.javatransform.TestUtil;
import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;

public class TestTIntHashSetDefault
{

	@Test
	public void test() throws IOException
	{
		String originalText = TestUtil.load("TIntHashSet.java.txt");
		String targetText = TestUtil
				.load("TIntHashSet.java.removeddefaultconstructor.txt");

		CompilationUnit cu = new JavaParser().parse(originalText).getResult()
				.get();

		List<String> types = new ArrayList<>();
		ConstructorRemoverFactory remover = new ConstructorRemoverFactory(
				types);

		new ModifierRunner(remover).transform(cu);
		String modifiedText = LexicalPreservingPrinter.print(cu);

		List<String> targetLines = TestUtil.lines(targetText);
		List<String> modifiedLines = TestUtil.lines(modifiedText);

		Patch<String> diffs = DiffUtils.diff(targetLines, modifiedLines);
		for (Delta<String> delta : diffs.getDeltas()) {
			System.out.println(delta.getType() + " " + delta);
		}

		Assert.assertEquals(targetText, modifiedText);
	}

}
