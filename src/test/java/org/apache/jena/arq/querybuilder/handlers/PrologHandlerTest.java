package org.apache.jena.arq.querybuilder.handlers;

import java.util.HashMap;
import java.util.Map;

import org.apache.jena.arq.querybuilder.handlers.PrologHandler;
import org.junit.Before;
import org.junit.Test;

import com.hp.hpl.jena.query.Query;

public class PrologHandlerTest extends AbstractHandlerTest {

	private PrologHandler handler;
	private Query query;

	@Before
	public void setup() {
		query = new Query();
		handler = new PrologHandler(query);
	}

	@Test
	public void testAddPrefixString() {
		handler.addPrefix("pfx", "uri");
		String[] lst = byLine(query.toString());
		assertContainsRegex("PREFIX\\s+pfx:\\s+\\<uri\\>", lst);
	}

	@Test
	public void testAddPrefixHandler() {
		PrologHandler handler2 = new PrologHandler(new Query());
		handler2.addPrefix("pfx", "uri");
		handler.addAll(handler2);
		String[] lst = byLine(query.toString());
		assertContainsRegex("PREFIX\\s+pfx:\\s+\\<uri\\>", lst);
	}

	@Test
	public void testAddPrefixes() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("pfx", "uri");
		map.put("pfx2", "uri2");
		handler.addPrefixes(map);
		String[] lst = byLine(query.toString());
		assertContainsRegex("PREFIX\\s+pfx:\\s+\\<uri\\>", lst);
		assertContainsRegex("PREFIX\\s+pfx2:\\s+\\<uri2\\>", lst);
	}

	@Test
	public void testAddDuplicatePrefix() {
		handler.addPrefix("pfx", "uri");
		handler.addPrefix("pfx", "uri");
		String[] lst = byLine(query.toString());
		assertContainsRegex("PREFIX\\s+pfx:\\s+\\<uri\\>", lst);
	}

	@Test
	public void testSetBaseString() {
		handler.setBase("foo");
		String[] lst = byLine(query.toString());
		assertContainsRegex("BASE\\s+\\<.+/foo\\>", lst);
	}

	@Test
	public void testBaseAndPrefix() {
		handler.setBase("foo");
		handler.addPrefix("pfx", "uri");
		String[] lst = byLine(query.toString());
		assertContainsRegex("PREFIX\\s+pfx:\\s+\\<uri\\>", lst);
		assertContainsRegex("BASE\\s+\\<.+/foo\\>", lst);
	}

}
