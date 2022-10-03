package fr.inra.maiage.bibliome.alvisnlp.core.module;

import java.io.File;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import fr.inra.maiage.bibliome.util.files.ExecutableFile;
import fr.inra.maiage.bibliome.util.files.InputDirectory;
import fr.inra.maiage.bibliome.util.files.InputFile;
import fr.inra.maiage.bibliome.util.files.OutputDirectory;
import fr.inra.maiage.bibliome.util.files.OutputFile;
import fr.inra.maiage.bibliome.util.streams.SourceStream;
import fr.inra.maiage.bibliome.util.streams.TargetStream;
import fr.inra.maiage.bibliome.util.xml.XMLUtils;

public class ResourceAnalysis {
	private final ParamHandler<?> paramHandler;
	private final String location;
	private final Mode mode;

	private ResourceAnalysis(ParamHandler<?> paramHandler, String location, Mode mode) {
		super();
		this.paramHandler = paramHandler;
		this.location = location;
		this.mode = mode;
	}

	static ResourceAnalysis build(ParamHandler<?> paramHandler) {
		if (!paramHandler.isSet()) {
			return null;
		}
		Object value = paramHandler.getValue();
		Class<?> type = paramHandler.getType();
		if (InputFile.class.isAssignableFrom(type)) {
			return new ResourceAnalysis(paramHandler, getFileName((InputFile) value), Mode.READ);
		}
		if (OutputFile.class.isAssignableFrom(type)) {
			return new ResourceAnalysis(paramHandler, getFileName((OutputFile) value), Mode.WRITE);
		}
		if (ExecutableFile.class.isAssignableFrom(type)) {
			return new ResourceAnalysis(paramHandler, getFileName((ExecutableFile) value), Mode.EXECUTE);
		}
		if (InputDirectory.class.isAssignableFrom(type)) {
			return new ResourceAnalysis(paramHandler, getFileName((InputDirectory) value), Mode.READ);
		}
		if (OutputDirectory.class.isAssignableFrom(type)) {
			return new ResourceAnalysis(paramHandler, getFileName((OutputDirectory) value), Mode.WRITE);
		}
		if (File.class.isAssignableFrom(type)) {
			return new ResourceAnalysis(paramHandler, getFileName((File) value), Mode.UNKNOWN);
		}
		if (SourceStream.class.isAssignableFrom(type)) {
			return new ResourceAnalysis(paramHandler, getSourceStreamName((SourceStream) value), Mode.READ);
		}
		if (TargetStream.class.isAssignableFrom(type)) {
			return new ResourceAnalysis(paramHandler, getTargetStreamName((TargetStream) value), Mode.WRITE);
		}
		return null;
	}
	
	private static String getFileName(File f) {
		if (f == null) {
			return null;
		}
		return f.getAbsolutePath();
	}
	
	private static String getSourceStreamName(SourceStream s) {
		if (s == null) {
			return null;
		}
		return s.toString();
	}
	
	private static String getTargetStreamName(TargetStream s) {
		if (s == null) {
			return null;
		}
		return s.toString();
	}

	public static enum Mode {
		READ,
		WRITE,
		EXECUTE,
		UNKNOWN;
	}

	public ParamHandler<?> getParamHandler() {
		return paramHandler;
	}

	public String getLocation() {
		return location;
	}
	
	public boolean hasLocation() {
		return location != null;
	}

	public Mode getMode() {
		return mode;
	}

	public void toXML(Document doc, Element parent) {
		if (location != null) {
			Element elt = XMLUtils.createElement(doc, parent, 0, "resource");
			elt.setAttribute("param", paramHandler.getName());
			elt.setAttribute("mode", mode.toString());
			XMLUtils.createText(doc, elt, location);
		}
	}
}
