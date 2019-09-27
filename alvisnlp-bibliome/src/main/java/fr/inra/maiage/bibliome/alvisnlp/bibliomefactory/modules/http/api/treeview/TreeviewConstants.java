package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.http.api.treeview;

public enum TreeviewConstants {
	;

	public static enum Parameters {
		;

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
