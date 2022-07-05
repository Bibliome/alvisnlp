package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.rebert;

import java.util.Arrays;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.util.fragments.FragmentTag;
import fr.inra.maiage.bibliome.util.fragments.FragmentTagIterator;

public class Candidate {
	private final boolean asserted;
	private final Element supportElement;
	private final Argument subject;
	private final Argument object;
	private final String label;
	
	public Candidate(boolean asserted, REBERTPredict owner, EvaluationContext evalCtx, Element supportElement, Element subject, Element object, String label) throws ModuleException {
		super();
		this.asserted = asserted;
		this.supportElement = supportElement;
		this.subject = new Argument(owner, evalCtx, Argument.SUBJECT_TAGS, subject);
		this.object = new Argument(owner, evalCtx, Argument.OBJECT_TAGS, object);
		this.label = label;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((object == null) ? 0 : object.hashCode());
		result = prime * result + ((subject == null) ? 0 : subject.hashCode());
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
		Candidate other = (Candidate) obj;
		if (object == null) {
			if (other.object != null)
				return false;
		} else if (!object.equals(other.object))
			return false;
		if (subject == null) {
			if (other.subject != null)
				return false;
		} else if (!subject.equals(other.subject))
			return false;
		return true;
	}

	public boolean isAsserted() {
		return asserted;
	}

	public Element getSupportElement() {
		return supportElement;
	}

	public Argument getSubject() {
		return subject;
	}

	public Argument getObject() {
		return object;
	}

	public String getLabel() {
		return label;
	}

	public Object[] getRecord() {
		if (subject.getElement() == null) {
			if (object.getElement() == null) {
				return new String[] { "", "", "", label };
			}
			String taggedSentence = buildText(object);
			return new String[] { taggedSentence, taggedSentence, "", label };
		}
		if (object.getElement() == null) {
			String taggedSentence = buildText(subject);
			return new String[] { taggedSentence, taggedSentence, "", label };
		}
		if (subject.getSentence() == object.getSentence()) {
			String taggedSentence = buildText(subject, object);
			return new String[] { taggedSentence, taggedSentence, "", label };
		}
		String subjectTaggedSentence = buildText(subject);
		String objectTaggedSentence = buildText(object);
		if (subject.sentenceBefore(object)) {
			return new String[] { subjectTaggedSentence + " " + objectTaggedSentence, subjectTaggedSentence, objectTaggedSentence, label };
		}
		return new String[] { objectTaggedSentence + " " + subjectTaggedSentence, objectTaggedSentence, subjectTaggedSentence, label };
	}
	
	private static String buildText(Argument... argsInfo) {
		TextBuilder textBuilder = new TextBuilder();
		Annotation sentence = argsInfo[0].getSentence();
		FragmentTag.iterateFragments(textBuilder, sentence.getForm(), Arrays.asList(argsInfo), sentence.getStart());
		return textBuilder.getText();
	}
	
	private static class TextBuilder implements FragmentTagIterator<String,Argument> {
		private final StringBuilder result = new StringBuilder();

		@Override
		public void handleGap(String text, int from, int to) {
			result.append(text.substring(from, to));
		}

		@Override
		public void handleHead(String text, int to) {
			result.append(text.substring(0, to));
		}

		@Override
		public void handleTag(String text, FragmentTag<Argument> tag) {
			Argument arg = tag.getFragment();
			space();
			switch (tag.getTagType()) {
				case EMPTY: throw new RuntimeException();
				case OPEN: {
					result.append(arg.getOpeningTag());
					break;
				}
				case CLOSE: {
					result.append(arg.getClosingTag());
					break;
				}
			}
			space();
		}

		@Override
		public void handleTail(String text, int from) {
			result.append(text.substring(from));
		}
		
		public void space() {
			result.append(' ');
		}
		
		public String getText() {
			return result.toString();
		}
	}
}