package de.topobyte.javatransform;

import java.io.IOException;

public interface TextModifier
{

	boolean determineWillNeedModifications();

	String transform() throws IOException;

	boolean isModified();

}
