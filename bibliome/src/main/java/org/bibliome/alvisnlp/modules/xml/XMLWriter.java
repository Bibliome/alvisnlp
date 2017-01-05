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



package org.bibliome.alvisnlp.modules.xml;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

import org.bibliome.alvisnlp.modules.CorpusModule;
import org.bibliome.alvisnlp.modules.ResolvedObjects;
import org.bibliome.util.streams.TargetStream;

import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.module.ModuleException;
import alvisnlp.module.ProcessingContext;
import alvisnlp.module.lib.AlvisNLPModule;
import alvisnlp.module.lib.Param;

/**
 * Writes an XML serialization of a corpus.
 */
@AlvisNLPModule(obsoleteUseInstead=XMLWriter2.class)
public class XMLWriter extends CorpusModule<ResolvedObjects> {
    private TargetStream outFile;

    @Override
	protected ResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new ResolvedObjects(ctx, this);
	}

    @Override
    public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
        try {
            getLogger(ctx).fine("writing corpus in " + outFile.getName());
            PrintStream out = outFile.getPrintStream();
            corpus.toXML(out);
            out.close();
        }
        catch (FileNotFoundException fnfe) {
            rethrow(fnfe);
        }
        catch (UnsupportedEncodingException uee) {
            rethrow(uee);
        }
        catch (IOException ioe) {
            rethrow(ioe);
        }
    }

    @Param
    public TargetStream getOutFile() {
        return outFile;
    }

    public void setOutFile(TargetStream outFile) {
        this.outFile = outFile;
    }
}
