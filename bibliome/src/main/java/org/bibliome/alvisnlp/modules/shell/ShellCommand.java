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


package org.bibliome.alvisnlp.modules.shell;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeSet;
import java.util.regex.Pattern;

import org.bibliome.util.Iterators;
import org.bibliome.util.Strings;

import alvisnlp.corpus.Element;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.EvaluationType;
import alvisnlp.corpus.expressions.Evaluator;
import alvisnlp.corpus.expressions.VariableLibrary;
import alvisnlp.corpus.expressions.VariableLibrary.Variable;

public enum ShellCommand {
	QUERY("query") {
		@Override
		void execute(ShellEnvironment env, String operand) throws Exception {
			Evaluator expr = env.parseAndResolveExpression(operand);
			Collection<EvaluationType> types = expr.getTypes();
			EvaluationType primaryType = types.iterator().next();
			switch (primaryType) {
				case BOOLEAN:
					boolean b = expr.evaluateBoolean(env.getEvaluationContext(), env.getCurrentElement());
					System.out.println(b);
					break;
				case INT:
					int i = expr.evaluateInt(env.getEvaluationContext(), env.getCurrentElement());
					System.out.println(i);
					break;
				case DOUBLE:
					double d = expr.evaluateDouble(env.getEvaluationContext(), env.getCurrentElement());
					System.out.println(d);
					break;
				case STRING:
					String s = expr.evaluateString(env.getEvaluationContext(), env.getCurrentElement());
					System.out.println(Strings.escapeJava(s));
					break;
				case UNDEFINED:
					throw new RuntimeException("could not determine the expression type, force one with boolean, int, double, string or elements");
				default:
					Iterator<Element> it = expr.evaluateElements(env.getEvaluationContext(), env.getCurrentElement());
					for (Element elt : Iterators.loop(it))
						System.out.println(elt.accept(PrintElement.INSTANCE, null));
			}
			env.getEvaluationContext().commit();
		}
	},

	MOVE("move") {
		@Override
		void execute(ShellEnvironment env, String operand) throws Exception {
			Evaluator expr = env.parseAndResolveExpression(operand);
			List<Element> list = expr.evaluateList(env.getEvaluationContext(), env.getCurrentElement());
			env.push(list);
			System.out.println(Integer.toString(list.size()) + " elements");
			System.out.println(env.getCurrentElement().accept(PrintElement.INSTANCE, null));
		}
	},

	UP("up") {
		@Override
		void execute(ShellEnvironment env, String operand) throws Exception {
			env.pop();
		}
	},

	NEXT("next") {
		@Override
		void execute(ShellEnvironment env, String operand) throws Exception {
			Element elt = env.forward();
			System.out.println(elt.accept(PrintElement.INSTANCE, null));
		}
	},


	PREV("prev") {
		@Override
		void execute(ShellEnvironment env, String operand) throws Exception {
			Element elt = env.back();
			System.out.println(elt.accept(PrintElement.INSTANCE, null));
		}
	},
	
	HELP("help") {
		@Override
		void execute(ShellEnvironment env, String operand) throws Exception {
			Locale locale = env.getLocale();
//			ResourceBundle bundle = ResourceBundle.getBundle(ShellCommand.class.getCanonicalName(), locale);
			Class<ShellCommand> klass = ShellCommand.class;
			ResourceBundle bundle = ResourceBundle.getBundle(klass.getCanonicalName(), locale, klass.getClassLoader());
			for (ShellCommand shellCommand : ShellCommand.values()) {
				String cmd = shellCommand.command;
				String usage = bundle.getString(cmd + ".usage");
				String help = bundle.getString(cmd + ".help");
				System.out.print("    ");
				System.out.println(usage);
				System.out.println(help);
				System.out.println();
			}
		}
	},

	FEATURES("features") {
		@Override
		void execute(ShellEnvironment env, String operand) throws Exception {
			Element currentElement = env.getCurrentElement();
			Iterator<Element> it;
			if (operand.trim().isEmpty())
				it = Iterators.singletonIterator(currentElement);
			else {
				Evaluator expr = env.parseAndResolveExpression(operand);
				it = expr.evaluateElements(env.getEvaluationContext(), currentElement);
			}
			for (Element elt : Iterators.loop(it)) {
				System.out.println(elt.accept(PrintElement.INSTANCE, null));
				Collection<String> featureKeys = new TreeSet<String>(elt.getFeatureKeys());
				for (String name : featureKeys) {
					System.out.println(
							"    " +
									name +
									" = \"" +
									Strings.escapeJava(elt.getLastFeature(name)) +
									"\" [ " +
									Strings.join(elt.getFeature(name), ", ") +
							" ]");
				}
			}
		}
	},
	
	STACK("stack") {
		@Override
		void execute(ShellEnvironment env, String operand) throws Exception {
			Element[] stack = env.stack();
			for (int i = 0; i < stack.length; ++i) {
				for (int j = 0; j < i; ++j)
					System.out.print("  ");
				System.out.println(stack[i].accept(PrintElement.INSTANCE, null));
			}
		}
	},
	
	REF("ref") {
		@Override
		void execute(ShellEnvironment env, String operand) throws Exception {
			String[] args = SPACE.split(operand, 2);
			if (args.length != 2)
				throw new Exception("@ref requires 2 operands");
			String name = args[0];
			Evaluator expr = env.parseAndResolveExpression(args[1]);
			VariableLibrary varLib = env.getVarLib();
			Variable var;
			if (varLib.hasVariable(name))
				var = varLib.getVariable(name);
			else
				var = varLib.newVariable(name);
			EvaluationContext ctx = env.getEvaluationContext();
			Collection<EvaluationType> types = expr.getTypes();
			EvaluationType primaryType = types.iterator().next();
			switch (primaryType) {
				case BOOLEAN:
					boolean b = expr.evaluateBoolean(ctx, env.getCurrentElement());
					var.set(b);
					break;
				case INT:
					int i = expr.evaluateInt(ctx, env.getCurrentElement());
					var.set(i);
					break;
				case DOUBLE:
					double d = expr.evaluateDouble(ctx, env.getCurrentElement());
					var.set(d);
					break;
				case STRING:
					String s = expr.evaluateString(ctx, env.getCurrentElement());
					var.set(s);
					break;
				case UNDEFINED:
					throw new Error("could not determine the expression type, force one with boolean, int, double, string or elements");
				default:
					List<Element> l = expr.evaluateList(env.getEvaluationContext(), env.getCurrentElement());
					var.set(l);
			}
		}
	},
	
	ALLOW("allow") {
		@Override
		void execute(ShellEnvironment env, String operand) throws Exception {
			EvaluationContext ctx = env.getEvaluationContext();
			Shell shell = env.getOwner();
			String[] args = SPACE.split(operand);
			String action = args[0];
			switch (action) {
				case "add":
					if (args.length > 1)
						throw new Error("extra operands after add");
					ctx.setAllowAddAnnotation(true);
					break;
				case "remove":
					if (args.length > 1)
						throw new Error("extra operands after remove");
					ctx.setAllowRemoveAnnotation(true);
					break;
				case "delete":
					if (args.length > 1)
						throw new Error("extra operands after delete");
					ctx.setAllowDeleteElement(true);
					break;
				case "args":
					if (args.length > 1)
						throw new Error("extra operands after args");
					ctx.setAllowSetArgument(true);
					break;
				case "features":
					if (args.length > 1)
						throw new Error("extra operands after features");
					ctx.setAllowSetFeature(true);
					break;
				case "create":
					if (args.length < 2)
						throw new Error("missing operands after create");
					for (int i = 1; i < args.length; ++i) {
						String type = args[i];
						switch (type) {
							case "documents":
								ctx.setDocumentCreator(shell);
								break;
							case "sections":
								ctx.setSectionCreator(shell);
								break;
							case "annotations":
								ctx.setAnnotationCreator(shell);
								break;
							case "relations":
								ctx.setRelationCreator(shell);
								break;
							case "tuples":
								ctx.setTupleCreator(shell);
								break;
							case "all":
								ctx.setDocumentCreator(shell);
								ctx.setSectionCreator(shell);
								ctx.setAnnotationCreator(shell);
								ctx.setRelationCreator(shell);
								ctx.setTupleCreator(shell);
								break;
							default:
								throw new Exception("create suports only: documents, sections, annotations, relations tuples and all");
						}
					}
					break;
				case "everything":
					ctx.setAllowDeleteElement(true);
					ctx.setAllowSetArgument(true);
					ctx.setAllowSetFeature(true);
					ctx.setDocumentCreator(shell);
					ctx.setSectionCreator(shell);
					ctx.setAnnotationCreator(shell);
					ctx.setRelationCreator(shell);
					ctx.setTupleCreator(shell);
					break;
				default:
					throw new Exception("@allow supports only: delete, args, features and create");
			}
		}
	},
	
	STATE("context") {
		private void printAllow(String subject, boolean value) {
			System.out.println("allow " + subject + ": " + value);
		}
		
		@Override
		void execute(ShellEnvironment env, String operand) throws Exception {
			EvaluationContext ctx = env.getEvaluationContext();
			printAllow("delete elements", ctx.isAllowDeleteElement());
			printAllow("set features", ctx.isAllowSetFeature());
			printAllow("set tuple arguments", ctx.isAllowSetArgument());
			printAllow("create documents", ctx.getDocumentCreator() != null);
			printAllow("create sections", ctx.getSectionCreator() != null);
			printAllow("create annotations", ctx.getAnnotationCreator() != null);
			printAllow("create relations", ctx.getRelationCreator() != null);
			printAllow("create tuples", ctx.getTupleCreator() != null);
			printAllow("layer addition", ctx.isAllowAddAnnotation());
			printAllow("layer substraction", ctx.isAllowRemoveAnnotation());
		}
	}
	;

	public final String command;

	private ShellCommand(String command) {
		this.command = command;
	}

	abstract void execute(ShellEnvironment env, String operand) throws Exception;

	public static Map<String,ShellCommand> getCommands() {
		Map<String,ShellCommand> result = new HashMap<String,ShellCommand>();
		for (ShellCommand cmd : ShellCommand.values())
			result.put(cmd.command, cmd);
		return result;
	}

	private static final Pattern SPACE = Pattern.compile("\\s+");
}
