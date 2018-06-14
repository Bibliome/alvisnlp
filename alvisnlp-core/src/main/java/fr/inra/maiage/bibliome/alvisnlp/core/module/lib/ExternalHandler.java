package fr.inra.maiage.bibliome.alvisnlp.core.module.lib;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

import fr.inra.maiage.bibliome.alvisnlp.core.module.Annotable;
import fr.inra.maiage.bibliome.alvisnlp.core.module.Module;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
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

	protected abstract void prepare() throws IOException;
	protected abstract ProcessBuilder getProcessBuilder();
	protected abstract OutputHandler getOutputHandler();
	protected abstract OutputHandler getErrorHandler();
	protected abstract void collect() throws IOException;
	protected abstract String getPrepareTask();
	protected abstract String getExecTask();
	protected abstract String getCollectTask();
	
	public void start() throws InterruptedException, IOException {
		doPrepare();
		doExec();
		doCollect();
	}
	
	private void doPrepare() throws IOException {
		Timer<TimerCategory> prepareTimer = module.getTimer(processingContext, getPrepareTask(), TimerCategory.PREPARE_DATA, true);
		prepare();
		prepareTimer.stop();
	}
	
	private void doExec() throws IOException, InterruptedException {
		Timer<TimerCategory> execTimer = module.getTimer(processingContext, getExecTask(), TimerCategory.EXTERNAL, true);
		ProcessBuilder processBuilder = getProcessBuilder();
		Process p = processBuilder.start();
		startOutputHandler(getOutputHandler(), p.getInputStream());
		startOutputHandler(getErrorHandler(), p.getErrorStream());
		p.wait();
		execTimer.stop();
	}
	
	private void doCollect() throws IOException {
		Timer<TimerCategory> collectTimer = module.getTimer(processingContext, getCollectTask(), TimerCategory.COLLECT_DATA, true);
		collect();
		collectTimer.stop();
	}

	private static void startOutputHandler(OutputHandler handler, InputStream is) {
		if (handler == null) {
			return;
		}
		if (is == null) {
			return;
		}
		new Thread(new OutputHandlerRunnable(handler, is)).start();
	}

	private static class OutputHandlerRunnable implements Runnable {
		private final OutputHandler handler;
		private final InputStream is;
		
		private OutputHandlerRunnable(OutputHandler handler, InputStream is) {
			super();
			this.handler = handler;
			this.is = is;
		}

		@Override
		public void run() {
			try {
				handler.handle(is);
			}
			catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}
}
