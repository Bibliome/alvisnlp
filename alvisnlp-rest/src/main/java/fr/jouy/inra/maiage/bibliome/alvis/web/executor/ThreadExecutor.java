package fr.jouy.inra.maiage.bibliome.alvis.web.executor;

import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.servlet.ServletContext;

import fr.jouy.inra.maiage.bibliome.alvis.web.runs.PlanBuilder;
import fr.jouy.inra.maiage.bibliome.alvis.web.runs.Run;
import fr.jouy.inra.maiage.bibliome.alvis.web.runs.RunStatus;

public class ThreadExecutor implements AlvisNLPExecutor {
	private static final int THREAD_POOL_SIZE = 6;
	private static final ExecutorService THREAD_POOL = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
	private static final Map<String,Future<?>> RUNNING = new ConcurrentHashMap<String,Future<?>>();
	
	public ThreadExecutor() {
		super();
	}
	
	@Override
	public void execute(final ServletContext servletContext, final PlanBuilder planBuilder, final Run run, boolean async) {
		final String runId = run.getId();
		Future<?> future = THREAD_POOL.submit(new Runnable() {
			@Override
			public void run() {
				run.process(planBuilder);
				RUNNING.remove(runId);
			}
		});
		if (async) {
			try {
				future.get();
			}
			catch (InterruptedException|CancellationException e) {
				run.addStatus(RunStatus.INTERRUPTED, "", true);
				run.write();
			}
			catch (ExecutionException e) {
				// XXX if the execution threw an exception, then it should have been caught and laid on the run status
			}
		}
		else {
			RUNNING.put(runId, future);
		}
	}

	@Override
	public void cancel(Run run) {
		String runId = run.getId();
		Future<?> future = RUNNING.remove(runId);
		if (future == null) {
			return;
		}
		if (future.isDone()) {
			return;
		}
		if (future.isCancelled()) {
			return;
		}
		future.cancel(true);
	}
}
