package fr.jouy.inra.maiage.bibliome.alvis.web.runs;

public class StringParamValue extends AbstractStringParamValue {
	protected StringParamValue(String name, String value) {
		super(ParamValue.METHOD_STRING, name, value);
	}

	@Override
	protected String getConcreteValue() {
		return getValue();
	}
}
