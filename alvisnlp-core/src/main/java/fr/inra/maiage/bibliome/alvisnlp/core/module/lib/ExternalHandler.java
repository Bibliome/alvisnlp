package fr.inra.maiage.bibliome.alvisnlp.core.module.lib;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import fr.inra.maiage.bibliome.alvisnlp.core.module.Annotable;
import fr.inra.maiage.bibliome.alvisnlp.core.module.Module;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.TimerCategory;
import fr.inra.maiage.bibliome.util.Timer;

public abstract class ExternalHandler<T extends Annotable,M extends Module<T>> {
	private final ProcessingContext<T> processingContext;
	private final M module;
	private final T annotable;
	private final File tempDir;
	private final Logger logger;
	
	protected ExternalHandler(ProcessingContext<T> processingContext, M module, T annotable) {
		super();
		this.processingContext = processingContext;
		this.module = module;
		this.annotable = annotable;
		this.tempDir = processingContext.getTempDir(module);
		this.logger = module.getLogger(processingContext);
	}
	
	public ProcessingContext<T> getProcessingContext() {
		return processingContext;
	}

	public M getModule() {
		return module;
	}

	public T getAnnotable() {
		return annotable;
	}
	
	public Logger getLogger() {
		return logger;
	}

	public File getTempDir() {
		return tempDir;
	}
	
	public File getTempFile(String name) {
		return new File(tempDir, name);
	}

	protected abstract void prepare() throws IOException, ModuleException;
	protected abstract void collect() throws IOException, ModuleException;
	protected abstract String getPrepareTask();
	protected abstract String getExecTask();
	protected abstract String getCollectTask();
	
	protected abstract List<String> getCommandLine();
	protected abstract void updateEnvironment(Map<String,String> env);
	protected abstract File getWorkingDirectory();
	protected abstract String getInputFileame();
	protected abstract String getOutputFilename();
	
	public File getInputFile() {
		return getTempFile(getInputFileame());
	}
	
	public File getOutputFile() {
		return getTempFile(getOutputFilename());
	}
	
	protected ProcessBuilder createProcessBuilder() {
		ProcessBuilder result = new ProcessBuilder(getCommandLine());
		updateEnvironment(result.environment());
		result.directory(getWorkingDirectory());
		if (getInputFileame() != null) {
			result.redirectInput(getInputFile());
		}
		if (getOutputFilename() == null) {
			result.redirectErrorStream(true);
		}
		else {
			result.redirectOutput(getOutputFile());
		}
		return result;
	}
	
	public void start(boolean collect) throws InterruptedException, IOException, ModuleException {
		doPrepare();
		doExec();
		if (collect) {
			doCollect();
		}
	}
	
	public void start() throws InterruptedException, IOException, ModuleException {
		start(true);
	}
	
	private void doPrepare() throws IOException, ModuleException {
		Timer<TimerCategory> prepareTimer = module.getTimer(processingContext, getPrepareTask(), TimerCategory.PREPARE_DATA, true);
		prepare();
		prepareTimer.stop();
	}
	
	private void doExec() throws IOException, InterruptedException, ProcessingException {
		Timer<TimerCategory> execTimer = module.getTimer(processingContext, getExecTask(), TimerCategory.EXTERNAL, true);
		ProcessBuilder processBuilder = createProcessBuilder();
		Process p = processBuilder.start();
		new Thread(new OutputLogger(logger, processBuilder.redirectErrorStream() ? p.getInputStream() : p .getErrorStream())).start();
		synchronized (p) {
			int retval = p.waitFor();
			if (retval != 0) {
				throw new ProcessingException("geniatagger returned " + retval);
			}
		}
		execTimer.stop();
	}
	
	public void doCollect() throws IOException, ModuleException {
		Timer<TimerCategory> collectTimer = module.getTimer(processingContext, getCollectTask(), TimerCategory.COLLECT_DATA, true);
		collect();
		collectTimer.stop();
	}
	
	private static class OutputLogger implements Runnable {
		private final Logger logger;
		private final InputStream is;
		
		private OutputLogger(Logger logger, InputStream is) {
			super();
			this.logger = logger;
			this.is = is;
		}

		@Override
		public void run() {
			try {
				BufferedReader r = new BufferedReader(new InputStreamReader(is));
				while (true) {
					String line = r.readLine();
					if (line == null) {
						break;
					}
					line = line.trim();
					logger.fine(line);
				}
			}
			catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}
}
