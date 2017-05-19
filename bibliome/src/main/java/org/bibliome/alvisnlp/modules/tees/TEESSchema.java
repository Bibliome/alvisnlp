package org.bibliome.alvisnlp.modules.tees;

import java.util.Map;
import java.util.logging.Logger;

import org.bibliome.util.Checkable;

import alvisnlp.module.types.MultiMapping;

@SuppressWarnings("serial")
public class TEESSchema extends MultiMapping implements Checkable {
	public TEESSchema() {
		super();
	}

	@Override
	public boolean check(Logger logger) {
		boolean result = true;
		for (Map.Entry<String,String[]> e : entrySet()) {
			String[] roles = e.getValue();
			if (roles.length != 2) {
				logger.severe("TEES schema relation " + e.getKey() + " is not binary");
				result = false;
			}
		}
		return result;
	}
}
