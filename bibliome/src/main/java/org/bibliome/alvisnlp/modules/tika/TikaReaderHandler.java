package org.bibliome.alvisnlp.modules.tika;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Stack;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaMetadataKeys;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

class TikaReaderHandler extends DefaultHandler {
	private final String name;
	private final Metadata metadata = new Metadata();
	private boolean inBody = false;
	private final StringBuilder contents = new StringBuilder();
	private final Collection<ElementFragment> elements = new ArrayList<ElementFragment>();
	private final Stack<ElementFragment> openedElements = new Stack<ElementFragment>();

	TikaReaderHandler(String name) {
		super();
		this.name = name;
		metadata.add(TikaMetadataKeys.RESOURCE_NAME_KEY, name);
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		if (inBody) {
			contents.append(ch, start, length);
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		inBody = inBody && !localName.equals("body");
		if (inBody) {
			ElementFragment frag = openedElements.pop();
			frag.setEnd(contents.length());
			elements.add(frag);
		}
	}
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (inBody) {
			ElementFragment frag = new ElementFragment(localName, contents.length());
			openedElements.push(frag);
		}
		else {
			inBody = localName.equals("body");
		}
	}

	public String getName() {
		return name;
	}

	public Metadata getMetadata() {
		return metadata;
	}

	public String getContents() {
		return contents.toString();
	}

	public Collection<ElementFragment> getElements() {
		return elements;
	}
}
