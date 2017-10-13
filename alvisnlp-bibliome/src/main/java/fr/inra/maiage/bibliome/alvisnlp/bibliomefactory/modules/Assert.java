/*
Copyright 2016, 2017 Institut National de la Recherche Agronomique

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/


package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules;

import java.io.IOException;
import java.io.PrintStream;
import java.util.logging.Logger;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.Assert.AssertResolvedObjects;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Evaluator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.LibraryResolver;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.VariableLibrary;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.VariableLibrary.Variable;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.NameUsage;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AlvisNLPModule;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.util.Iterators;
import fr.inra.maiage.bibliome.util.streams.TargetStream;

@AlvisNLPModule
public class Assert extends CorpusModule<AssertResolvedObjects> {
	private Expression target;
	private Expression assertion;
	private Boolean severe = true;
	private Integer stopAt;
	private Expression message;
	private TargetStream outFile;
	
	class AssertResolvedObjects extends ResolvedObjects {
		private final Evaluator target;
		private final Evaluator assertion;
		private final Variable targetVariable;
		private final Evaluator message;
		
		private AssertResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
			super(ctx, Assert.this);
			VariableLibrary targetLib = new VariableLibrary("target");
			targetVariable = targetLib.newVariable(null);
			LibraryResolver assertionResolver = targetLib.newLibraryResolver(rootResolver);
			target = Assert.this.target.resolveExpressions(rootResolver);
			assertion = Assert.this.assertion.resolveExpressions(assertionResolver);
			message = assertionResolver.resolveNullable(Assert.this.message);
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
			super.collectUsedNames(nameUsage, defaultType);
			target.collectUsedNames(nameUsage, defaultType);
			assertion.collectUsedNames(nameUsage, defaultType);
			nameUsage.collectUsedNamesNullable(message, defaultType);
		}
		
		private String getMessage(EvaluationContext evalCtx, Element elt) {
			if (message == null) {
				return "assertion failed for " + elt;
			}
			return message.evaluateString(evalCtx, elt);
		}
	}
	
	@Override
	protected AssertResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new AssertResolvedObjects(ctx);
	}
	
	private PrintStream openStream() throws IOException {
		if (outFile == null) {
			return null;
		}
		return outFile.getPrintStream();
	}

	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		AssertResolvedObjects resObj = getResolvedObjects();
		Logger logger = getLogger(ctx);
		EvaluationContext evalCtx = new EvaluationContext(logger);
		try (PrintStream out = openStream()) {
			int failures = 0;
			int checked = 0;
			for (Element elt : Iterators.loop(resObj.target.evaluateElements(evalCtx, corpus))) {
				checked++;
				resObj.targetVariable.set(elt);
				if (!resObj.assertion.evaluateBoolean(evalCtx, elt)) {
					failures++;
					String message = resObj.getMessage(evalCtx, elt);
					logger.warning(message);
					if (out != null) {
						out.println(message);
					}
					if (stopAt != null && failures == stopAt.intValue())
						break;
				}
			}
			String msg = "assertion failures: " + failures;
			if (severe && failures > 0) {
				if (out != null) {
					out.println(msg);
				}
				processingException(msg);
			}
			else {
				logger.info("elements checked: " + checked);
				logger.info(msg);
			}
		}
		catch (IOException e) {
			rethrow(e);
		}
	}

	@Param
	public Expression getTarget() {
		return target;
	}

	@Param
	public Expression getAssertion() {
		return assertion;
	}

	@Param
	public Boolean getSevere() {
		return severe;
	}

	@Param(mandatory=false)
	public Integer getStopAt() {
		return stopAt;
	}

	@Param(mandatory=false)
	public Expression getMessage() {
		return message;
	}

	@Param(mandatory=false)
	public TargetStream getOutFile() {
		return outFile;
	}

	public void setOutFile(TargetStream outFile) {
		this.outFile = outFile;
	}

	public void setMessage(Expression message) {
		this.message = message;
	}

	public void setStopAt(Integer stopAt) {
		this.stopAt = stopAt;
	}

	public void setTarget(Expression target) {
		this.target = target;
	}

	public void setAssertion(Expression assertion) {
		this.assertion = assertion;
	}

	public void setSevere(Boolean severe) {
		this.severe = severe;
	}
}
