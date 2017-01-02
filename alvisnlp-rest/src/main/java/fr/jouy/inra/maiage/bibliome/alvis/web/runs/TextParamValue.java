package fr.jouy.inra.maiage.bibliome.alvis.web.runs;

public class TextParamValue extends AbstractStringParamValue {
	protected TextParamValue(String name, String value) {
		super(ParamValue.METHOD_TEXT, name, value);
	}

	@Override
	protected String getConcreteValue() {
		return getName();
	}
}
