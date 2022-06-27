package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.python;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.NameType;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AlvisNLPModule;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.alvisnlp.core.module.types.ExpressionMapping;
import fr.inra.maiage.bibliome.util.streams.SourceStream;

@AlvisNLPModule(beta=true)
public abstract class PythonScript extends PythonScriptBase {
	private String[] commandLine;
	private Boolean callPython;
	private SourceStream script;
	private String[] layerNames;
	private String[] relationNames;
	private ExpressionMapping scriptParams = new ExpressionMapping();

	@Override
	protected boolean isScriptCopy() {
		return false;
	}

	@Override
	@Param(mandatory = false, nameType = NameType.LAYER)
	public String[] getLayerNames() {
		return layerNames;
	}

	@Override
	@Param(mandatory = false, nameType = NameType.RELATION)
	public String[] getRelationNames() {
		return relationNames;
	}

	@Override
	@Param
	public String[] getCommandLine() {
		return commandLine;
	}

	@Override
	@Param
	public Boolean getCallPython() {
		return callPython;
	}

	@Override
	@Param
	public SourceStream getScript() {
		return script;
	}

	@Override
	@Param
	public ExpressionMapping getScriptParams() {
		return scriptParams;
	}

	public void setLayerNames(String[] layerNames) {
		this.layerNames = layerNames;
	}

	public void setRelationNames(String[] relationNames) {
		this.relationNames = relationNames;
	}

	public void setCommandLine(String[] commandLine) {
		this.commandLine = commandLine;
	}

	public void setCallPython(Boolean callPython) {
		this.callPython = callPython;
	}

	public void setScript(SourceStream script) {
		this.script = script;
	}

	public void setScriptParams(ExpressionMapping scriptParams) {
		this.scriptParams = scriptParams;
	}
}
