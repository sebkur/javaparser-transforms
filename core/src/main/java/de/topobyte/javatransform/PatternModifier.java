package de.topobyte.javatransform;

import java.io.IOException;

public class PatternModifier implements TextModifier
{

	private String text;
	private String needle;
	private boolean literal;
	private String replacement;

	private boolean modified = false;

	public PatternModifier(String text, String needle, boolean literal,
			String replacement)
	{
		this.text = text;
		this.needle = needle;
		this.literal = literal;
		this.replacement = replacement;
	}

	@Override
	public boolean determineWillNeedModifications()
	{
		return text.contains(needle);
	}

	@Override
	public String transform() throws IOException
	{
		String newText;
		if (literal) {
			newText = text.replace(needle, replacement);
		} else {
			newText = text.replaceAll(needle, replacement);
		}
		modified = !newText.equals(text);
		return newText;
	}

	@Override
	public boolean isModified()
	{
		return modified;
	}

}
