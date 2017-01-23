/*
Copyright 2017 Institut National de la Recherche Agronomique

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

package fr.jouy.inra.maiage.bibliome.alvis.web.runs;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.bibliome.util.Checkable;
import org.bibliome.util.FlushedStreamHandler;
import org.bibliome.util.service.ServiceException;
import org.bibliome.util.service.UnsupportedServiceException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import alvisnlp.app.cli.CommandLineLogFormatter;
import alvisnlp.converters.CompoundParamConverterFactory;
import alvisnlp.converters.ConverterException;
import alvisnlp.converters.ParamConverterFactory;
import alvisnlp.corpus.Corpus;
import alvisnlp.factory.CompoundCorpusModuleFactory;
import alvisnlp.factory.CorpusModuleFactory;
import alvisnlp.module.MissingParameterException;
import alvisnlp.module.ModuleException;
import alvisnlp.module.ParamHandler;
import alvisnlp.module.ParameterException;
import alvisnlp.module.ParameterValueConstraintException;
import alvisnlp.module.Sequence;
import alvisnlp.plan.PlanException;
import alvisnlp.plan.PlanLoader;
import fr.jouy.inra.maiage.bibliome.alvis.web.AlvisNLPContextParameter;

public class PlanBuilder {
	private static final String ALVISNLP_LOGGER_NAME = "alvisnlp-rest.plan-builder";
	private final File planDir;
	private final File resourceDir;
	private final Logger logger;
	private final CompoundCorpusModuleFactory moduleFactory;
	private final CompoundParamConverterFactory converterFactory;

	public PlanBuilder(ServletContext servletContext) {
		this(
				AlvisNLPContextParameter.PLAN_DIR.getFileValue(servletContext),
				AlvisNLPContextParameter.RESOURCE_DIR.getFileValue(servletContext)
				);
	}
	
	public PlanBuilder(File planDir, File resourceDir) {
		super();

		this.planDir = planDir;
		this.resourceDir = resourceDir;

		logger = Logger.getLogger(ALVISNLP_LOGGER_NAME);
		Level logLevel = Level.INFO;
		logger.setLevel(logLevel);
		logger.setUseParentHandlers(false);
    	for (Handler h : logger.getHandlers()) {
    		logger.removeHandler(h);
    	}
    	Handler stderrHandler = new FlushedStreamHandler(System.err, CommandLineLogFormatter.INSTANCE);
    	logger.addHandler(stderrHandler);
    	stderrHandler.setLevel(logLevel);
    	
        moduleFactory = new CompoundCorpusModuleFactory();
        moduleFactory.loadServiceFactories(CorpusModuleFactory.class, null, null, null);

        converterFactory = new CompoundParamConverterFactory();
        converterFactory.loadServiceFactories(ParamConverterFactory.class, null, null, null);
	}
	
	private static DocumentBuilder createDocumentBuilder() throws ParserConfigurationException {
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        docBuilderFactory.setNamespaceAware(true);
        docBuilderFactory.setFeature("http://xml.org/sax/features/use-entity-resolver2", true);
        return docBuilderFactory.newDocumentBuilder();
	}
	
	public PlanLoader<Corpus> createPlanLoader() throws ParserConfigurationException, PlanException {
        DocumentBuilder docBuilder = createDocumentBuilder();
		return new PlanLoader<Corpus>(moduleFactory, converterFactory, null, null, null, docBuilder, "creator", Collections.<String,String> emptyMap());
	}
	
	public PlanLoader<Corpus> createPlanLoader(Run run) throws ParserConfigurationException, PlanException {
        DocumentBuilder docBuilder = createDocumentBuilder();
        File inputDir = run.getInputDir();
        String inputPath = inputDir.getAbsolutePath();
        String resourcePath = resourceDir.getAbsolutePath();
        List<String> inputPaths = new ArrayList<String>();
        inputPaths.add(inputPath);
        inputPaths.add(resourcePath);
        File outputDir = run.getOutputDir();
        String outputPath = outputDir.getAbsolutePath();
		return new PlanLoader<Corpus>(moduleFactory, converterFactory, null, inputPaths, outputPath, docBuilder, "creator", Collections.<String,String> emptyMap());
	}
	
	public Sequence<Corpus> buildPlan(PlanLoader<Corpus> planLoader, String planName) throws PlanException, ModuleException, ServiceException, ConverterException, SAXException, IOException, URISyntaxException {
		File planFile = new File(planDir, planName + ".plan");
		String planPath = planFile.getAbsolutePath();
		if (!planFile.exists()) {
			return null;
		}
		Document doc = planLoader.parseDoc(planPath);
		return planLoader.loadDocument(logger, planPath, doc);
	}

	public Sequence<Corpus> buildPlan(String planName) throws SAXException, IOException, URISyntaxException, ModuleException, ServiceException, ConverterException, PlanException, ParserConfigurationException {
		return buildPlan(createPlanLoader(), planName);
	}
	
	public Sequence<Corpus> buildPlan(Run run) throws PlanException, ModuleException, ServiceException, ConverterException, SAXException, IOException, URISyntaxException, ParserConfigurationException {
		return buildPlan(createPlanLoader(run), run.getPlanName());
	}

	public static void setParams(PlanLoader<Corpus> planLoader, Run run, Sequence<Corpus> plan) throws ParameterException, UnsupportedServiceException, PlanException, ConverterException, SAXException, IOException, URISyntaxException {
		for (ParamValue<?> pv : run.getParamValues()) {
			pv.setParam(planLoader, plan);
		}
	}
	
	public void setParams(Run run, Sequence<Corpus> plan) throws ParserConfigurationException, PlanException, ParameterException, UnsupportedServiceException, ConverterException, SAXException, IOException, URISyntaxException {
		setParams(createPlanLoader(run), run, plan);
	}

	public void check(Sequence<Corpus> plan) throws ModuleException {
		for (ParamHandler<Corpus> paramHandler : plan.getAllParamHandlers()) {
			if (!paramHandler.isSet()) {
				if (paramHandler.isMandatory()) {
					throw new MissingParameterException(paramHandler.getName());
				}
				continue;
			}
			if (paramHandler.isInhibitCheck()) {
				continue;
			}
			Class<?> type = paramHandler.getType();
			if (isCheckable(type)) {
				checkCheckable(paramHandler, paramHandler.getValue());
			}
			else if (type.isArray()) {
				if (isCheckable(type.getComponentType())) {
					for (Object value : (Object[]) paramHandler.getValue())
						checkCheckable(paramHandler, value);
				}
			}
		}
	}
	
	private static boolean isCheckable(Class<?> type) {
		return Checkable.class.isAssignableFrom(type);
	}
	
	private void checkCheckable(ParamHandler<Corpus> paramHandler, Object value) throws ParameterValueConstraintException {
		Checkable checkable = (Checkable) value;
		if (!checkable.check(logger)) {
			throw new ParameterValueConstraintException("bad parameter range", paramHandler.getName());
		}
	}

	public File getPlanDir() {
		return planDir;
	}

	public CompoundCorpusModuleFactory getModuleFactory() {
		return moduleFactory;
	}

	public CompoundParamConverterFactory getConverterFactory() {
		return converterFactory;
	}
}
