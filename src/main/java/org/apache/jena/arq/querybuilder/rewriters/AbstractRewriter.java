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
package org.apache.jena.arq.querybuilder.rewriters;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.sparql.core.TriplePath;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.core.VarExprList;
import com.hp.hpl.jena.sparql.engine.binding.Binding;
import com.hp.hpl.jena.sparql.engine.binding.BindingHashMap;
import com.hp.hpl.jena.sparql.expr.Expr;
import com.hp.hpl.jena.sparql.util.ExprUtils;

public class AbstractRewriter<T> {

	protected final Map<Var, Node> values;
	private final Stack<T> result = new Stack<T>();

	protected AbstractRewriter(Map<Var, Node> values) {
		this.values = values;
	}

	protected final void push(T value) {
		result.push(value);
	}

	protected final T pop() {
		return result.pop();
	}

	protected final boolean isEmpty() {
		return result.isEmpty();
	}

	public final T getResult() {
		if (isEmpty()) {
			return null;
		}
		return pop();
	}

	protected final TriplePath rewrite(TriplePath t) {
		if (t.getPath() == null) {
			return new TriplePath(new Triple(changeNode(t.getSubject()),
					changeNode(t.getPredicate()), changeNode(t.getObject())));
		} else {
			PathRewriter transform = new PathRewriter(values);
			t.getPath().visit(transform);
			return new TriplePath(changeNode(t.getSubject()),
					transform.getResult(), changeNode(t.getObject()));
		}
	}

	protected final Triple rewrite(Triple t) {
		return new Triple(changeNode(t.getSubject()),
				changeNode(t.getPredicate()), changeNode(t.getObject()));
	}

	protected final Node changeNode(Node n) {
		if (n.isVariable()) {
			Var v = Var.alloc(n);

			if (values.containsKey(v)) {
				return values.get(v);
			}
		}
		return n;
	}

	protected final List<Node> changeNodes(List<Node> src) {
		List<Node> lst = new ArrayList<Node>();
		for (Node t : src) {
			lst.add(changeNode(t));
		}
		return lst;
	}

	public final List<Triple> rewrite(List<Triple> src) {
		List<Triple> lst = new ArrayList<Triple>();
		for (Triple t : src) {
			lst.add(rewrite(t));
		}
		return lst;
	}

	protected final Binding rewrite(Binding binding) {
		BindingHashMap retval = new BindingHashMap();
		Iterator<Var> iter = binding.vars();
		while (iter.hasNext()) {
			Var v = iter.next();
			Node n = changeNode(binding.get(v));
			n = n.equals(v) ? binding.get(v) : n;
			retval.add(v, n);
		}
		return retval;
	}

	public final VarExprList rewrite(VarExprList lst) {

		VarExprList retval = new VarExprList();
		for (Var v : lst.getVars()) {
			Node n = values.get(v);
			if (n != null) {
				if (n.isVariable()) {
					retval.add(Var.alloc(n));
				}
			} else {
				retval.add(v);
			}
		}

		for (Map.Entry<Var, Expr> entry : lst.getExprs().entrySet()) {
			Expr target = ExprUtils.nodeToExpr(entry.getKey());
			Node n = values.get(entry.getKey());
			Var v = entry.getKey();
			Expr e = entry.getValue();
			if (n != null) {
				if (n.isVariable()) {
					v = Var.alloc(n);
					if (target.equals(e)) {
						e = ExprUtils.nodeToExpr(n);
					}
				} else {
					v = null;
				}
			}
			if (v != null) {
				retval.add(v, e);
			}
		}
		return retval;

	}

}
