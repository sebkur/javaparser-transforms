package de.topobyte.javatransform;

import com.github.javaparser.ast.CompilationUnit;

public interface ModifierFactory
{

	Modifier create(CompilationUnit cu);

}
