package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.rebert;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.library.standard.NavigationLibrary;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.rebert.REBERTPredict.REBERTPredictResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.DownCastElement;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Layer;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Section;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.util.fragments.Fragment;

public class Argument implements Fragment {
	public static final String[] SUBJECT_TAGS = new String[] { "@@", "@@" };
	public static final String[] OBJECT_TAGS = new String[] { "<<", ">>" };

	private final String[] tags;
	private final Element element;
	private final int start;
	private final int end;
	private final Section section;
	private final Annotation sentence;
	
	public Argument(REBERTBase owner, EvaluationContext evalCtx, String[] tags, Element element) throws ModuleException {
		this.tags = tags;
		REBERTPredictResolvedObjects resObj = owner.getResolvedObjects();
		this.element = element;
		start = resObj.getStart().evaluateInt(evalCtx, element);
		end = resObj.getEnd().evaluateInt(evalCtx, element);
		section = DownCastElement.toSection(NavigationLibrary.section(evalCtx, element).next());
		Layer sentenceLayer = section.ensureLayer(owner.getSentenceLayer());
		Layer enclosingSentences = sentenceLayer.including(start, end);
		if (enclosingSentences.size() == 0) {
			throw new ModuleException("could not find enclosing sentence for " + element + " :: " + sentenceLayer);
		}
		sentence = enclosingSentences.get(0);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((element == null) ? 0 : element.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Argument other = (Argument) obj;
		if (element == null) {
			if (other.element != null)
				return false;
		} else if (!element.equals(other.element))
			return false;
		return true;
	}

	protected boolean sentenceBefore(Argument other) {
		if (section.getDocument() != other.section.getDocument()) {
			return true;
		}
		if (section.getOrder() <= other.section.getOrder()) {
			return true;
		}
		return sentence.getStart() < other.sentence.getStart();
	}

	public String getOpeningTag() {
		return tags[0];
	}
	
	public String getClosingTag() {
		return tags[1];
	}

	public Element getElement() {
		return element;
	}

	public Section getSection() {
		return section;
	}

	public Annotation getSentence() {
		return sentence;
	}

	@Override
	public int getStart() {
		return start;
	}

	@Override
	public int getEnd() {
		return end;
	}
}