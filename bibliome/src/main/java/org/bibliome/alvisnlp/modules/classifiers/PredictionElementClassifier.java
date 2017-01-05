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


package org.bibliome.alvisnlp.modules.classifiers;

import java.io.File;

import weka.core.Attribute;
import weka.core.Instances;

public abstract class PredictionElementClassifier extends ElementClassifier {
	private String predictedClassFeatureKey;
	private File classifierFile;

	protected static String[] getClasses(Instances instances) {
		Attribute classAttribute = instances.classAttribute();
		String[] result = new String[classAttribute.numValues()];
		for (int i = 0; i < result.length; ++i)
			result[i] = classAttribute.value(i);
		return result;
	}

	public String getPredictedClassFeatureKey() {
		return predictedClassFeatureKey;
	}
	
	public File getClassifierFile() {
		return classifierFile;
	}
	
	public void setPredictedClassFeatureKey(String predictedClassFeatureKey) {
		this.predictedClassFeatureKey = predictedClassFeatureKey;
	}
	
	public void setClassifierFile(File classifierFile) {
		this.classifierFile = classifierFile;
	}
}
