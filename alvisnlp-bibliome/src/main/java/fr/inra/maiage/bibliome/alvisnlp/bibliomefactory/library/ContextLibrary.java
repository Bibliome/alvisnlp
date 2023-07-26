package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.library;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.DefaultNames;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.DownCastElement;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Layer;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.NameType;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Section;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Function;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.FunctionLibrary;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Library;

@Library("ctx")
public abstract class ContextLibrary extends FunctionLibrary {
	@Function
	public static final String before(EvaluationContext ctx, Element elt, int size) {
		Annotation a = DownCastElement.toAnnotation(elt);
		if (a == null) {
			return "";
		}
		Section sec = a.getSection();
		String contents = sec.getContents();
		int start = a.getStart();
		int from = Math.max(0, start - size);
		return contents.substring(from, start);
	}

	@Function
	public static final String after(EvaluationContext ctx, Element elt, int size) {
		Annotation a = DownCastElement.toAnnotation(elt);
		if (a == null) {
			return "";
		}
		Section sec = a.getSection();
		String contents = sec.getContents();
		int end = a.getEnd();
		int to = Math.min(contents.length(), end + size);
		return contents.substring(end, to);
	}

	@Function(firstFtor = "sentence-before")
	public static final String sentenceBefore(EvaluationContext ctx, Element elt) {
		return sentenceBefore(ctx, elt, DefaultNames.getSentenceLayer());
	}

	@Function(firstFtor = "sentence-before", ftors = 1, nameTypes = {NameType.LAYER})
	public static final String sentenceBefore(EvaluationContext ctx, Element elt, String layerName) {
		Annotation a = DownCastElement.toAnnotation(elt);
		if (a == null) {
			return "";
		}
		Section sec = a.getSection();
		if (!sec.hasLayer(layerName)) {
			return "";
		}
		Layer layer = sec.getLayer(layerName);
		Layer sentences = layer.including(a);
		if (sentences.isEmpty()) {
			return "";
		}
		Annotation sent = sentences.first();
		String contents = sec.getContents();
		return contents.substring(sent.getStart(), a.getStart());
	}

	@Function(firstFtor = "sentence-after")
	public static final String sentenceAfter(EvaluationContext ctx, Element elt) {
		return sentenceAfter(ctx, elt, DefaultNames.getSentenceLayer());
	}
	
	@Function(firstFtor = "sentence-after", ftors = 1, nameTypes = {NameType.LAYER})
	public static final String sentenceAfter(EvaluationContext ctx, Element elt, String layerName) {
		Annotation a = DownCastElement.toAnnotation(elt);
		if (a == null) {
			return "";
		}
		Section sec = a.getSection();
		if (!sec.hasLayer(layerName)) {
			return "";
		}
		Layer layer = sec.getLayer(layerName);
		Layer sentences = layer.including(a);
		if (sentences.isEmpty()) {
			return "";
		}
		Annotation sent = sentences.first();
		String contents = sec.getContents();
		return contents.substring(a.getEnd(), sent.getEnd());
	}

	@Function(firstFtor = "words-before")
	public static final String wordsBefore(EvaluationContext ctx, Element elt, int size) {
		return wordsBefore(ctx, elt, DefaultNames.getWordLayer(), size);
	}
	
	@Function(firstFtor = "words-before", ftors = 1, nameTypes = {NameType.LAYER})
	public static final String wordsBefore(EvaluationContext ctx, Element elt, String layerName, int size) {
		Annotation a = DownCastElement.toAnnotation(elt);
		if (a == null) {
			return "";
		}
		Section sec = a.getSection();
		if (!sec.hasLayer(layerName)) {
			return "";
		}
		Layer layer = sec.getLayer(layerName);
		Layer words = layer.before(a.getStart());
		if (words.isEmpty()) {
			return "";
		}
		String contents = sec.getContents();
		if (words.size() < size) {
			Annotation first = words.first();
			return contents.substring(first.getStart(), a.getStart());
		}
		Annotation first = words.get(words.size() - size);
		return contents.substring(first.getStart(), a.getStart());
	}

	@Function(firstFtor = "words-after")
	public static final String wordsAfter(EvaluationContext ctx, Element elt, int size) {
		return wordsAfter(ctx, elt, DefaultNames.getWordLayer(), size);
	}
	
	@Function(firstFtor = "words-after", ftors = 1, nameTypes = {NameType.LAYER})
	public static final String wordsAfter(EvaluationContext ctx, Element elt, String layerName, int size) {
		Annotation a = DownCastElement.toAnnotation(elt);
		if (a == null) {
			return "";
		}
		Section sec = a.getSection();
		if (!sec.hasLayer(layerName)) {
			return "";
		}
		Layer layer = sec.getLayer(layerName);
		Layer words = layer.before(a.getStart());
		if (words.isEmpty()) {
			return "";
		}
		String contents = sec.getContents();
		if (words.size() < size) {
			Annotation last = words.last();
			return contents.substring(a.getEnd(), last.getEnd());
		}
		Annotation last = words.get(size);
		return contents.substring(a.getEnd(), last.getEnd());
	}
}
