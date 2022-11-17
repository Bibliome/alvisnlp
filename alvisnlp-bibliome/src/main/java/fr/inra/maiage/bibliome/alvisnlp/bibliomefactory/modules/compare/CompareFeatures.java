package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.compare;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.TreeSet;
import java.util.logging.Logger;

import javax.ws.rs.ProcessingException;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.CorpusModule;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.ResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.compare.CompareFeatures.CompareFeaturesResolvedObjects;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.NameType;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Evaluator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.NameUsage;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AlvisNLPModule;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.util.Iterators;
import fr.inra.maiage.bibliome.util.Pair;
import fr.inra.maiage.bibliome.util.streams.TargetStream;

@AlvisNLPModule
public class CompareFeatures extends CorpusModule<CompareFeaturesResolvedObjects> {
	private Expression items;
	private String referenceFeature;
	private String predictedFeature;
	private String[] classesOfInterest;
	private TargetStream outFile;

	static class CompareFeaturesResolvedObjects extends ResolvedObjects {
		private final Evaluator items;

		public CompareFeaturesResolvedObjects(ProcessingContext<Corpus> ctx, CompareFeatures module) throws ResolverException {
			super(ctx, module);
			this.items = module.items.resolveExpressions(rootResolver);
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
			super.collectUsedNames(nameUsage, defaultType);
			this.items.collectUsedNames(nameUsage, defaultType);
		}
	}

	@Override
	protected CompareFeaturesResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new CompareFeaturesResolvedObjects(ctx, this);
	}

	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		CompareFeaturesResolvedObjects resObj = getResolvedObjects();
		Logger logger = getLogger(ctx);
		EvaluationContext evalCtx = new EvaluationContext(logger);
		Collection<String> allClasses = new TreeSet<String>();
		Collection<Pair<String,String>> pairs = new ArrayList<Pair<String,String>>();
		for (Element elt : Iterators.loop(resObj.items.evaluateElements(evalCtx, corpus))) {
			String refClass = elt.getLastFeature(referenceFeature);
			String predClass = elt.getLastFeature(predictedFeature);
			if (refClass == null) {
				refClass = "";
			}
			if (predClass == null) {
				predClass = "";
			}
			allClasses.add(refClass);
			allClasses.add(predClass);
			pairs.add(new Pair<String,String>(refClass, predClass));
		}
		Collection<String> displayClasses = classesOfInterest == null ? allClasses : Arrays.asList(classesOfInterest);
		try (PrintStream out = outFile.getPrintStream()) {
			String indent = "";
			Metric.COUNT_ITEMS.message(out, logger, indent, null, pairs);
			Metric.TRUE_POSITIVES.message(out, logger, indent, null, pairs);
			Metric.ACCURACY.message(out, logger, indent, null, pairs);
			indent = "    ";
			for (String theClass : displayClasses) {
				out.println("metrics for class " + theClass);
				logger.info("metrics for class " + theClass);
				Metric.COUNT_CLASS_REFERENCE.message(out, logger, indent, theClass, pairs);
				Metric.COUNT_CLASS_PREDICTION.message(out, logger, indent, theClass, pairs);
				Metric.TRUE_POSITIVES.message(out, logger, indent, theClass, pairs);
				Metric.RECALL.message(out, logger, indent, theClass, pairs);
				Metric.PRECISION.message(out, logger, indent, theClass, pairs);
				Metric.F_SCORE.message(out, logger, indent, theClass, pairs);
			}
		}
		catch (IOException e) {
			throw new ProcessingException(e);
		}
	}

	private static enum Metric {
		COUNT_ITEMS("items") {
			@Override
			double compute(String theClass, Collection<Pair<String, String>> pairs) {
				return pairs.size();
			}
		},

		ACCURACY("accuracy") {
			@Override
			double compute(String theClass, Collection<Pair<String, String>> pairs) {
				return TRUE_POSITIVES.compute(null, pairs) / COUNT_ITEMS.compute(null, pairs);
			}
		},

		COUNT_CLASS_REFERENCE("reference") {
			@Override
			double compute(String theClass, Collection<Pair<String, String>> pairs) {
				int result = 0;
				for (Pair<String,String> p : pairs) {
					if (p.first.equals(theClass)) {
						result++;
					}
				}
				return result;
			}
		},

		COUNT_CLASS_PREDICTION("prediction") {
			@Override
			double compute(String theClass, Collection<Pair<String, String>> pairs) {
				int result = 0;
				for (Pair<String,String> p : pairs) {
					if (p.second.equals(theClass)) {
						result++;
					}
				}
				return result;
			}
		},

		TRUE_POSITIVES("true positives") {
			@Override
			double compute(String theClass, Collection<Pair<String, String>> pairs) {
				int result = 0;
				for (Pair<String,String> p : pairs) {
					if (p.first.equals(p.second) && ((theClass == null) || p.first.equals(theClass))) {
						result++;
					}
				}
				return result;
			}
		},

		RECALL("recall") {
			@Override
			double compute(String theClass, Collection<Pair<String, String>> pairs) {
				return TRUE_POSITIVES.compute(theClass, pairs) / COUNT_CLASS_REFERENCE.compute(theClass, pairs);
			}
		},

		PRECISION("precision") {
			@Override
			double compute(String theClass, Collection<Pair<String, String>> pairs) {
				return TRUE_POSITIVES.compute(theClass, pairs) / COUNT_CLASS_PREDICTION.compute(theClass, pairs);
			}
		},

		F_SCORE("f-score") {
			@Override
			double compute(String theClass, Collection<Pair<String, String>> pairs) {
				double recall = RECALL.compute(theClass, pairs);
				double precision = PRECISION.compute(theClass, pairs);
				return 2 * recall * precision / (recall + precision);
			}
		};

		private final String name;
		private Metric(String name) {
			this.name = name;
		}

		private String getMessage(String indent, String theClass, Collection<Pair<String, String>> pairs) {
			return indent + name + " = " + compute(theClass, pairs);
		}

		private void message(PrintStream out, Logger logger, String indent, String theClass, Collection<Pair<String, String>> pairs) {
			String msg = getMessage(indent, theClass, pairs);
			out.println(msg);
			logger.info(msg);
		}

		abstract double compute(String theClass, Collection<Pair<String,String>> pairs);
	}

	@Param
	public Expression getItems() {
		return items;
	}

	@Param(nameType=NameType.FEATURE)
	public String getReferenceFeature() {
		return referenceFeature;
	}

	@Param(nameType=NameType.FEATURE)
	public String getPredictedFeature() {
		return predictedFeature;
	}

	@Param(mandatory=false)
	public String[] getClassesOfInterest() {
		return classesOfInterest;
	}

	@Param
	public TargetStream getOutFile() {
		return outFile;
	}

	public void setItems(Expression items) {
		this.items = items;
	}

	public void setReferenceFeature(String referenceFeature) {
		this.referenceFeature = referenceFeature;
	}

	public void setPredictedFeature(String predictedFeature) {
		this.predictedFeature = predictedFeature;
	}

	public void setClassesOfInterest(String[] classesOfInterest) {
		this.classesOfInterest = classesOfInterest;
	}

	public void setOutFile(TargetStream outFile) {
		this.outFile = outFile;
	}
}
