package de.topobyte.javatransform;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.printer.lexicalpreservation.LexicalPreservingPrinter;

import de.topobyte.system.utils.SystemPaths;

public class Test
{

	public static void main(String[] args) throws IOException
	{
		List<Path> files = new ArrayList<>();
		files.add(SystemPaths.HOME
				.resolve("github/OpenMetroMaps/OpenMetroMaps/java/maps-gwt/build/unpackedJars/com/slimjars/dist/gnu/trove/set/hash/TIntHashSet.java"));

		for (Path file : files) {
			mod(file);
		}
	}

	private static void mod(Path file) throws IOException
	{
		CompilationUnit originalCu = JavaParser.parse(file);
		CompilationUnit cu = LexicalPreservingPrinter.setup(originalCu);

		cu.findAll(ClassOrInterfaceDeclaration.class)
				.stream()
				.filter(c -> !c.isInterface())
				.forEach(
						c -> {
							System.out.println(c.getName());

							List<MethodDeclaration> writeExternal = c
									.getMethodsByName("writeExternal");
							System.out.println("found writeExternal: "
									+ writeExternal.size());
							for (MethodDeclaration decl : writeExternal) {
								boolean done = c.remove(decl);
								System.out.println(done);
							}

							List<MethodDeclaration> readExternal = c
									.getMethodsByName("readExternal");
							System.out.println("found readExternal: "
									+ readExternal.size());
							for (MethodDeclaration decl : readExternal) {
								boolean done = c.remove(decl);
								System.out.println(done);
							}
						});

		String text = LexicalPreservingPrinter.print(cu);
		Files.write(file, text.getBytes());
	}

}