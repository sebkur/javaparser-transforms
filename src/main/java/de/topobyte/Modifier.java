package de.topobyte;

import java.io.IOException;

public interface Modifier
{

	boolean isModified();

	void transform() throws IOException;

}
