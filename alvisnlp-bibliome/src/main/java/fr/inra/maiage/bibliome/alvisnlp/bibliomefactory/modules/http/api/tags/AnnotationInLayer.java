package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.http.api.tags;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.util.fragments.Fragment;

public class AnnotationInLayer implements Fragment {
	private final Annotation annotation;
	private final String layerName;
	
	public AnnotationInLayer(Annotation annotation, String layerName) {
		super();
		this.annotation = annotation;
		this.layerName = layerName;
	}

	public Annotation getAnnotation() {
		return annotation;
	}

	public String getLayerName() {
		return layerName;
	}

	@Override
	public int getStart() {
		return annotation.getStart();
	}

	@Override
	public int getEnd() {
		return annotation.getEnd();
	}
}
