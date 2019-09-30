package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.http.api.tags;

import java.util.Comparator;

public enum AnnotationInLayerStableComparator implements Comparator<AnnotationInLayer> {
	INSTANCE;

	@Override
	public int compare(AnnotationInLayer a, AnnotationInLayer b) {
		return Integer.compare(System.identityHashCode(a), System.identityHashCode(b));
	}
}
