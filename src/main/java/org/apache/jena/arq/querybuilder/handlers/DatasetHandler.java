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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.sparql.core.Var;

public class DatasetHandler implements Handler {

	private final Query query;

	public DatasetHandler(Query query) {
		this.query = query;
	}

	public void addAll(DatasetHandler datasetHandler) {
		from(datasetHandler.query.getGraphURIs());
		fromNamed(datasetHandler.query.getNamedGraphURIs());
	}

	public void fromNamed(String graphName) {
		query.addNamedGraphURI(graphName);
	}

	public void fromNamed(Collection<String> graphNames) {
		for (String uri : graphNames) {
			query.addNamedGraphURI(uri);
		}
	}

	public void from(String graphName) {
		query.addGraphURI(graphName);
	}

	public void from(Collection<String> graphNames) {
		for (String uri : graphNames) {
			query.addGraphURI(uri);
		}
	}

	private void setVars(Map<Var, Node> values, String fieldName) {
		if (values.isEmpty()) {
			return;
		}
		try {
			Field f = Query.class.getDeclaredField(fieldName);
			f.setAccessible(true);
			List<String> orig = (List<String>) f.get(query);
			List<String> lst = null;
			if (orig != null) {
				lst = new ArrayList<String>();
				for (String s : orig) {
					Node n = null;
					if (s.startsWith("?")) {
						Var v = Var.alloc(s.substring(1));
						n = values.get(v);
					}
					lst.add(n == null ? s : n.toString());
				}
				f.set(query, lst);
			}
		} catch (NoSuchFieldException e) {
			throw new IllegalStateException(e.getMessage(), e);
		} catch (SecurityException e) {
			throw new IllegalStateException(e.getMessage(), e);
		} catch (IllegalAccessException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	@Override
	public void setVars(Map<Var, Node> values) {
		setVars(values, "namedGraphURIs");
		setVars(values, "graphURIs");
	}

	@Override
	public void build() {
		// TODO Auto-generated method stub

	}

}
