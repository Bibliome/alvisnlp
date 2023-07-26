package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.json.helper;

import java.util.List;
import java.util.Map;

import org.json.simple.parser.ContainerFactory;

public enum JContainerFactory implements ContainerFactory {
	INSTANCE;
	
	@SuppressWarnings("rawtypes")
	@Override
	public Map createObjectContainer() {
		return new JObject();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List creatArrayContainer() {
		return new JArray();
	}
}
