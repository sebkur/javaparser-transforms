package de.topobyte.javatransform.web;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.printer.lexicalpreservation.LexicalPreservingPrinter;

import de.topobyte.javatransform.ModifierRunner;
import de.topobyte.javatransform.TestUtil;
import de.topobyte.javatransform.WebContextRemoverFactory;

public class TestPageGenerator
{

	@Test
	public void test() throws IOException
	{
		String originalText = TestUtil.load("PageGenerator.txt");
		String expectedText = TestUtil.load("PageGenerator.mod.txt");

		CompilationUnit cu = JavaParser.parse(originalText);

		WebContextRemoverFactory remover = new WebContextRemoverFactory();

		ModifierRunner modifierRunner = new ModifierRunner(remover);

		if (!modifierRunner.willNeedModifications(cu)) {
			return;
		}
		modifierRunner.transform(cu);
		String modifiedText = LexicalPreservingPrinter.print(cu);

		Assert.assertEquals(expectedText, modifiedText);
	}

}
