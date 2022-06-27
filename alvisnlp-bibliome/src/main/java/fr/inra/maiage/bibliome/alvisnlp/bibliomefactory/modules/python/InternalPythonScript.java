package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.python;

import java.util.Collections;
import java.util.List;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ConstantsLibrary;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression;
import fr.inra.maiage.bibliome.alvisnlp.core.module.types.ExpressionMapping;
import fr.inra.maiage.bibliome.util.streams.CompressionFilter;
import fr.inra.maiage.bibliome.util.streams.ResourceSourceStream;
import fr.inra.maiage.bibliome.util.streams.SourceStream;

public abstract class InternalPythonScript extends PythonScriptBase {
	protected abstract String getScriptName();

	protected abstract void fillScriptParams(ExpressionMapping mapping);

	@Override
	public ExpressionMapping getScriptParams() {
		ExpressionMapping result = new ExpressionMapping();
		fillScriptParams(result);
		return result;
	}

	@Override
	public String[] getCommandLine() {
		return new String[0];
	}

	@Override
	protected boolean isScriptCopy() {
		return true;
	}

	@Override
	public Boolean getCallPython() {
		return true;
	}

	@Override
	public SourceStream getScript() {
		String moduleClass = getModuleClass();
		int lastDot = moduleClass.lastIndexOf('.');
		String modulePackage = moduleClass.substring(0, lastDot);
		List<String> bases = Collections.singletonList(modulePackage.replace('.', '/'));
		String name = getScriptName();
		return new ResourceSourceStream("UTF-8", CompressionFilter.NONE, bases, name);
	}

	protected ExpressionMapping addScriptParam(ExpressionMapping mapping, String key, String value) {
		Expression expr = ConstantsLibrary.create(value);
		mapping.put(key, expr);
		return mapping;
	}

	protected ExpressionMapping addScriptParam(ExpressionMapping mapping, String key, boolean value) {
		Expression expr = ConstantsLibrary.create(value ? "yes" : "no");
		mapping.put(key, expr);
		return mapping;
	}

}