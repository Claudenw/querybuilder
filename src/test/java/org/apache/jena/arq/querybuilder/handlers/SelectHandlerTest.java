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
	public void testAddAll() {
		// handler.setBase("foo");
		// handler.addPrefix("pfx", "uri");
		// String[] lst = byLine(query.toString());
		// assertContainsRegex("PREFIX\\s+pfx:\\s+\\<uri\\>", lst);
		// assertContainsRegex("BASE\\s+\\<.+/foo\\>", lst);
	}

}
