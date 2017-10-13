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


package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.converters.expression.parser;

import java.util.Arrays;
import java.util.List;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.library.StringLibrary;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.library.standard.ConditionalLibrary;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.library.standard.ConvertLibrary;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.library.standard.NavigationLibrary;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.library.standard.PropertiesLibrary;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.library.standard.SetLayerLibrary;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.library.standard.SetLibrary;

import fr.inra.maiage.bibliome.util.Pair;

public class LegacyFunctors {
	public static Pair<String,List<String>> getFunctors(String lib, List<String> ftors, int arity) {
		int n = ftors.size();
//		System.err.println("lib = " + lib);
//		System.err.println("ftors = " + ftors);
//		System.err.println("n = " + n);
//		System.err.println("arity = " + arity);
		switch (lib) {
			case "add":
			case "remove":
				if (n == 1 && arity == 0)
					return new Pair<String,List<String>>(SetLayerLibrary.NAME, Arrays.asList(lib, ftors.get(0)));
				break;
			case "start":
			case "end":
			case "length":
			case "contents":
				if (n == 0 && arity == 0)
					return new Pair<String,List<String>>(PropertiesLibrary.NAME, Arrays.asList(lib));
				break;
			case "@":
				if (n == 1 && arity == 0)
					return new Pair<String,List<String>>(PropertiesLibrary.NAME, Arrays.asList(lib, ftors.get(0)));
				break;
			case "after":
			case "before":
			case "inside":
			case "outside":
			case "overlapping":
			case "span":
			case "layer":
			case "relations":
			case "documents":
			case "args":
			case "sections":
				if (arity == 0) {
					switch (n) {
						case 0:
							return new Pair<String,List<String>>(NavigationLibrary.NAME, Arrays.asList(lib));
						case 1:
							return new Pair<String,List<String>>(NavigationLibrary.NAME, Arrays.asList(lib, ftors.get(0)));
					}
				}
				break;
			case "^":
				if (n == 0)
					return new Pair<String,List<String>>(StringLibrary.NAME, Arrays.asList("concat"));
				break;
			case ConditionalLibrary.NAME:
				if (n == 0 && arity == 3)
					return new Pair<String,List<String>>(lib, Arrays.asList(lib));
				break;
			case "bool":
			case "int":
			case "double":
			case "elements":
			case "string":
				if (n == 0 && arity == 1)
					return new Pair<String,List<String>>(ConvertLibrary.NAME, Arrays.asList(lib));
				break;
			case "tuples":
				if (arity == 0) {
					switch (n) {
						case 0:
							return new Pair<String,List<String>>(NavigationLibrary.NAME, Arrays.asList(lib));
						case 1:
							return new Pair<String,List<String>>(NavigationLibrary.NAME, Arrays.asList(lib, ftors.get(0)));
						case 2:
							return new Pair<String,List<String>>(NavigationLibrary.NAME, Arrays.asList(lib, ftors.get(0), ftors.get(1)));
					}
				}
				break;
			case "arg":
			case "feat":
				if (n == 1 && arity == 1)
					return new Pair<String,List<String>>(SetLibrary.NAME, Arrays.asList(lib, ftors.get(0)));
				break;
			case "relation":
			case "corpus":
			case "document":
			case "section":
				if (n == 0 && arity == 0)
					return new Pair<String,List<String>>(NavigationLibrary.NAME, Arrays.asList(lib));
				break;
		}
		return new Pair<String,List<String>>(lib, ftors);
	}
}
