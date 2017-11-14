package de.topobyte.javatransform;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import de.topobyte.melon.paths.PathUtil;

public class ReplaceMethod
{

	public static void main(String[] args) throws IOException
	{
		if (args.length < 3) {
			System.out.println(
					"usage: ReplaceMethod <method name> <replacement java fragment> file...");
			System.exit(1);
		}

		String methodName = args[0];
		String replacementText = args[1];

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
		System.out.println("replacing with:");
		System.out.println(replacementText);

		for (Path file : files) {
			ModifierRunner modifier = new ModifierRunner(
					new MethodReplacerFactory(methodName, replacementText));
			modifier.transform(file);
		}
	}

}