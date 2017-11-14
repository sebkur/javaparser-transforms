package de.topobyte;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import de.topobyte.melon.paths.PathUtil;

public class RemoveExternalizable
{

	public static void main(String[] args) throws IOException
	{
		List<Path> files = new ArrayList<>();

		for (String arg : args) {
			Path file = Paths.get(arg);

			if (Files.isRegularFile(file)) {
				files.add(file);
			} else if (Files.isDirectory(file)) {
				files.addAll(PathUtil.findRecursive(file, "*.java"));
			}
		}

		System.out.println(String.format("Working on %d files", files.size()));

		for (Path file : files) {
			ModifierRunner modifier = new ModifierRunner(
					new ExternalizableRemoverFactory());
			modifier.transform(file);
		}
	}

}