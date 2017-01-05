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


package alvisnlp.annotation;

import java.io.IOException;
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

import alvisnlp.corpus.expressions.Library;

/**
 * Annotation processor for AlvisNLP modules and converters.
 * @author rbossy
 *
 */
@SupportedAnnotationTypes( {
	"alvisnlp.module.lib.AlvisNLPModule",
	"alvisnlp.module.lib.Param",
	"alvisnlp.module.lib.TimeThis",
	"alvisnlp.converters.lib.Converter",
	"alvisnlp.corpus.expressions.Library",
	"alvisnlp.corpus.expressions.Function",
	"javax.annotation.Generated"
})
@SupportedOptions( {
	"moduleFactoryName",
	"dataClass",
	"sequenceImplementationClass",
	"factoryInterface",
	"converterFactoryName",
	"classPrefix",
	"shellModule"
})
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class AlvisNLPAnnotationProcessor extends AbstractProcessor {
	private ModelContext ctx;

	@Override
	public void init(ProcessingEnvironment processingEnv) {
		super.init(processingEnv);
		ctx = new ModelContext(processingEnv, getClass().getCanonicalName());
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		try {
			String prefix = ctx.hasOption("classPrefix") ? ctx.getOption("classPrefix") : "Concrete";

			if (annotations.contains(ctx.getModuleAnnotation())) {				
				String moduleFactoryName = ctx.getOption("moduleFactoryName");
				String dataClass = ctx.getOption("dataClass");
				String sequenceImplementationClass = ctx.getOption("sequenceImplementationClass");
				String factoryInterface = ctx.getOption("factoryInterface");
				String shellModule = ctx.getOption("shellModule");
				ModuleFactoryModel moduleFactory = new ModuleFactoryModel(moduleFactoryName, dataClass, sequenceImplementationClass, factoryInterface, prefix, shellModule);

				ctx.note("processing module sources");
				for (TypeElement moduleElement : ElementFilter.typesIn(roundEnv.getElementsAnnotatedWith(ctx.getModuleAnnotation()))) {
					ModuleModel module = new ModuleModel(ctx, moduleElement);
					moduleFactory.addModule(ctx, module);
				}

				ctx.note("generating module factory for data class " + dataClass + " (" + moduleFactory.moduleCount() + " modules, " + moduleFactory.betaCount() + " beta, " + moduleFactory.obsoleteCount() + " obsolete)");
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
				ConverterFactoryModel converterFactory = new ConverterFactoryModel(ctx.getOption("converterFactoryName"));
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
