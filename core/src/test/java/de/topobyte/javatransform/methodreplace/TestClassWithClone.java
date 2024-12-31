package de.topobyte.javatransform.methodreplace;

import java.io.IOException;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.printer.lexicalpreservation.LexicalPreservingPrinter;

import de.topobyte.javatransform.MethodReplacerFactory;
import de.topobyte.javatransform.ModifierRunner;
import de.topobyte.javatransform.TestUtil;
import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;

public class TestClassWithClone
{

	@Test
	public void test() throws IOException
	{
		String originalText = TestUtil.load("ClassWithClone.java.txt");
		String targetText = TestUtil.load("ClassWithClone.java.mod.txt");

		CompilationUnit cu = new JavaParser().parse(originalText).getResult()
				.get();

		ModifierRunner modifierRunner = new ModifierRunner(
				new MethodReplacerFactory("clone",
						"public Object clone() { return new ClassWithClone(1); }"));

		modifierRunner.willNeedModifications(cu);
		modifierRunner.transform(cu);
		String modifiedText = LexicalPreservingPrinter.print(cu);
		modifiedText = modifierRunner.postTransform(modifiedText);

		List<String> originalLines = TestUtil.lines(originalText);
		List<String> modifiedLines = TestUtil.lines(modifiedText);

		Patch<String> diffs = DiffUtils.diff(originalLines, modifiedLines);
		for (Delta<String> delta : diffs.getDeltas()) {
			System.out.println(delta.getType() + " " + delta);
		}

		Assert.assertEquals(targetText, modifiedText);
	}

}
