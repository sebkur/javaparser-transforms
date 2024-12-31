package de.topobyte.javatransform;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

import de.topobyte.melon.paths.PathUtil;

public class SubstituteLiteral
{

	public static void main(String[] args) throws IOException
	{
		if (args.length < 3) {
			System.out
					.println("usage: SubstituteLiteral <text> <replacement> file...");
			System.exit(1);
		}

		String needle = args[0];
		String replacement = args[1];

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
		System.out.println("text: '" + needle + "'");
		System.out.println("replacement: '" + replacement + "'");

		for (Path file : files) {
			InputStream is = Files.newInputStream(file);
			String text = IOUtils.toString(is, StandardCharsets.UTF_8);
			is.close();
			PatternModifier modifier = new PatternModifier(text, needle, true,
					replacement);
			String newText = modifier.transform();
			if (modifier.isModified()) {
				System.out.println("modified: " + file);
				Files.write(file, newText.getBytes());
			}
		}
	}

}