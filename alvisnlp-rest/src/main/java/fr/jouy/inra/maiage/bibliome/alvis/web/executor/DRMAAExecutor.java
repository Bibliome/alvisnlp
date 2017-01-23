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

package fr.jouy.inra.maiage.bibliome.alvis.web.executor;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.servlet.ServletContext;

import org.ggf.drmaa.DrmaaException;
import org.ggf.drmaa.JobInfo;
import org.ggf.drmaa.JobTemplate;
import org.ggf.drmaa.Session;
import org.ggf.drmaa.SessionFactory;
import org.xml.sax.SAXException;

import fr.jouy.inra.maiage.bibliome.alvis.web.AlvisNLPContextParameter;
import fr.jouy.inra.maiage.bibliome.alvis.web.runs.PlanBuilder;
import fr.jouy.inra.maiage.bibliome.alvis.web.runs.Run;
import fr.jouy.inra.maiage.bibliome.alvis.web.runs.RunStatus;

public class DRMAAExecutor implements AlvisNLPExecutor {
	private static final String DRMAA_JOB_ID_PROPERTY = "drmaa-job-id";

	@Override
	public void execute(ServletContext servletContext, PlanBuilder planBuilder, Run run, boolean async) {
		SessionFactory sessionFactory = SessionFactory.getFactory();
		Session session = sessionFactory.getSession();
		try {
			session.init("");
			JobTemplate jobTemplate = createJobTemplate(servletContext, session, run);
			String jobId = enqueue(session, run, jobTemplate);
			if (async) {
				String status = waitForStatus(session, jobId);
				if (status != null) {
					run.addStatus(status, "", true);
				}
			}
			session.deleteJobTemplate(jobTemplate);
			session.exit();
		}
		catch (DrmaaException e) {
			throw new RuntimeException(e);
		}
	}

	private static JobTemplate createJobTemplate(ServletContext servletContext, Session session, Run run) throws DrmaaException {
		JobTemplate result = session.createJobTemplate();
		result.setRemoteCommand("java");
		result.setArgs(Arrays.asList(
				"-cp",
				DRMAAContextParameter.JARS_PATH.getStringValue(servletContext),
				DRMAAExecutor.class.getCanonicalName(),
				AlvisNLPContextParameter.ROOT_PROCESSING_DIR.getStringValue(servletContext),
				AlvisNLPContextParameter.RESOURCE_DIR.getStringValue(servletContext),
				AlvisNLPContextParameter.PLAN_DIR.getStringValue(servletContext),
				run.getId()
				));
		String nativeSpecification = DRMAAContextParameter.NATIVE_SPECIFICATION.getStringValue(servletContext);
		if (nativeSpecification != null && !nativeSpecification.isEmpty()) {
			result.setNativeSpecification(nativeSpecification);
		}
		return result;
	}
	
	private static String enqueue(Session session, Run run, JobTemplate jobTemplate) throws DrmaaException {
		String result = session.runJob(jobTemplate);
		run.addStatus(RunStatus.QUEUED, result, false);
		run.setProperty(DRMAA_JOB_ID_PROPERTY, result);
		run.write();
		return result;
	}
	
	private static String waitForStatus(Session session, String jobId) throws DrmaaException {
		JobInfo jobInfo = session.wait(jobId, Session.TIMEOUT_WAIT_FOREVER);
		if (jobInfo.wasAborted()) {
			return RunStatus.ABORTED;
		}
		if (jobInfo.hasExited()) {
			return null;
		}
		if (jobInfo.hasSignaled()) {
			return RunStatus.SIGNALLED;
		}
		return RunStatus.DISAPPEARED;
	}

	@Override
	public void cancel(Run run) {
		String jobId = run.getProperty(DRMAA_JOB_ID_PROPERTY);
		if (jobId == null) {
			return;
		}
		SessionFactory sessionFactory = SessionFactory.getFactory();
		Session session = sessionFactory.getSession();
		try {
			session.init("");
			session.control(jobId, Session.TERMINATE);
			session.exit();
		}
		catch (DrmaaException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, SAXException, IOException {
		File rootProcessingDir = new File(args[0]);
		File resourceDir = new File(args[1]);
		File planDir = new File(args[2]);
		String runId = args[3];
		Run run = Run.read(rootProcessingDir, runId);
		PlanBuilder planBuilder = new PlanBuilder(planDir, resourceDir);
		run.process(planBuilder);
	}
}
