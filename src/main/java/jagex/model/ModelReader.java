package jagex.model;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public abstract class ModelReader {

	private static final Logger logger = Logger.getLogger(ModelReader.class.getName());

	/**
	 * The map for registered readers.
	 */
	private static final Map<String, ModelReader> formats = new HashMap<>();

	static {
	}

	/**
	 * Gets the model reader for the specified format.
	 *
	 * @param format the format.
	 * @return the model reader. (null if one of this type does not exist.)
	 */
	public static final ModelReader get(String format) {
		return formats.get(format);
	}

	/**
	 * Registers a model reader.
	 *
	 * @param format the format.
	 * @param readerClass the reader class.
	 * @throws IllegalAccessException if the class or its nullary constructor is not accessible.
	 * @throws InstantiationException if this {@code Class} represents an abstract class, an interface, an array class,
	 * a primitive type, or void; or if the class has no nullary constructor; or if the instantiation fails for some
	 * other reason.
	 */
	public static final void register(String format, Class<? extends ModelReader> readerClass) throws InstantiationException, IllegalAccessException {
		if (formats.containsKey(format)) {
			return;
		}

		formats.put(format, readerClass.newInstance());
	}

	/**
	 * Reads the model.
	 *
	 * @param in the input stream.
	 * @return the read model.
	 * @throws IOException if there was an error reading.
	 */
	public abstract Model read(InputStream in) throws IOException;

	/**
	 * Reads the model from a url.
	 *
	 * @param url the url.
	 * @return the model.
	 * @throws IOException if there was an error reading.
	 */
	public Model read(URL url) throws IOException {
		Model m;
		try (InputStream in = url.openStream()) {
			m = read(in);
		}
		return m;
	}

	/**
	 * Reads the model from a file.
	 *
	 * @param file the file.
	 * @return the model.
	 * @throws IOException if there was an error reading.
	 */
	public Model read(File file) throws IOException {
		return read(file.toURI().toURL());
	}
}
