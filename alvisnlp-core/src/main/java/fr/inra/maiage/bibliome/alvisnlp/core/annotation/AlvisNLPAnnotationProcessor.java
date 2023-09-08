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


package fr.inra.maiage.bibliome.alvisnlp.core.annotation;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Library;

/**
 * Annotation processor for AlvisNLP modules and converters.
 * @author rbossy
 *
 */
@SupportedAnnotationTypes( {
	"fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AlvisNLPModule",
	"fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param",
	"fr.inra.maiage.bibliome.alvisnlp.core.module.lib.TimeThis",
	"fr.inra.maiage.bibliome.alvisnlp.core.converters.lib.Converter",
	"fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Library",
	"fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Function",
	"javax.annotation.Generated"
})
@SupportedOptions( {
	"moduleFactoryName",
	"sequenceImplementationClass",
	"factoryInterface",
	"converterFactoryName",
	"classPrefix",
	"shellModule",
	"browserModule"
})
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class AlvisNLPAnnotationProcessor extends AbstractProcessor {
	private ModelContext ctx;

	@Override
	public void init(ProcessingEnvironment processingEnv) {
		super.init(processingEnv);
		try {
			ctx = new ModelContext(processingEnv, getClass().getCanonicalName());
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		try {
			String prefix = ctx.hasOption("classPrefix") ? ctx.getOption("classPrefix") : "Concrete";

			if (annotations.contains(ctx.getModuleAnnotation())) {				
				String moduleFactoryName = ctx.getOption("moduleFactoryName");
				String sequenceImplementationClass = ctx.getOption("sequenceImplementationClass");
				String factoryInterface = ctx.getOption("factoryInterface");
				String shellModule = ctx.getOption("shellModule");
				String browserModule = ctx.getOption("browserModule");
				ModuleFactoryModel moduleFactory = new ModuleFactoryModel(moduleFactoryName, sequenceImplementationClass, factoryInterface, prefix, shellModule, browserModule);

				ctx.note("processing module sources");
				for (TypeElement moduleElement : ElementFilter.typesIn(roundEnv.getElementsAnnotatedWith(ctx.getModuleAnnotation()))) {
					ModuleModel module = new ModuleModel(ctx, moduleElement);
					moduleFactory.addModule(ctx, module);
				}

				ctx.note("generating module factory (" + moduleFactory.moduleCount() + " modules, " + moduleFactory.betaCount() + " beta, " + moduleFactory.obsoleteCount() + " obsolete)");
				moduleFactory.generateClass(ctx);
				ctx.note("generating module classes");
				moduleFactory.generateModules(ctx);
				// XXX broken in maven build
				//ctx.note("generating module documentation");
				//moduleFactory.generateModuleDocs(ctx);
				//ctx.note("generating module factory service");
				//moduleFactory.generateService(ctx);
			}

			if (annotations.contains(ctx.getConverterAnnotation())) {
				if (!ctx.hasOption("converterFactoryName")) {
					ctx.error("missing option -AconverterFactoryName");
					return true;
				}
				String converterFactoryName = ctx.getOption("converterFactoryName");
				List<String> resourceBases = Arrays.asList(ctx.getOption("resourceBases").split(":"));
				ConverterFactoryModel converterFactory = new ConverterFactoryModel(converterFactoryName, resourceBases);
				ctx.note("processing converter sources");
				for (TypeElement converterElement : ElementFilter.typesIn(roundEnv.getElementsAnnotatedWith(ctx.getConverterAnnotation()))) {
					ConverterModel converter = new ConverterModel(ctx, converterElement);
					converterFactory.addConverter(converter);
				}
				ctx.note("generating converter factory class");
				converterFactory.generateClass(ctx);
				ctx.note("generating converter documentation");
				converterFactory.generateConvertersDoc(ctx);
				//ctx.note("generating converter factory service");
				//converterFactory.generateService(ctx);
			}

			if (annotations.contains(ctx.getLibraryAnnotation())) {
				for (TypeElement libraryElement : ElementFilter.typesIn(roundEnv.getElementsAnnotatedWith(ctx.getLibraryAnnotation()))) {
					ctx.note("generating library class: " + libraryElement.getAnnotation(Library.class).value());
					LibraryModel libModel = new LibraryModel(ctx, libraryElement, prefix);
					libModel.generateClass(ctx);
					// XXX broken by maven
					// libModel.generateDocumentation(ctx);
				}
			}
		}
		catch (TransformerException te) {
			ctx.error("error while processing source: " + te.getMessage());
		}
		catch (IOException ioe) {
			ctx.error("error while processing source: " + ioe.getMessage());
		}
		catch (ModelException me) {
			ctx.error(me.getMessage());
		}
//		catch (XPathExpressionException xpee) {
//			ctx.error(xpee.getMessage());
//		}
		catch (TransformerFactoryConfigurationError tfce) {
			ctx.error(tfce.getMessage());
		}
//		catch (SAXException saxe) {
//			ctx.error(saxe.getMessage());
//		}
		return true;
	}
}
