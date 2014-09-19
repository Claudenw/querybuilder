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

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.core.VarExprList;

public class SelectHandlerTest extends AbstractHandlerTest {

	private SelectHandler handler;
	private Query query;

	@Before
	public void setup() {
		query = new Query();
		handler = new SelectHandler(query);
	}

	@Test
	public void testAddVarString() {
		Var v = Var.alloc("one");
		handler.addVar("one");
		VarExprList expr = query.getProject();
		assertEquals(1, expr.size());
		assertTrue(expr.contains(v));
	}

	@Test
	public void testAddVarAsterisk() {
		handler.addVar("*");
		VarExprList expr = query.getProject();
		assertEquals(0, expr.size());
		assertTrue(query.isQueryResultStar());
	}

	@Test
	public void testAddVarAfterAsterisk() {
		handler.addVar("*");
		handler.addVar("?x");
		VarExprList expr = query.getProject();
		assertEquals(1, expr.size());
		assertFalse(query.isQueryResultStar());
		assertTrue(expr.contains(Var.alloc("x")));
	}

	@Test
	public void testAddVarNode() {
		Var v = Var.alloc("one");
		handler.addVar(NodeFactory.createVariable("one"));
		VarExprList expr = query.getProject();
		assertEquals(1, expr.size());
		assertTrue(expr.contains(v));
	}

	@Test
	public void testAddVarVar() {
		Var v = Var.alloc("one");
		handler.addVar(v);
		VarExprList expr = query.getProject();
		assertEquals(1, expr.size());
		assertTrue(expr.contains(v));
	}

	@Test
	public void testSetDistinct() {
		assertFalse(query.isDistinct());
		assertFalse(query.isReduced());

		handler.setDistinct(true);
		assertTrue(query.isDistinct());
		assertFalse(query.isReduced());

		handler.setReduced(false);
		assertTrue(query.isDistinct());
		assertFalse(query.isReduced());

		handler.setReduced(true);
		assertFalse(query.isDistinct());
		assertTrue(query.isReduced());

		handler.setDistinct(true);
		assertTrue(query.isDistinct());
		assertFalse(query.isReduced());

		handler.setDistinct(false);
		assertFalse(query.isDistinct());
		assertFalse(query.isReduced());
	}

	@Test
	public void testSetReduced() {
		assertFalse(query.isDistinct());
		assertFalse(query.isReduced());

		handler.setReduced(true);
		assertFalse(query.isDistinct());
		assertTrue(query.isReduced());

		handler.setDistinct(false);
		assertFalse(query.isDistinct());
		assertTrue(query.isReduced());

		handler.setDistinct(true);
		assertTrue(query.isDistinct());
		assertFalse(query.isReduced());

		handler.setReduced(true);
		assertFalse(query.isDistinct());
		assertTrue(query.isReduced());

		handler.setReduced(false);
		assertFalse(query.isDistinct());
		assertFalse(query.isReduced());

	}

	@Test
	public void testAddAllResultStartReduced() {
		SelectHandler sh = new SelectHandler(new Query());
		sh.addVar("*");
		sh.setReduced(true);

		handler.addAll(sh);
		assertTrue(query.isReduced());
		assertTrue(query.isQueryResultStar());
	}

	@Test
	public void testAddAllVarsDistinct() {
		SelectHandler sh = new SelectHandler(new Query());
		sh.addVar("?foo");
		sh.setDistinct(true);

		handler.addAll(sh);
		assertTrue(query.isDistinct());
		assertFalse(query.isQueryResultStar());
		assertEquals(1, query.getResultVars().size());
	}

}
