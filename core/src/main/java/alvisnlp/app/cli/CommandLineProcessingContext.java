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



package alvisnlp.app.cli;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.bibliome.util.Strings;
import org.bibliome.util.Timer;
import org.bibliome.util.defaultmap.DefaultMap;

import alvisnlp.module.AbstractParamVisitor;
import alvisnlp.module.Annotable;
import alvisnlp.module.GlobalNameUsage;
import alvisnlp.module.Module;
import alvisnlp.module.ModuleException;
import alvisnlp.module.NameUser;
import alvisnlp.module.ParamHandler;
import alvisnlp.module.ProcessingContext;
import alvisnlp.module.ProcessingException;
import alvisnlp.module.TimerCategory;
import alvisnlp.module.lib.External;
import alvisnlp.module.lib.ExternalFailureException;
import alvisnlp.module.lib.ModuleBase;

/**
 * Base class for processing contexts of alvisnlp CLI.
 */
public abstract class CommandLineProcessingContext<T extends Annotable> implements ProcessingContext<T> {
    private Locale                                    locale;
    private File                                      rootTempDir;
    private boolean                                   resumeMode;
    private boolean                                   dumps = true;
    private final Timer<TimerCategory> timer;
    private final DefaultMap<String,Logger> loggers = new LoggerMap();
    private Thread cleanTmpDirHook = null;
    
    /**
     * Creates a new new processing context object. This object will have the following default behaviour:
     * <ul>
     * <li>the locale is set to the system default locale;</li>
     * <li>the root temporary directory is set to /tmp</li>
     * <li>there is no main module</li>
     * <li>resume mode is off</li>
     * </ul>
     * @param timer
     */
    protected CommandLineProcessingContext(Timer<TimerCategory> timer) {
        locale = Locale.getDefault();
        rootTempDir = new File("/tmp");
        resumeMode = false;
        this.timer = timer;
    }

    @Override
    public Locale getLocale() {
        return locale;
    }

    @Override
    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    @Override
    public File getRootTempDir() {
        return rootTempDir;
    }

    @Override
    public void setRootTempDir(File rootTempDir) {
        this.rootTempDir = rootTempDir;
    }
    
    @Override
    public File getTempDir(Module<T> module) {
    	List<String> path = new LinkedList<String>();
    	for (Module<T> m = module; m != null; m = m.getSequence()) {
			String id = m.getId();
			path.add(0, id);
		}
    	File result = rootTempDir;
    	for (String id : path) {
    		result = new File(result, id);
    	}
    	return result;
    }

	@Override
    public void processCorpus(Module<T> module, T corpus) throws ModuleException {
        if (module == null) {
			return;
		}
        Logger moduleLogger = module.getLogger(this);
        getTempDir(module).mkdirs();
        moduleLogger.log(ModuleBase.HIGHLIGHT, "processing");
        String modulePath = module.getPath();
        Timer<TimerCategory> timer = module.getTimer(this);
        if (module.testProcess(this, corpus)) {
        	timer.start();
        	module.process(this, corpus);
        	timer.stop();
            moduleLogger.info("done in " + (timer.getTime() / 1000000) + " ms");
        }
        corpus.hasBeenProcessedBy(modulePath);
        module.clean();
        File dumpFile = module.getDumpFile();
        if (dumpFile == null) {
			return;
		}
        if (!isDumps()) {
            moduleLogger.info("dump is inhibited");
            return;
        }
        String dumpName = dumpFile.getAbsolutePath();
        moduleLogger.info("dumping corpus into: " + dumpName);
    	Timer<TimerCategory> dumpTimer = timer.newChild("dump", TimerCategory.DUMP);
    	timer.start();
    	dumpTimer.start();
        try (Annotable.Dumper<T> dumper = getDumper(dumpFile)) {
        	dumper.dump(corpus);
        }
        catch (Exception e) {
        	throw new ModuleException(e);
//            moduleLogger.warning("cannot write to " + dumpName + ", skip dumping: " + ioe.getClass() + "/" + ioe.getMessage());
        }
        finally {
            dumpTimer.stop();
            timer.stop();
        }
    }

    @Override
    public boolean isResumeMode() {
        return resumeMode;
    }

    @Override
    public void setResumeMode(boolean mode) {
        resumeMode = mode;
    }

    private static void saveCommandLine(File file, String[] clArgs, String[] envp, File wd) throws ModuleException {
    	if (file == null)
    		return;
    	try (PrintStream ps = new PrintStream(file)) {
    		if (wd != null)
    			ps.println("cd " + wd);
    		if (envp != null)
    			for (String e : envp)
    				ps.println(e);
    		Strings.join(ps, clArgs, ' ');
    	}
    	catch (IOException e) {
    		throw new ModuleException("error while recording command-line", e);
		}
    }
    
    @Override
	public void callExternal(External<T> ext) throws ModuleException {
    	callExternal(ext, Charset.defaultCharset().name());
	}

	@Override
	public void callExternal(External<T> ext, File saveCL) throws ModuleException {
		callExternal(ext, Charset.defaultCharset().name(), saveCL);
	}

	@Override
    public void callExternal(External<T> ext, String outCharset) throws ModuleException {
    	callExternal(ext, outCharset, null);
    }
    
    @Override
    public void callExternal(External<T> ext, String outCharset, File saveCL) throws ModuleException {
        try {
            String[] clArgs = ext.getCommandLineArgs();
            String[] envp = ext.getEnvironment();
            File wd = ext.getWorkingDirectory();
            saveCommandLine(saveCL, clArgs, envp, wd);
            Process p = Runtime.getRuntime().exec(clArgs, envp, wd);
            BufferedReader out = new BufferedReader(new InputStreamReader(p.getInputStream(), outCharset));
            BufferedReader err = new BufferedReader(new InputStreamReader(p.getErrorStream(), outCharset));
            ext.processOutput(out, err);
            int retval = p.waitFor();
            if (retval != 0) {
            	throw new ExternalFailureException(ext.getOwner(), ext.getCommandLineArgs()[0], retval);
            }
            out.close();
            err.close();
        }
        catch (IOException ioe) {
        	throw new ProcessingException("system call failure", ioe);
        }
        catch (InterruptedException ie) {
        	throw new ProcessingException(ie);
        }
    }

    @Override
    public void setDumps(boolean dumps) {
        this.dumps = dumps;
    }

    @Override
    public boolean isDumps() {
        return dumps;
    }

	@Override
	public Timer<TimerCategory> getTimer() {
		return timer;
	}
	
	@Override
	public Logger getLogger(String name) {
		return loggers.safeGet(name);
	}
	
	private static final class LoggerMap extends DefaultMap<String,Logger> {
		public LoggerMap() {
			super(true, new HashMap<String,Logger>());
		}

		@Override
		protected Logger defaultValue(String key) {
			return Logger.getLogger(key);
		}
	}
	
	@Override
	public boolean isCleanTmpDir() {
		return cleanTmpDirHook != null;
	}

	@Override
	public boolean checkPlan(Logger logger, Module<T> mainModule) throws ModuleException {
		return checkNameUsage(logger, mainModule);
	}

	private boolean checkNameUsage(Logger logger, Module<T> mainModule) throws ModuleException {
		Collection<String> nameTypes = getNameTypes();
		GlobalNameUsage globalNameUsage = new GlobalNameUsage(nameTypes);
		NameUsageVisitor<T> visitor = new NameUsageVisitor<T>();
		mainModule.accept(visitor, globalNameUsage);
		for (String nameType : nameTypes) {
			Map<String,Set<String>> names = globalNameUsage.getUsedNames(nameType);
			Collection<String> nameKeys = names.keySet();
			Collection<String> ignore = getIgnoreNameTypes(nameType);
			nameKeys.removeAll(ignore);
			checkNameUsage(logger, nameType, names);
		}
		return true;
	}
	
	private static void checkNameUsage(Logger logger, String nameType, Map<String,Set<String>> names) {
		if (names.isEmpty()) {
			logger.info("no usage for " + nameType + " names");
			return;
		}
		logger.info("usage for " + nameType + " names");
		for (Map.Entry<String,Set<String>> e : names.entrySet()) {
			String name = e.getKey();
			Set<String> modules = e.getValue();
			logger.finer(nameType + " " + name + ": " + Strings.join(modules, ", "));
			if (modules.size() == 1) {
				String modulePath = modules.iterator().next();
				logger.warning(nameType + " " + name + " used only once by " + modulePath);
			}
		}
	}
	
	private static class NameUsageVisitor<T extends Annotable> extends AbstractParamVisitor<T,GlobalNameUsage> {
		@Override
		public void visitModule(Module<T> module, GlobalNameUsage param) throws ModuleException {
			super.visitModule(module, param);
			Class<?> moduleClass = module.getClass();
			if (NameUser.class.isAssignableFrom(moduleClass)) {
				NameUser nameUserModule = (NameUser) module;
				param.registerUsedNames(module.getPath(), nameUserModule, null);
			}
		}

		@Override
		public void visitParam(ParamHandler<T> paramHandler, GlobalNameUsage param) throws ModuleException {
			Object value = paramHandler.getValue();
			if (value == null) {
				return;
			}
			Module<T> module = paramHandler.getModule();
			String path = module.getPath();
			Class<?> paramType = paramHandler.getType();
			String nameType = paramHandler.getNameType();
			if (NameUser.class.isAssignableFrom(paramType)) {
				NameUser nameUser = (NameUser) value;
				param.registerUsedNames(path, nameUser, nameType);
				return;
			}
			if (String.class.isAssignableFrom(paramType)) {
				if (nameType != null) {
					param.registerUsedName(path, nameType, (String) value);
				}
				return;
			}
			if (paramType.isArray()) {
				Class<?> componentType = paramType.getComponentType();
				if (NameUser.class.isAssignableFrom(componentType)) {
					for (Object v : (Object[]) value) {
						NameUser nameUser = (NameUser) v;
						param.registerUsedNames(path, nameUser, nameType);
					}
					return;
				}
				if (String.class.isAssignableFrom(componentType)) {
					if (nameType != null) {
						for (Object v : (Object[]) value) {
							param.registerUsedName(path, nameType, (String) v);
						}
					}
					return;
				}
			}
		}
	}
	
	protected abstract Collection<String> getNameTypes();
	
	protected abstract Collection<String> getIgnoreNameTypes(String nameType);
}
