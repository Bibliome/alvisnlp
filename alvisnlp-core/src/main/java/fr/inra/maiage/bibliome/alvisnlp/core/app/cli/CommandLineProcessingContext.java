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



package fr.inra.maiage.bibliome.alvisnlp.core.app.cli;

import java.io.File;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.dump.Dumper;
import fr.inra.maiage.bibliome.alvisnlp.core.module.AbstractParamVisitor;
import fr.inra.maiage.bibliome.alvisnlp.core.module.GlobalNameUsage;
import fr.inra.maiage.bibliome.alvisnlp.core.module.Module;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.NameUser;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ParamHandler;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.TimerCategory;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.ModuleBase;
import fr.inra.maiage.bibliome.util.Files;
import fr.inra.maiage.bibliome.util.Strings;
import fr.inra.maiage.bibliome.util.Timer;
import fr.inra.maiage.bibliome.util.defaultmap.DefaultMap;

/**
 * Base class for processing contexts of alvisnlp CLI.
 */
public abstract class CommandLineProcessingContext implements ProcessingContext {
    private Locale                                    locale;
    private File                                      rootTempDir;
    private boolean                                   resumeMode;
    private boolean                                   dumps = true;
    private final Timer<TimerCategory> timer;
    private final DefaultMap<String,Logger> loggers = new LoggerMap();
    private boolean cleanTmpDir = false;
    
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
    public File getTempDir(Module module) {
    	List<String> path = new LinkedList<String>();
    	for (Module m = module; m != null; m = m.getSequence()) {
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
    public void processCorpus(Module module, Corpus corpus) throws ModuleException {
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
        if (cleanTmpDir) {
    		File tempDir = getTempDir(module);
			moduleLogger.info("deleting temp dir: " + tempDir);
			Files.recDelete(tempDir);
        }
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
        try (Dumper dumper = getDumper(moduleLogger, dumpFile)) {
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
			super(true, new LinkedHashMap<String,Logger>());
		}

		@Override
		protected Logger defaultValue(String key) {
			return Logger.getLogger(key);
		}
	}
	
	@Override
	public boolean isCleanTmpDir() {
		return cleanTmpDir;
	}

	@Override
	public boolean checkPlan(Logger logger, Module mainModule) throws ModuleException {
		return checkNameUsage(logger, mainModule);
	}

	private boolean checkNameUsage(Logger logger, Module mainModule) throws ModuleException {
		Collection<String> nameTypes = getNameTypes();
		GlobalNameUsage globalNameUsage = new GlobalNameUsage(nameTypes);
		NameUsageVisitor visitor = new NameUsageVisitor();
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
	
	private static class NameUsageVisitor extends AbstractParamVisitor<GlobalNameUsage> {
		private NameUsageVisitor() {
			super(true);
		}

		@Override
		public void visitModule(Module module, GlobalNameUsage param) throws ModuleException {
			super.visitModule(module, param);
			Class<?> moduleClass = module.getClass();
			if (NameUser.class.isAssignableFrom(moduleClass)) {
				NameUser nameUserModule = (NameUser) module;
				param.registerUsedNames(module.getPath(), nameUserModule, null);
			}
		}

		@Override
		public void visitParam(ParamHandler paramHandler, GlobalNameUsage param) throws ModuleException {
			Object value = paramHandler.getValue();
			if (value == null) {
				return;
			}
			Module module = paramHandler.getModule();
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

	@Override
	public void setCleanTmpDir(boolean cleanTmpDir) {
		this.cleanTmpDir = cleanTmpDir;
	}
}
