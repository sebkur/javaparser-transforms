package de.topobyte.javatransform;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.Name;

public class ImportRemover extends BaseModifier
{

	private String importName;
	private boolean isWildcard;

	public ImportRemover(CompilationUnit cu, String importName,
			boolean isWildcard)
	{
		super(cu);
		this.importName = importName;
		this.isWildcard = isWildcard;
	}

	@Override
	public boolean determineWillNeedModifications()
	{
		final boolean[] hasRelevantMethods = { false };

		NodeList<ImportDeclaration> imports = cu.getImports();
		for (ImportDeclaration imp : imports) {
			if (fits(imp)) {
				hasRelevantMethods[0] = true;
			}
		}

		return hasRelevantMethods[0];
	}

	@Override
	public void transform() throws IOException
	{
		if (isWildcard) {
			System.out.println("Remove import: " + importName + ".*");
		} else {
			System.out.println("Remove import: " + importName);
		}

		List<ImportDeclaration> toRemove = new ArrayList<>();

		NodeList<ImportDeclaration> imports = cu.getImports();
		for (ImportDeclaration imp : imports) {
			String full = imp.getName().toString();
			if (fits(imp)) {
				if (isWildcard) {
					System.out.println("REMOVE " + full + ".*");
				} else {
					System.out.println("REMOVE " + full);
				}
				toRemove.add(imp);
			}
		}

		modified = !toRemove.isEmpty();
		imports.removeAll(toRemove);
	}

	private boolean fits(ImportDeclaration imp)
	{
		Name name = imp.getName();
		String full = name.toString();

		if (isWildcard && imp.isAsterisk()) {
			if (importName.equals(full)) {
				return true;
			}
		}

		if (!isWildcard && !imp.isAsterisk()) {
			String id = name.getIdentifier();
			if (importName.equals(full)) {
				return true;
			} else if (importName.equals(id)) {
				return true;
			}
		}

		return false;
	}

}