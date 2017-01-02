package fr.jouy.inra.maiage.bibliome.alvis.web.executor;

import javax.servlet.ServletContext;

import fr.jouy.inra.maiage.bibliome.alvis.web.runs.PlanBuilder;
import fr.jouy.inra.maiage.bibliome.alvis.web.runs.Run;

public interface AlvisNLPExecutor {
	public abstract void execute(ServletContext servletContext, PlanBuilder planBuilder, Run run, boolean async);
	public abstract void cancel(Run run);
}
