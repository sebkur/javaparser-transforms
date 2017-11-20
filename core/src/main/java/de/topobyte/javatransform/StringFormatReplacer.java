package de.topobyte.javatransform;

import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.SimpleName;

public class StringFormatReplacer extends BaseModifier
{

	final static Logger logger = LoggerFactory
			.getLogger(StringFormatReplacer.class);

	private static final String IMPORT_STRING_FORMAT = "java.lang.String.format";
	private Set<String> relevantMethods = new HashSet<>();
	{
		relevantMethods.add("String.format");
		relevantMethods.add("java.lang.String.format");
	}

	private static final String REPLACEMENT_CLASS = "de.topobyte.formatting.Formatting";
	private static final String REPLACEMENT_METHOD = "format";

	public StringFormatReplacer(CompilationUnit cu)
	{
		super(cu);
	}

	@Override
	public boolean determineWillNeedModifications()
	{
		final boolean[] hasRelevantCode = { false };

		cu.findAll(ImportDeclaration.class).forEach(i -> {
			if (i.isStatic()) {
				String name = i.getNameAsString();
				logger.debug("static import: " + name);
				if (name.equals(IMPORT_STRING_FORMAT)) {
					hasRelevantCode[0] = true;
				}
			}
		});

		cu.findAll(ClassOrInterfaceDeclaration.class).stream()
				.filter(c -> !c.isInterface()).forEach(c -> {
					c.findAll(MethodCallExpr.class).forEach(m -> {
						SimpleName name = m.getName();
						Optional<Expression> optScope = m.getScope();
						if (optScope.isPresent()) {
							Expression scope = optScope.get();
							String qualifiedName = scope + "." + name;
							logger.debug("method call: " + qualifiedName);
							if (relevantMethods.contains(qualifiedName)) {
								hasRelevantCode[0] = true;
							}
						} else {
							logger.debug("method call: " + name);
						}
					});
				});

		return hasRelevantCode[0];
	}

	@Override
	public void transform() throws IOException
	{
		cu.findAll(ImportDeclaration.class).forEach(i -> {
			if (i.isStatic()) {
				String name = i.getNameAsString();
				if (name.equals(IMPORT_STRING_FORMAT)) {
					i.setName(REPLACEMENT_CLASS + "." + REPLACEMENT_METHOD);
					modified = true;
				}
			}
		});

		cu.findAll(ClassOrInterfaceDeclaration.class).stream()
				.filter(c -> !c.isInterface()).forEach(c -> {
					modified |= transform(c);
				});
	}

	private boolean transform(ClassOrInterfaceDeclaration c)
	{
		boolean[] modified = { false };

		c.findAll(MethodCallExpr.class).forEach(m -> {
			SimpleName name = m.getName();
			Optional<Expression> optScope = m.getScope();
			if (optScope.isPresent()) {
				Expression scope = optScope.get();
				String qualifiedName = scope + "." + name;
				logger.debug("method call: " + qualifiedName);
				if (relevantMethods.contains(qualifiedName)) {
					scope.replace(new NameExpr(REPLACEMENT_CLASS));
					modified[0] = true;
				}
			} else {
				logger.debug("method call: " + name);
			}
		});

		return modified[0];
	}

}