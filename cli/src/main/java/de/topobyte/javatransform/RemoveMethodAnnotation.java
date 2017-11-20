package de.topobyte.javatransform;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import de.topobyte.melon.paths.PathUtil;

public class RemoveMethodAnnotation
{

	public static void main(String[] args) throws IOException
	{
		if (args.length < 3) {
			System.out.println(
					"usage: RemoveMethod <method name> <annotation name> file...");
			System.exit(1);
		}

		String methodName = args[0];
		String annotationName = args[1];

		List<Path> files = new ArrayList<>();

		for (int i = 2; i < args.length; i++) {
			Path file = Paths.get(args[i]);

			if (Files.isRegularFile(file)) {
				files.add(file);
			} else if (Files.isDirectory(file)) {
				files.addAll(PathUtil.findRecursive(file, "*.java"));
			}
		}

		System.out.println(String.format("Working on %d files", files.size()));
		System.out.println("method name: '" + methodName + "'");
		System.out.println("annotation name: '" + annotationName + "'");

		for (Path file : files) {
			ModifierRunner modifier = new ModifierRunner(
					new MethodAnnotationRemoverFactory(methodName,
							annotationName));
			modifier.transform(file);
		}
	}

}