package de.topobyte.javatransform;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;

public class TestUtil
{

	public static String load(String path) throws IOException
	{
		InputStream input = TestTIntHashSet.class.getClassLoader()
				.getResourceAsStream(path);
		return IOUtils.toString(input, StandardCharsets.UTF_8);
	}

	public static List<String> lines(String originalText)
	{
		return Arrays.asList(originalText.split("\\r?\\n"));
	}

}
