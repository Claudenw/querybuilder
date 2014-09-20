/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jena.arq.querybuilder.handlers;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.core.VarExprList;

public class SelectHandler implements Handler {

	private final Query query;

	public SelectHandler(Query query) {
		this.query = query;
		setDistinct(query.isDistinct());
		setReduced(query.isReduced());
	}

	public void setDistinct(boolean state) {
		query.setDistinct(state);
		if (state) {
			query.setReduced(false);
		}
	}

	public void setReduced(boolean state) {
		query.setReduced(state);
		if (state) {
			query.setDistinct(false);
		}
	}

	public void addVar(Var var) {
		if (var == null)
		{
			query.setQueryResultStar(true);
		} else {
			query.setQueryResultStar(false);
			query.addResultVar(var);
		}
	}

	public List<Var> getVars() {
		return query.getProjectVars();
	}

	public void addAll(SelectHandler selectHandler) {

		setReduced(selectHandler.query.isReduced());
		setDistinct(selectHandler.query.isDistinct());
		query.setQueryResultStar(selectHandler.query.isQueryResultStar());

		try {
			Field f = Query.class.getDeclaredField("projectVars");
			f.setAccessible(true);
			VarExprList projectVars = (VarExprList) f.get(selectHandler.query);
			f.set(query, new VarExprList(projectVars));
		} catch (NoSuchFieldException e) {
			throw new IllegalStateException(e);
		} catch (SecurityException e) {
			throw new IllegalStateException(e);
		} catch (IllegalAccessException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public void setVars(Map<Var, Node> values) {
		// nothing to do
	}

	@Override
	public void build() {
		if (query.getProject().getVars().isEmpty())
		{
			query.setQueryResultStar(true);
		}
		// handle the SELECT * case
		query.getProjectVars();
	}
}
