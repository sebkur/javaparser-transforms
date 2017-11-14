package de.topobyte.javatransform;

import java.io.IOException;

public interface Modifier
{

	boolean determineWillNeedModifications();

	void transform() throws IOException;

	String postTransform(String text);

	boolean isModified();

}
