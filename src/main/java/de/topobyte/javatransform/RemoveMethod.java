package de.topobyte.javatransform;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import de.topobyte.melon.paths.PathUtil;

public class RemoveMethod
{

	public static void main(String[] args) throws IOException
	{
		if (args.length < 2) {
			System.out.println("usage: RemoveMethod <method name> file...");
			System.exit(1);
		}

		String methodName = args[0];

		List<Path> files = new ArrayList<>();

		for (int i = 1; i < args.length; i++) {
			Path file = Paths.get(args[i]);

			if (Files.isRegularFile(file)) {
				files.add(file);
			} else if (Files.isDirectory(file)) {
				files.addAll(PathUtil.findRecursive(file, "*.java"));
			}
		}

		System.out.println(String.format("Working on %d files", files.size()));
		System.out.println("method name: '" + methodName + "'");

		for (Path file : files) {
			ModifierRunner modifier = new ModifierRunner(
					new MethodRemoverFactory(methodName));
			modifier.transform(file);
		}
	}

}