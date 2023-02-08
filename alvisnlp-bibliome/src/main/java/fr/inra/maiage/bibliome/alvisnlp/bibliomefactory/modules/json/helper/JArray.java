package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.json.helper;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

@SuppressWarnings("serial")
public class JArray extends JSONArray {
	public JArray() {
		super();
	}
	
	public static JArray parse(Reader reader) throws IOException, ParseException {
		JSONParser parser = new JSONParser();
		return (JArray) parser.parse(reader, JContainerFactory.INSTANCE);
	}
	
	public static JArray parse(String js) throws ParseException {
		JSONParser parser = new JSONParser();
		return (JArray) parser.parse(js, JContainerFactory.INSTANCE);
	}
	
	@SuppressWarnings("unchecked")
	public List<JObject> asObjectArray() {
		return (List<JObject>) this;
	}
	
	@SuppressWarnings("unchecked")
	public List<JArray> asArrayArray() {
		return (List<JArray>) this;
	}
	
	@SuppressWarnings("unchecked")
	public List<String> asStringArray() {
		return (List<String>) this;
	}
	
	@SuppressWarnings("unchecked")
	public List<Long> asLongArray() {
		return (List<Long>) this;
	}
	
	@SuppressWarnings("unchecked")
	public List<Integer> asIntegerArray() {
		return (List<Integer>) this;
	}
	
	@SuppressWarnings("unchecked")
	public List<Boolean> asBooleanArray() {
		return (List<Boolean>) this;
	}

	public JObject getObject(int index) {
		return (JObject) get(index);
	}
	
	public JArray getArray(int index) {
		return (JArray) get(index);
	}
	
	public String getString(int index) {
		return (String) get(index);
	}
	
	public long getLong(int index) {
		return (long) get(index);
	}
	
	public int getInt(int index) {
		return (int) (long) get(index);
	}
	
	public boolean getBoolean(int index) {
		return (boolean) get(index);
	}		
}