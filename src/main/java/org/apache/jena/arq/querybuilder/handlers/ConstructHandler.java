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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.jena.arq.querybuilder.rewriters.AbstractRewriter;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.sparql.core.BasicPattern;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.syntax.Template;

public class ConstructHandler implements Handler {
	private final Query query;
	private List<Triple> constructs;

	public ConstructHandler(Query query) {
		this.query = query;
		constructs = new ArrayList<Triple>();
		if (query.getConstructTemplate() != null) {
			for (Triple t : query.getConstructTemplate().getTriples()) {
				constructs.add(t);
			}
		}
	}

	public void addConstruct(Triple t) {
		constructs.add(t);
		query.setConstructTemplate(new Template(BasicPattern.wrap(constructs)));
	}

	public void addAll(ConstructHandler handler) {
		constructs.addAll(handler.constructs);
		query.setConstructTemplate(new Template(BasicPattern.wrap(constructs)));
	}

	@Override
	public void setVars(Map<Var, Node> values) {
		if (values.isEmpty()) {
			return;
		}
		AbstractRewriter<Node> rw = new AbstractRewriter<Node>(values) {
		};
		query.setConstructTemplate(new Template(BasicPattern.wrap(rw
				.rewrite(constructs))));
	}

	@Override
	public void build() {
		// TODO Auto-generated method stub

	}

}
