package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.http.api;

public enum Constants {
	;

	public static enum Parameters {
		;

		public static final String LAYER_NAME = "layer";
		public static final String LAYERS = "layers[]";
		public static final String DOCUMENT_ID = "docId";
		public static final String ELEMENT_ID = "eltId";
		public static final String NODE_ID = "parentId";
		public static final String EXPRESSION = "expr";
	}
	
	public static enum NodeIdFunctors {
		;
		public static final String CHILDREN = "children";
		public static final String FEATURES = "features";
		public static final String ANNOTATIONS = "annotations";
		public static final String EVALUATE = "evaluate";
	}
}
