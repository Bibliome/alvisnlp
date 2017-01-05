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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContext;

import org.bibliome.util.FlushedStreamHandler;
import org.bibliome.util.Timer;
import org.bibliome.util.xml.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import alvisnlp.app.cli.CommandLineLogFormatter;
import alvisnlp.app.cli.CorpusCommandLineProcessingContext;
import alvisnlp.corpus.Corpus;
import alvisnlp.module.ProcessingContext;
import alvisnlp.module.Sequence;
import alvisnlp.module.TimerCategory;
import fr.jouy.inra.maiage.bibliome.alvis.web.executor.AlvisNLPExecutor;

public class Run {
	private static final String ALVISNLP_LOG_FILENAME = "alvisnlp.log";
	private static final String TMP_DIRNAME = "tmp";
	private static final String STATUS_DIRNAME = "status";
	private static final String OUTPUT_DIRNAME = "output";
	private static final String INPUT_DIRNAME = "input";

	private static final String RUN_FILENAME = "run.xml";

	private static final String PROCESSING_DIR_PREFIX = "alvisnlp-run-";
	
	private final String id;
	private final String planName;
	private final File processingDir;
	private final List<ParamValue<?>> paramValues = new ArrayList<ParamValue<?>>();
	private final List<RunStatus> runStatuses = new ArrayList<RunStatus>();
	private boolean setParam;
	private boolean finished = false;
	private boolean cancelled = false;
	private final Map<String,String> properties = new HashMap<String,String>();
	private final AlvisNLPExecutor executor;

	public Run(File rootProcessingDir, Sequence<Corpus> plan, AlvisNLPExecutor executor) throws IOException {
		this.planName = plan.getId();
		this.processingDir = Files.createTempDirectory(rootProcessingDir.toPath(), PROCESSING_DIR_PREFIX).toFile();
		String processingPath = this.processingDir.getName();
		this.id = processingPath.substring(PROCESSING_DIR_PREFIX.length(), processingPath.length());
		this.setParam = true;
		this.executor = executor;
		processingDir.mkdirs();
		getInputDir().mkdirs();
		getOutputDir().mkdirs();
		getStatusDir().mkdirs();
		addStatus(RunStatus.CREATED, "", false);
	}

	private Run(String id, File processingDir, String planName, AlvisNLPExecutor executor) {
		this.id = id;
		this.processingDir = processingDir;
		this.planName = planName;
		this.executor = executor;
	}

	public static Run read(File rootProcessingDir, String id) throws SAXException, IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		File processingDir = new File(rootProcessingDir, PROCESSING_DIR_PREFIX + id);
		File runFile = new File(processingDir, RUN_FILENAME);
		if (!runFile.exists()) {
			return null;
		}
		Document runDoc = XMLUtils.docBuilder.parse(runFile);
		Element runElt = runDoc.getDocumentElement();
		String planName = runElt.getAttribute("plan-name");
		String executorClassName = runElt.getAttribute("executor");
		Class<?> executorClass = Class.forName(executorClassName);
		AlvisNLPExecutor executor = (AlvisNLPExecutor) executorClass.newInstance();
		Run result = new Run(id, processingDir, planName, executor);
		result.readRunXML(runElt);
		result.readStatusDir();
		result.setParam = false;
		return result;
	}

	private void readRunXML(Element runElt) {
		for (Element elt : XMLUtils.childrenElements(runElt)) {
			String tagName = elt.getTagName();
			switch (tagName) {
				case "param-values":
					readXMLParams(elt);
					break;
				case "properties":
					readXMLProperties(elt);
					break;
			}
		}
	}
	
	private void readXMLParams(Element elt) {
		for (Element child : XMLUtils.childrenElements(elt)) {
			ParamValue<?> pv = ParamValue.create(child);
			paramValues.add(pv);
		}
	}
	
	private void readXMLProperties(Element elt) {
		for (Element child : XMLUtils.childrenElements(elt)) {
			String key = child.getTagName();
			String value = child.getTextContent();
			properties.put(key, value);
		}
	}

	private void readStatusDir() throws SAXException, IOException {
		for (File sf : getStatusDir().listFiles()) {
			Document sdoc = XMLUtils.docBuilder.parse(sf);
			Element se = sdoc.getDocumentElement();
			RunStatus runStatus = new RunStatus(se);
			runStatuses.add(runStatus);
			if (runStatus.isFinished()) {
				this.finished = true;
			}
			if (runStatus.getStatus().equals(RunStatus.CANCELLED)) {
				this.cancelled = true;
			}
		}
		Collections.sort(runStatuses, RunStatus.COMPARATOR);
	}

	public Document toXML(boolean includeStatuses) {
		Document result = XMLUtils.docBuilder.newDocument();
		Element runElt = XMLUtils.createRootElement(result, "alvisnlp-run");
		runElt.setAttribute("plan-name", planName);
		runElt.setAttribute("id", id);
		runElt.setAttribute("executor", executor.getClass().getCanonicalName());
		paramValuesToXML(result, runElt);
		propertiesToXML(result, runElt);
		if (includeStatuses) {
			statusesToXML(result, runElt);
		}
		return result;
	}
	
	public void write() {
		Document doc = toXML(false);
		XMLUtils.writeDOMToFile(doc, null, getRunFile());
	}
	
	private void paramValuesToXML(Document doc, Element runElt) {
		Element paramsElt = XMLUtils.createElement(doc, runElt, -1, "param-values");
		for (ParamValue<?> pv : paramValues) {
			pv.toXML(doc, paramsElt);
		}
	}
	
	private void propertiesToXML(Document doc, Element runElt) {
		Element propsElt = XMLUtils.createElement(doc, runElt, -1, "properties");
		for (Map.Entry<String,String> e : properties.entrySet()) {
			XMLUtils.createElement(doc, propsElt, -1, e.getKey(), e.getValue());
		}
	}

	private void statusesToXML(Document doc, Element runElt) {
		Element statusesElt = XMLUtils.createElement(doc, runElt, -1, "statuses");
		for (RunStatus rs : runStatuses) {
			rs.toXML(doc, statusesElt);
		}
	}

	public String getId() {
		return id;
	}

	public String getPlanName() {
		return planName;
	}
	
	public File getProcessingDir() {
		return processingDir;
	}

	public File getInputDir() {
		return new File(processingDir, INPUT_DIRNAME);
	}

	public File getOutputDir() {
		return new File(processingDir, OUTPUT_DIRNAME);
	}

	public File getStatusDir() {
		return new File(processingDir, STATUS_DIRNAME);
	}
	
	public File getRunFile() {
		return new File(processingDir, RUN_FILENAME);
	}

	public List<ParamValue<?>> getParamValues() {
		return Collections.unmodifiableList(paramValues);
	}

	public List<RunStatus> getRunStatuses() {
		return Collections.unmodifiableList(runStatuses);
	}
	
	public RunStatus getLastStatus() {
		return runStatuses.get(runStatuses.size() - 1);
	}
	
	public void addStatus(String status, String value, boolean finished) {
		RunStatus runStatus = new RunStatus(status, value, finished);
		Document statusDoc = runStatus.toXML();
		File statusFile = new File(getStatusDir(), status);
		XMLUtils.writeDOMToFile(statusDoc, null, statusFile);
		runStatuses.add(runStatus);
		this.finished = finished;
		if (status.equals(RunStatus.CANCELLED)) {
			cancelled = true;
		}
	}

	public boolean isSetParam() {
		return setParam;
	}

	public boolean isFinished() {
		return finished;
	}
	
	public boolean isCancelled() {
		return cancelled;
	}

	public Collection<String> getPropertyKeys() {
		return Collections.unmodifiableCollection(properties.keySet());
	}
	
	public String getProperty(String key) {
		return properties.get(key);
	}

	public void setProperty(String key, String value) {
		if (!setParam) {
			throw new IllegalStateException("cannot add parameter values");
		}
		properties.put(key, value);
	}

	public AlvisNLPExecutor getExecutor() {
		return executor;
	}
	
	public void process(PlanBuilder planBuilder) {
		ProcessingContext<Corpus> processingContext = getProcessingContext();
		Timer<TimerCategory> timer = processingContext.getTimer();
		timer.start();
		try {
			Logger logger = getLogger(processingContext);
			logger.info("result identifier: " + id);
			Sequence<Corpus> plan = planBuilder.buildPlan(this);
			planBuilder.setParams(this, plan);
			planBuilder.check(plan);
			addStatus(RunStatus.STARTED, "", false);
			plan.init(processingContext);
			processingContext.processCorpus(plan, new Corpus());
			addStatus(RunStatus.SUCCESS, "", true);
		}
		catch (Exception e) {
			addStatus(RunStatus.FAILURE, "", true);
		}
        finally {
        	timer.stop();
        }
	}

	public void execute(ServletContext servletContext, PlanBuilder planBuilder, boolean async) {
		executor.execute(servletContext, planBuilder, this, async);
	}

	public void cancel() {
		if (isCancelled()) {
			return;
		}
		executor.cancel(this);
		addStatus(RunStatus.CANCELLED, "", false);
	}
	
	private void addParamValue(ParamValue<?> paramValue) {
		if (!setParam) {
			throw new IllegalStateException("cannot add parameter values");
		}
		paramValues.add(paramValue);
	}
	
	public void addStringParamValue(String name, String value) {
		addParamValue(new StringParamValue(name, value));
	}
	
	public void addXMLParamValue(String name, String value) {
		addXMLParamValue(name, value);
	}

	public void addTextParamValue(String name, String contents) throws FileNotFoundException, UnsupportedEncodingException {
		try (PrintStream os = new PrintStream(new File(getInputDir(), name), "UTF-8")) {
			os.print(contents);
		}
		addParamValue(new TextParamValue(name, contents));
	}

	public void addUploadParamValue(String name, String fileName, InputStream contents) throws IOException {
		Files.copy(contents, new File(getInputDir(), fileName).toPath());
		addParamValue(new UploadParamValue(name, fileName));
	}
	
	public CorpusCommandLineProcessingContext getProcessingContext() {
		CorpusCommandLineProcessingContext result = new CorpusCommandLineProcessingContext(new Timer<TimerCategory>("alvisnlp", TimerCategory.MODULE));
    	result.setDumps(false);
    	result.setRootTempDir(new File(processingDir, TMP_DIRNAME));
    	return result;
	}
	
	public Logger getLogger(ProcessingContext<Corpus> processingContext) throws FileNotFoundException {
    	Logger result = processingContext.getLogger("alvisnlp");
    	result.setLevel(Level.INFO);
        result.setUseParentHandlers(false);
    	setHandlers(result, Level.INFO);
        return result;
    }

    public void setHandlers(Logger logger, Level logLevel) throws FileNotFoundException {
    	for (Handler h : logger.getHandlers()) {
    		logger.removeHandler(h);
    	}
    	Handler stderrHandler = new FlushedStreamHandler(System.err, CommandLineLogFormatter.INSTANCE);
    	logger.addHandler(stderrHandler);
    	stderrHandler.setLevel(logLevel);

    	Handler fileHandler = new FlushedStreamHandler(new FileOutputStream(new File(processingDir, ALVISNLP_LOG_FILENAME), false), CommandLineLogFormatter.INSTANCE);
        logger.addHandler(fileHandler);
    	fileHandler.setLevel(logLevel);
    }
}
