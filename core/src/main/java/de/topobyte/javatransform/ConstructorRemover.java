package de.topobyte.javatransform;

import java.io.IOException;
import java.util.List;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.Parameter;

public class ConstructorRemover implements Modifier
{

	private CompilationUnit cu;
	private List<String> parameterTypes;

	private boolean modified = false;

	public ConstructorRemover(CompilationUnit cu, List<String> parameterTypes)
	{
		this.cu = cu;
		this.parameterTypes = parameterTypes;
	}

	@Override
	public boolean isModified()
	{
		return modified;
	}

	@Override
	public boolean determineWillNeedModifications()
	{
		final boolean[] hasRelevantMethods = { false };

		cu.findAll(ClassOrInterfaceDeclaration.class).stream()
				.filter(c -> !c.isInterface()).forEach(c -> {
					List<ConstructorDeclaration> constructors = c
							.getConstructors();
					for (ConstructorDeclaration constructor : constructors) {
						if (fits(constructor)) {
							hasRelevantMethods[0] = true;
						}
					}
				});

		return hasRelevantMethods[0];
	}

	private boolean fits(ConstructorDeclaration constructor)
	{
		NodeList<Parameter> parameters = constructor.getParameters();
		if (parameters.size() != parameterTypes.size()) {
			return false;
		}

		for (int i = 0; i < parameters.size(); i++) {
			String requiredType = parameterTypes.get(i);
			Parameter parameter = parameters.get(i);
			String type = parameter.getType().toString();
			if (!requiredType.equals(type)) {
				return false;
			}
		}

		return true;
	}

	@Override
	public void transform() throws IOException
	{
		cu.findAll(ClassOrInterfaceDeclaration.class).stream()
				.filter(c -> !c.isInterface()).forEach(c -> {
					List<ConstructorDeclaration> constructors = c
							.getConstructors();
					for (ConstructorDeclaration constructor : constructors) {
						if (fits(constructor)) {
							c.remove(constructor);
							modified = true;
						}
					}
				});
	}

	@Override
	public String postTransform(String text)
	{
		return text;
	}

}