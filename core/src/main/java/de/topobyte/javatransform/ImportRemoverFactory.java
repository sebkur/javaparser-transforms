package de.topobyte.javatransform;

import com.github.javaparser.ast.CompilationUnit;

public class ImportRemoverFactory implements ModifierFactory
{

	private String importName;
	private boolean isWildcard;

	public ImportRemoverFactory(String importName, boolean isWildcard)
	{
		this.importName = importName;
		this.isWildcard = isWildcard;
	}

	@Override
	public Modifier create(CompilationUnit cu)
	{
		return new ImportRemover(cu, importName, isWildcard);
	}

}
