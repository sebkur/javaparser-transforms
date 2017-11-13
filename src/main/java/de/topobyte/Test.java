package de.topobyte;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

import de.topobyte.system.utils.SystemPaths;

public class Test
{

	public static void main(String[] args) throws IOException
	{
		List<Path> files = new ArrayList<>();

		Path file = SystemPaths.HOME
				.resolve("github/OpenMetroMaps/OpenMetroMaps/java/maps-gwt/build/unpackedJars/com/slimjars/dist/gnu/trove/set/hash/TIntHashSet.java");

		CompilationUnit cu = JavaParser.parse(file);

		cu.findAll(ClassOrInterfaceDeclaration.class)
				.stream()
				.filter(c -> !c.isInterface()
						&& !c.getName().getId().startsWith("Abstract"))
				.forEach(
						c -> {
							System.out.println(c.getName());

							List<MethodDeclaration> writeExternal = c
									.getMethodsByName("writeExternal");
							System.out.println("found writeExternal: "
									+ writeExternal.size());

							List<MethodDeclaration> readExternal = c
									.getMethodsByName("readExternal");
							System.out.println("found readExternal: "
									+ readExternal.size());
						});
	}

}