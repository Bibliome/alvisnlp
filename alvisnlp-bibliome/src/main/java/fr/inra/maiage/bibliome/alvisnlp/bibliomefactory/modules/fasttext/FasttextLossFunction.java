package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.fasttext;

public enum FasttextLossFunction {
	SOFTMAX("softmax"),
	SKIPGRAM_NEGATICE_SAMPLING("ns"),
	SKIPGRAM_HIERARCHICAL_SOFTMAX("hs");
	
	public final String commandlineName;

	private FasttextLossFunction(String commandlineName) {
		this.commandlineName = commandlineName;
	}

	@Override
	public String toString() {
		return commandlineName;
	}
}
