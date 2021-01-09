package de.topobyte.javatransform;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.ExplicitConstructorInvocationStmt;

public class WebContextRemover extends BaseModifier
{

	final static Logger logger = LoggerFactory
			.getLogger(WebContextRemover.class);

	private static final String IMPORT = "de.topobyte.pagegen.core.Context";
	private static final String TYPENAME = "Context";

	private ImportRemoverFactory importRemoverFactory;

	public WebContextRemover(CompilationUnit cu)
	{
		super(cu);
		importRemoverFactory = new ImportRemoverFactory(IMPORT, false);
	}

	@Override
	public boolean determineWillNeedModifications()
	{
		final boolean[] hasRelevantCode = { false };

		cu.findAll(ImportDeclaration.class).forEach(i -> {
			if (!i.isStatic()) {
				String name = i.getNameAsString();
				logger.debug("import: " + name);
				if (name.equals(IMPORT)) {
					hasRelevantCode[0] = true;
				}
			}
		});

		return hasRelevantCode[0];
	}

	@Override
	public void transform() throws IOException
	{
		Modifier importRemover = importRemoverFactory.create(cu);
		importRemover.transform();
		modified = true;

		cu.findAll(ConstructorDeclaration.class).stream().forEach(c -> {
			String name = null;
			if (c.getParameters().isNonEmpty()) {
				Parameter param = c.getParameter(0);
				if (param.getType().toString().equals(TYPENAME)) {
					c.remove(param);
					name = param.getName().toString();
				}
			}
			if (name != null) {
				final String theName = name;
				c.findAll(ExplicitConstructorInvocationStmt.class).stream()
						.forEach(spr -> {
							if (spr.getArguments().isNonEmpty()) {
								Expression argument = spr.getArgument(0);
								if (argument.toString().equals(theName)) {
									spr.remove(argument);
								}
							}
						});
			}
		});
	}

}