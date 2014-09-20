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

import java.io.ByteArrayInputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.jena.arq.querybuilder.SelectBuilder;
import org.apache.jena.arq.querybuilder.clauses.ConstructClause;
import org.apache.jena.arq.querybuilder.clauses.DatasetClause;
import org.apache.jena.arq.querybuilder.clauses.SolutionModifierClause;
import org.apache.jena.arq.querybuilder.clauses.WhereClause;
import org.apache.jena.arq.querybuilder.rewriters.ElementRewriter;
import com.hp.hpl.jena.graph.FrontsNode;
import com.hp.hpl.jena.graph.FrontsTriple;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.expr.Expr;
import com.hp.hpl.jena.sparql.lang.sparql_11.ParseException;
import com.hp.hpl.jena.sparql.lang.sparql_11.SPARQLParser11;
import com.hp.hpl.jena.sparql.syntax.Element;
import com.hp.hpl.jena.sparql.syntax.ElementFilter;
import com.hp.hpl.jena.sparql.syntax.ElementGroup;
import com.hp.hpl.jena.sparql.syntax.ElementNamedGraph;
import com.hp.hpl.jena.sparql.syntax.ElementOptional;
import com.hp.hpl.jena.sparql.syntax.ElementSubQuery;
import com.hp.hpl.jena.sparql.syntax.ElementTriplesBlock;
import com.hp.hpl.jena.sparql.syntax.ElementUnion;

public class WhereHandler implements Handler {

	private final Query query;

	public WhereHandler(Query query) {
		this.query = query;
	}

	public void addAll(WhereHandler whereHandler) {
		Element e = whereHandler.query.getQueryPattern();
		Element locE = query.getQueryPattern();
		if (e != null) {
			if (locE == null) {
				query.setQueryPattern(e);
			} else {
				ElementTriplesBlock locEtb = (ElementTriplesBlock) locE;
				ElementTriplesBlock etp = (ElementTriplesBlock) e;
				Iterator<Triple> iter = etp.patternElts();
				while (iter.hasNext()) {
					locEtb.addTriple(iter.next());
				}
			}
		}
	}

	private Element getElement() {
		ElementGroup eg = getClause();
		if (eg.getElements().size() == 1) {
			return eg.getElements().get(0);
		}
		return eg;
	}

	private ElementGroup getClause() {
		ElementGroup e = (ElementGroup) query.getQueryPattern();
		if (e == null) {
			e = new ElementGroup();
			query.setQueryPattern(e);
		}
		return e;
	}

	private void testTriple(Triple t) {
		// verify Triple is valid
		boolean validSubject = t.getSubject().isURI()
				|| t.getSubject().isBlank() || t.getSubject().isVariable()
				|| t.getSubject().equals(Node.ANY);
		boolean validPredicate = t.getPredicate().isURI()
				|| t.getPredicate().isVariable()
				|| t.getPredicate().equals(Node.ANY);
		boolean validObject = t.getObject().isURI()
				|| t.getObject().isLiteral() || t.getObject().isBlank()
				|| t.getObject().isVariable() || t.getObject().equals(Node.ANY);

		if (!validSubject || !validPredicate || !validObject) {
			StringBuilder sb = new StringBuilder();
			if (!validSubject) {
				sb.append(String
						.format("Subject (%s) must be a URI, blank, variable, or a wildcard. %n",
								t.getSubject()));
			}
			if (!validPredicate) {
				sb.append(String
						.format("Predicate (%s) must be a URI , variable, or a wildcard. %n",
								t.getPredicate()));
			}
			if (!validObject) {
				sb.append(String
						.format("Object (%s) must be a URI, literal, blank, , variable, or a wildcard. %n",
								t.getObject()));
			}
			if (!validSubject || !validPredicate) {
				sb.append(String
						.format("Is a prefix missing?  Prefix must be defined before use. %n"));
			}
			throw new IllegalArgumentException(sb.toString());
		}
	}

	public void addWhere(Triple t) {
		testTriple(t);
		ElementGroup eg = getClause();
		List<Element> lst = eg.getElements();
		if (lst.isEmpty()) {
			ElementTriplesBlock etb = new ElementTriplesBlock();
			etb.addTriple(t);
			eg.addElement(etb);
		} else {
			Element e = lst.get(lst.size() - 1);
			if (e instanceof ElementTriplesBlock) {
				ElementTriplesBlock etb = (ElementTriplesBlock) e;
				etb.addTriple(t);
			} else {
				ElementTriplesBlock etb = new ElementTriplesBlock();
				etb.addTriple(t);
				eg.addElement(etb);
			}

		}
	}

	public void addOptional(Triple t) {
		testTriple(t);
		ElementTriplesBlock etb = new ElementTriplesBlock();
		etb.addTriple(t);
		ElementOptional opt = new ElementOptional(etb);
		getClause().addElement(opt);
	}

	public void addFilter(String expression) throws ParseException {
		String filterClause = "FILTER( " + expression + ")";
		SPARQLParser11 parser = new SPARQLParser11(new ByteArrayInputStream(
				filterClause.getBytes()));
		getClause().addElement(parser.Filter());
	}

	public void addFilter(Expr expr) {
		getClause().addElement(new ElementFilter(expr));
	}

	public void addSubQuery(SelectBuilder subQuery) {
		getClause().addElement(makeSubQuery(subQuery));
	}

	private ElementSubQuery makeSubQuery(SelectBuilder subQuery) {
		Query q = new Query();
		PrologHandler ph = new PrologHandler(query);
		ph.addAll(subQuery.getPrologHandler());

		for (Var v : subQuery.getVars()) {
			q.addResultVar(v);
			q.setQuerySelectType();
		}

		if (subQuery instanceof ConstructClause) {
			ConstructHandler ch = new ConstructHandler(q);
			ch.addAll(((ConstructClause<?>) subQuery).getConstructHandler());

		}
		if (subQuery instanceof DatasetClause) {
			DatasetHandler dh = new DatasetHandler(q);
			dh.addAll(((DatasetClause<?>) subQuery).getDatasetHandler());

		}
		if (subQuery instanceof SolutionModifierClause) {
			SolutionModifierHandler smh = new SolutionModifierHandler(q);
			smh.addAll(((SolutionModifierClause<?>) subQuery)
					.getSolutionModifierHandler());

		}
		if (subQuery instanceof WhereClause) {
			WhereHandler wh = new WhereHandler(q);
			wh.addAll(((WhereClause<?>) subQuery).getWhereHandler());

		}
		return new ElementSubQuery(q);

	}

	public void addUnion(SelectBuilder subQuery) {
		ElementUnion union = new ElementUnion();
		if (subQuery.getVars().size() > 0) {
			union.addElement(makeSubQuery(subQuery));
		} else {
			PrologHandler ph = new PrologHandler(query);
			ph.addAll(subQuery.getPrologHandler());
			for (Element el : subQuery.getWhereHandler().getClause()
					.getElements()) {
				union.addElement(el);
			}
		}
		getClause().addElement(union);
	}

	public void addGraph(Node graph, WhereHandler subQuery) {
		getClause().addElement(
				new ElementNamedGraph(graph, subQuery.getElement()));
	}

	@Override
	public void setVars(Map<Var, Node> values) {
		if (values.isEmpty()) {
			return;
		}

		Element e = query.getQueryPattern();
		if (e != null) {
			ElementRewriter r = new ElementRewriter(values);
			e.visit(r);
			query.setQueryPattern(r.getResult());
		}
	}

	@Override
	public void build() {
		// TODO Auto-generated method stub

	}
}
