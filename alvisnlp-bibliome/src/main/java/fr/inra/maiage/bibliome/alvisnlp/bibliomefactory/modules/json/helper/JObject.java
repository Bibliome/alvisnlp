package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.json.helper;

import java.io.IOException;
import java.io.Reader;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

@SuppressWarnings("serial")
public class JObject extends JSONObject {
	public JObject() {
		super();
	}

	public JObject(@SuppressWarnings("rawtypes") Map map) {
		super(map);
	}
	
	public static JObject parse(Reader reader) throws IOException, ParseException {
		JSONParser parser = new JSONParser();
		return (JObject) parser.parse(reader, JContainerFactory.INSTANCE);
	}
	
	public static JObject parse(String js) throws ParseException {
		JSONParser parser = new JSONParser();
		return (JObject) parser.parse(js, JContainerFactory.INSTANCE);
	}
	
	@SuppressWarnings("unchecked")
	public Iterable<Map.Entry<String,JObject>> objectEntries() {
		return (Iterable<Map.Entry<String,JObject>>) entrySet();
	}
	
	@SuppressWarnings("unchecked")
	public Iterable<Map.Entry<String,JArray>> arrayEntries() {
		return (Iterable<Map.Entry<String,JArray>>) entrySet();
	}
	
	@SuppressWarnings("unchecked")
	public Iterable<Map.Entry<String,String>> stringEntries() {
		return (Iterable<Map.Entry<String,String>>) entrySet();
	}
	
	@SuppressWarnings("unchecked")
	public Iterable<Map.Entry<String,Long>> longEntries() {
		return (Iterable<Map.Entry<String,Long>>) entrySet();
	}
	
	@SuppressWarnings("unchecked")
	public Iterable<Map.Entry<String,Integer>> integerEntries() {
		return (Iterable<Map.Entry<String,Integer>>) entrySet();
	}
	
	@SuppressWarnings("unchecked")
	public Iterable<Map.Entry<String,Boolean>> booleanEntries() {
		return (Iterable<Map.Entry<String,Boolean>>) entrySet();
	}

	public JObject getObject(String key) {
		return (JObject) get(key);
	}
	
	public JArray getArray(String key) {
		return (JArray) get(key);
	}
	
	public String getString(String key) {
		return (String) get(key);
	}
	
	public long getLong(String key) {
		return (long) get(key);
	}
	
	public int getInt(String key) {
		return (int) (long) get(key);
	}
	
	public boolean getBoolean(String key) {
		return (boolean) get(key);
	}
}