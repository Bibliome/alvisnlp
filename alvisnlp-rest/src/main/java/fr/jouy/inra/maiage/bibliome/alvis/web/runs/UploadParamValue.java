package fr.jouy.inra.maiage.bibliome.alvis.web.runs;

public class UploadParamValue extends AbstractStringParamValue {
	protected UploadParamValue(String name, String value) {
		super(ParamValue.METHOD_UPLOAD, name, value);
	}

	@Override
	protected String getConcreteValue() {
		return getValue();
	}
}
