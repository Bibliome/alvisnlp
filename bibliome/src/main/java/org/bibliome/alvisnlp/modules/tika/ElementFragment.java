package org.bibliome.alvisnlp.modules.tika;

import org.bibliome.util.fragments.Fragment;

class ElementFragment implements Fragment {
	private final String name;
	private final int start;
	private int end;
	
	ElementFragment(String name, int start) {
		super();
		this.name = name;
		this.start = start;
	}

	String getName() {
		return name;
	}

	@Override
	public int getStart() {
		return start;
	}

	@Override
	public int getEnd() {
		return end;
	}

	void setEnd(int end) {
		this.end = end;
	}
}