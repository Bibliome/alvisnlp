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


package org.bibliome.alvisnlp.modules.script;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.bibliome.alvisnlp.modules.CorpusModule;
import org.bibliome.alvisnlp.modules.ResolvedObjects;

import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.creators.AnnotationCreator;
import alvisnlp.corpus.creators.DocumentCreator;
import alvisnlp.corpus.creators.SectionCreator;
import alvisnlp.corpus.creators.TupleCreator;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.module.ModuleException;
import alvisnlp.module.ProcessingContext;
import alvisnlp.module.ProcessingException;
import alvisnlp.module.lib.AlvisNLPModule;
import alvisnlp.module.lib.Param;

@AlvisNLPModule(beta=true)
public abstract class Script extends CorpusModule<ResolvedObjects> implements DocumentCreator, SectionCreator, AnnotationCreator, TupleCreator {
    private String language = "JavaScript";
    private String script = null;
    
    @Override
	protected ResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new ResolvedObjects(ctx, this);
	}

	@Override
    public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
        try {
            Logger logger = getLogger(ctx);
            logger.info("configuring script engine");
            ScriptEngineManager seMgr = new ScriptEngineManager();
            ScriptEngine se = seMgr.getEngineByName(language);
            if (se == null)
                throw new ProcessingException("no script engine for " + language);
            ScriptContext sc = se.getContext();
            sc.setErrorWriter(new LogWriter(logger, Level.FINE));
            sc.getBindings(ScriptContext.ENGINE_SCOPE).put("alvisnlp", new HelperObject(ctx, this, corpus));
            logger.info("running script");
            se.eval(script);
            logger.info("script end");
            sc.getReader().close();
            sc.getWriter().flush();
            sc.getWriter().close();
            sc.getErrorWriter().close();
        }
        catch (ScriptException se) {
           rethrow(se);
        }
        catch (FileNotFoundException fnfe) {
            rethrow(fnfe);
        }
        catch (IOException ioe) {
            rethrow(ioe);
        }
    }

    @Param(defaultDoc = "The language of the script.")
    public String getLanguage() {
        return language;
    }

    @Param(defaultDoc = "The script to run (the source is inside the plan, not a path to a file).")
    public String getScript() {
        return script;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setScript(String script) {
        this.script = script;
    }
}
