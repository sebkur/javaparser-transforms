package de.topobyte.javatransform.externalizable;

import java.io.IOException;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.printer.lexicalpreservation.LexicalPreservingPrinter;

import de.topobyte.javatransform.ExternalizableRemoverFactory;
import de.topobyte.javatransform.ModifierRunner;
import de.topobyte.javatransform.TestUtil;
import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;

public class TestCaseMinimal4
{

	@Test
	public void test() throws IOException
	{
		String originalText = TestUtil.load("externalizable/Case4.txt");
		String targetText = TestUtil.load("externalizable/Case4.mod.txt");

		CompilationUnit cu = new JavaParser().parse(originalText).getResult()
				.get();
		new ModifierRunner(new ExternalizableRemoverFactory()).transform(cu);
		String modifiedText = LexicalPreservingPrinter.print(cu);
		System.out.println(originalText);

		List<String> targetLines = TestUtil.lines(targetText);
		List<String> modifiedLines = TestUtil.lines(modifiedText);

		Patch<String> diffs = DiffUtils.diff(targetLines, modifiedLines);
		for (Delta<String> delta : diffs.getDeltas()) {
			System.out.println(delta.getType() + " " + delta);
		}

		System.out.println(modifiedText);
		Assert.assertEquals(targetText, modifiedText);
	}

}
