package fr.inra.maiage.bibliome.alvisnlp.core.module;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import fr.inra.maiage.bibliome.util.xml.XMLUtils;

public class ModuleAnalysis {
	private final Module<?> module;
	private final List<ResourceAnalysis> resourceAnalysis = new ArrayList<ResourceAnalysis>();
	private final List<ModuleAnalysis> children = new ArrayList<ModuleAnalysis>();
	
	public ModuleAnalysis(Module<?> module) {
		super();
		this.module = module;
		for (ParamHandler<?> ph : module.getAllParamHandlers()) {
			ResourceAnalysis r = ResourceAnalysis.build(ph);
			if (r != null) {
				resourceAnalysis.add(r);
			}
		}
		if (module instanceof Sequence) {
			Sequence<?> sequence = (Sequence<?>) module;
			for (Module<?> child : sequence.getSubModules()) {
				ModuleAnalysis ma = new ModuleAnalysis(child);
				children.add(ma);
			}
		}
	}

	public boolean isSourceInherited() {
		Sequence<?> parent = module.getSequence();
		if (parent == null) {
			return false;
		}
		String source = module.getModuleSourceName();
		String parentSource = parent.getModuleSourceName();
		return source.equals(parentSource);
	}

	public Module<?> getModule() {
		return module;
	}

	public List<ModuleAnalysis> getChildren() {
		return Collections.unmodifiableList(children);
	}

	public List<ResourceAnalysis> getResourceAnalysis() {
		return Collections.unmodifiableList(resourceAnalysis);
	}
	
	public boolean hasResouceAnalysis() {
		return !resourceAnalysis.isEmpty();
	}
	
	public boolean hasChildren() {
		return !children.isEmpty();
	}
	
	public boolean hasResouceAnalysisRecursive() {
		if (hasResouceAnalysis()) {
			return true;
		}
		for (ModuleAnalysis child : children) {
			if (child.hasResouceAnalysisRecursive()) {
				return true;
			}
		}
		return false;
	}
	
	public void toXML(Document doc, Element parent, boolean topLevel) {
		Element elt = XMLUtils.createElement(doc, parent, 0, module.getId());
		if (!isSourceInherited()) {
			elt.setAttribute("plan-source", module.getModuleSourceName());
		}
		if (topLevel) {
			for (ParamHandler<?> ph : module.getAllParamHandlers()) {
				Element pe = XMLUtils.createElement(doc, elt, 0, "param");
				pe.setAttribute("name", ph.getName());
				pe.setAttribute("type", ph.getType().getCanonicalName());
				ResourceAnalysis ra = ResourceAnalysis.build(ph);
				if (ra != null) {
					pe.setAttribute("mode", ra.getMode().toString());
				}
			}
		}
		if (!topLevel) {
			for (ResourceAnalysis ra : resourceAnalysis) {
				ra.toXML(doc, elt);
			}
		}
		for (ModuleAnalysis child : children) {
			if (child.hasResouceAnalysisRecursive()) { 
				child.toXML(doc, elt, false);
			}
		}
	}
}
