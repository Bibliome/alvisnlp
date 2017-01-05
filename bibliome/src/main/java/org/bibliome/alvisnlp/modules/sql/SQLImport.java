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


package org.bibliome.alvisnlp.modules.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import org.bibliome.alvisnlp.modules.CorpusModule;
import org.bibliome.alvisnlp.modules.ResolvedObjects;
import org.bibliome.alvisnlp.modules.sql.SQLImport.SQLImportResolvedObjects;
import org.bibliome.util.Iterators;

import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.Element;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.Evaluator;
import alvisnlp.corpus.expressions.Expression;
import alvisnlp.corpus.expressions.LibraryResolver;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.module.ActionInterface;
import alvisnlp.module.ModuleException;
import alvisnlp.module.NameUsage;
import alvisnlp.module.ProcessingContext;
import alvisnlp.module.TimerCategory;
import alvisnlp.module.lib.AlvisNLPModule;
import alvisnlp.module.lib.Param;
import alvisnlp.module.lib.TimeThis;

@AlvisNLPModule(beta=true)
public abstract class SQLImport extends CorpusModule<SQLImportResolvedObjects> implements ActionInterface {
	private String url;
	private String username;
	private String password;
	private String query;
	private Expression target;
	private SQLParameter[] parameters;
	private Expression action;

	static class SQLImportResolvedObjects extends ResolvedObjects {
		private final ResultSetLibrary resultSetLibrary;
		private final Evaluator target;
		private final SQLParameterEvaluator[] parameters;
		private final Evaluator action;
		
		private SQLImportResolvedObjects(ProcessingContext<Corpus> ctx, SQLImport module) throws ResolverException {
			super(ctx, module);
			resultSetLibrary = new ResultSetLibrary();
			LibraryResolver resultSetResolver = resultSetLibrary.newLibraryResolver(rootResolver);
			target = module.target.resolveExpressions(rootResolver);
			parameters = rootResolver.resolveArray(module.parameters, SQLParameterEvaluator.class);
			action = module.action.resolveExpressions(resultSetResolver);
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
			super.collectUsedNames(nameUsage, defaultType);
			target.collectUsedNames(nameUsage, defaultType);
			nameUsage.collectUsedNamesArray(parameters, defaultType);
			action.collectUsedNames(nameUsage, defaultType);
		}
	}
	
	@Override
	protected SQLImportResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new SQLImportResolvedObjects(ctx, this);
	}

	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		SQLImportResolvedObjects resObj = getResolvedObjects();
		Logger logger = getLogger(ctx);
		EvaluationContext evalCtx = new EvaluationContext(logger);
		EvaluationContext actionCtx = new EvaluationContext(logger, this);

		try (Connection connection = openConnection(ctx)) {
			PreparedStatement statement = connection.prepareStatement(query);
			for (Element elt : Iterators.loop(resObj.target.evaluateElements(evalCtx, corpus))) {
				for (int i = 0; i < resObj.parameters.length; ++i) {
					resObj.parameters[i].updateStatement(statement, i, evalCtx, elt);
				}
				fetchResult(ctx, statement, actionCtx, elt);
			}
			commit(ctx, actionCtx);
		}
		catch (ClassNotFoundException|SQLException e) {
			rethrow(e);
		}
	}

	@TimeThis(task="fetch-results", category=TimerCategory.COLLECT_DATA)
	protected void fetchResult(@SuppressWarnings("unused") ProcessingContext<Corpus> ctx, PreparedStatement statement, EvaluationContext actionCtx, Element elt) throws SQLException {
		SQLImportResolvedObjects resObj = getResolvedObjects();
		ResultSet resultSet = statement.executeQuery();
		resObj.resultSetLibrary.setResultSet(resultSet);
		while (resultSet.next()) {
			Iterators.deplete(resObj.action.evaluateElements(actionCtx, elt));
		}
	}

	@TimeThis(task="open-connection", category=TimerCategory.LOAD_RESOURCE)
	protected Connection openConnection(@SuppressWarnings("unused") ProcessingContext<Corpus> ctx) throws SQLException, ClassNotFoundException {
//		Class.forName("org.postgresql.Driver");
		Class.forName("org.postgresql.Driver", true, SQLImport.class.getClassLoader());
		return DriverManager.getConnection(url, username, password);
	}

	@Param
	public String getUrl() {
		return url;
	}

	@Param
	public String getUsername() {
		return username;
	}

	@Param
	public String getPassword() {
		return password;
	}

	@Param
	public String getQuery() {
		return query;
	}

	@Param
	public Expression getTarget() {
		return target;
	}

	@Param
	public SQLParameter[] getParameters() {
		return parameters;
	}

	@Param
	public Expression getAction() {
		return action;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public void setTarget(Expression target) {
		this.target = target;
	}

	public void setParameters(SQLParameter[] parameters) {
		this.parameters = parameters;
	}

	public void setAction(Expression action) {
		this.action = action;
	}
}
