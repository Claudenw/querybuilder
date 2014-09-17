package org.apache.jena.arq.querybuilder.handlers;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.jena.arq.querybuilder.handlers.DatasetHandler;
import org.junit.Before;
import org.junit.Test;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.sparql.core.Var;

public class DatasetHandlerTest extends AbstractHandlerTest {
	private Query query;
	private DatasetHandler handler;

	@Before
	public void setup() {
		query = new Query();
		handler = new DatasetHandler(query);
	}

	@Test
	public void testAddAll() {
		DatasetHandler handler2 = new DatasetHandler(new Query());
		handler2.from("foo");
		handler2.fromNamed("bar");
		handler.addAll(handler2);
		String s = query.toString();
		assertTrue(s.contains("FROM <foo>"));
		assertTrue(s.contains("FROM NAMED <bar>"));
	}

	@Test
	public void testFromNamedString() {
		handler.fromNamed("foo");
		assertTrue(query.toString().contains("FROM NAMED <foo>"));
	}

	@Test
	public void fromNamedCollection() {
		String[] names = { "foo", "bar" };
		handler.fromNamed(Arrays.asList(names));
		String s = query.toString();
		assertTrue(s.contains("FROM NAMED <foo>"));
		assertTrue(s.contains("FROM NAMED <bar>"));
	}

	@Test
	public void fromString() {
		handler.from("foo");
		assertTrue(query.toString().contains("FROM <foo>"));
	}
	
	@Test
	public void setVarsFromNamed() {
		Map<Var,Node> values = new HashMap<Var,Node>();
		handler.fromNamed( "?foo");
		handler.from( "?bar" );
		values.put( Var.alloc( "foo" ), NodeFactory.createURI( "http://example.com/foo" ));
		handler.setVars(values);
		String s = query.toString();
		assertTrue(s.contains("FROM NAMED <http://example.com/foo>"));
		assertTrue(s.contains("FROM <?bar>"));
	}

	@Test
	public void setVarsFrom() {
		Map<Var,Node> values = new HashMap<Var,Node>();
		handler.fromNamed( "?foo");
		handler.from( "?bar" );
		values.put( Var.alloc( "bar" ), NodeFactory.createURI( "http://example.com/bar" ));
		handler.setVars(values);
		String s = query.toString();
		assertTrue(s.contains("FROM NAMED <?foo>"));
		assertTrue(s.contains("FROM <http://example.com/bar>"));
	}
	
	@Test
	public void setVarsBoth() {
		Map<Var,Node> values = new HashMap<Var,Node>();
		handler.fromNamed( "?foo");
		handler.from( "?bar" );
		values.put( Var.alloc( "bar" ), NodeFactory.createURI( "http://example.com/bar" ));
		values.put( Var.alloc( "foo" ), NodeFactory.createURI( "http://example.com/foo" ));
		handler.setVars(values);
		String s = query.toString();
		assertTrue(s.contains("FROM NAMED <http://example.com/foo>"));
		assertTrue(s.contains("FROM <http://example.com/bar>"));
	}

}
