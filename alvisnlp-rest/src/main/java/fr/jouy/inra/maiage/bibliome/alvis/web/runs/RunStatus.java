/*
Copyright 2017 Institut National de la Recherche Agronomique

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package fr.jouy.inra.maiage.bibliome.alvis.web.runs;

import java.util.Comparator;

import org.bibliome.util.xml.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class RunStatus {
	public static final String FAILURE = "failure";
	public static final String SUCCESS = "success";
	public static final String STARTED = "started";
	public static final String CANCELLED = "cancelled";
	public static final String CREATED = "created";
	public static final String QUEUED = "queued";
	public static final String ABORTED = "aborted";
	public static final String SIGNALLED = "signalled";
	public static final String DISAPPEARED = "disappeared";
	public static final String INTERRUPTED = "iterrupted";

	private final String status;
	private final long timestamp;
	private final String value;
	private final boolean finished;
	
	private RunStatus(String status, long timestamp, String value, boolean finished) {
		super();
		this.status = status;
		this.timestamp = timestamp;
		this.value = value;
		this.finished = finished;
	}

	public RunStatus(String status, String value, boolean finished) {
		this(status, System.currentTimeMillis(), value, finished);
	}
	
	public RunStatus(Element elt) {
		this(elt.getAttribute("status"), Long.parseLong(elt.getAttribute("timestamp")), elt.getTextContent(), Boolean.parseBoolean(elt.getAttribute("finished")));
	}
	
	public Document toXML() {
		Document result = XMLUtils.docBuilder.newDocument();
		toXML(result, result);
		return result;
	}
	
	public Element toXML(Document doc, Node parent) {
		Element result = XMLUtils.createElement(doc, parent, -1, "status", value);
		result.setAttribute("status", status);
		result.setAttribute("timestamp", Long.toString(timestamp));
		result.setAttribute("finished", Boolean.toString(isFinished()));
		return result;
	}
	
	public String getStatus() {
		return status;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public String getValue() {
		return value;
	}

	public boolean isFinished() {
		return finished;
	}
	
	public static final Comparator<RunStatus> COMPARATOR = new Comparator<RunStatus>() {
		@Override
		public int compare(RunStatus run1, RunStatus run2) {
			return Long.compare(run1.timestamp, run2.timestamp);
		}
	};
}
