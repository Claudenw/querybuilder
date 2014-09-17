package org.apache.jena.arq.querybuilder.handlers;

import static org.junit.Assert.assertEquals;
import java.util.List;

import org.apache.jena.arq.querybuilder.handlers.SolutionModifierHandler;
import org.junit.Before;
import org.junit.Test;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.SortCondition;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.lang.sparql_11.ParseException;

public class SolutionModifierHandlerTest extends AbstractHandlerTest {

	private Query query;
	private SolutionModifierHandler solutionModifier;

	@Before
	public void setup() {
		query = new Query();
		solutionModifier = new SolutionModifierHandler(query);
	}

	@Test
	public void testAddAll() throws ParseException {
		SolutionModifierHandler solutionModifier2 = new SolutionModifierHandler(
				new Query());
		solutionModifier2.addOrderBy("orderBy");
		solutionModifier2.addGroupBy("groupBy");
		solutionModifier2.addHaving("?having<10");
		solutionModifier2.setLimit(500);
		solutionModifier2.setOffset(200);

		solutionModifier.addAll(solutionModifier2);

		String[] s = byLine(query.toString());
		assertContainsRegex(GROUP_BY + var("groupBy"), s);
		assertContainsRegex(HAVING + OPEN_PAREN + var("having") + OPT_SPACE
				+ LT + OPT_SPACE + "10" + CLOSE_PAREN, s);
		assertContainsRegex(ORDER_BY + var("orderBy"), s);
		assertContainsRegex(LIMIT + "500", s);
		assertContainsRegex(OFFSET + "200", s);
	}

	@Test
	public void testAll() throws ParseException {
		solutionModifier.addOrderBy("orderBy");
		solutionModifier.addGroupBy("groupBy");
		solutionModifier.addHaving("SUM(?lprice) > 10");
		solutionModifier.setLimit(500);
		solutionModifier.setOffset(200);

		String[] s = byLine(query.toString());
		assertContainsRegex("GROUP BY\\s+\\?groupBy", s);
		assertContainsRegex("HAVING\\s+\\( sum\\(\\?lprice\\) > 10 \\)", s);
		assertContainsRegex("ORDER BY\\s+\\?orderBy", s);
		assertContainsRegex("LIMIT\\s+500", s);
		assertContainsRegex("OFFSET\\s+200", s);

	}

	@Test
	public void testAddOrderBy() {
		solutionModifier.addOrderBy("orderBy");
		List<SortCondition> sc = query.getOrderBy();
		assertEquals("Wrong number of conditions", 1, sc.size());
		assertEquals("Wrong value", sc.get(0).expression.asVar(),
				Var.alloc("orderBy"));

		solutionModifier.addOrderBy("orderBy2");
		sc = query.getOrderBy();
		assertEquals("Wrong number of conditions", 2, sc.size());
		assertEquals("Wrong value", sc.get(0).expression.asVar(),
				Var.alloc("orderBy"));
		assertEquals("Wrong value", sc.get(1).expression.asVar(),
				Var.alloc("orderBy2"));
	}

	@Test
	public void testAddGroupBy() {
		solutionModifier.addGroupBy("groupBy");
		String[] s = byLine(query.toString());
		assertContainsRegex("GROUP BY\\s+\\?groupBy", s);

		solutionModifier.addGroupBy("groupBy2");
		s = byLine(query.toString());
		assertContainsRegex("GROUP BY\\s+\\?groupBy\\s+\\?groupBy2", s);
	}

	@Test
	public void testAddHaving() throws ParseException {
		solutionModifier.addHaving("?having<10");
		assertContainsRegex(HAVING + OPEN_PAREN + var("having") + OPT_SPACE
				+ LT + OPT_SPACE + 10 + CLOSE_PAREN, query.toString());

		solutionModifier.addHaving("?having2");
		assertContainsRegex(HAVING + OPEN_PAREN + var("having") + OPT_SPACE
				+ LT + OPT_SPACE + 10 + CLOSE_PAREN + OPT_SPACE
				+ var("having2"), query.toString());
	}

	@Test
	public void testSetLimit() {
		solutionModifier.setLimit(500);
		String[] s = byLine(query.toString());
		assertContainsRegex("LIMIT\\s+500", s);

		solutionModifier.setLimit(200);
		s = byLine(query.toString());
		assertContainsRegex("LIMIT\\s+200", s);

		solutionModifier.setLimit(-1);
		s = byLine(query.toString());
		assertNotContainsRegex("LIMIT.*", s);

	}

	@Test
	public void testSetOffset() {
		solutionModifier.setOffset(500);
		String[] s = byLine(query.toString());
		assertContainsRegex("OFFSET\\s+500", s);

		solutionModifier.setOffset(200);
		s = byLine(query.toString());
		assertContainsRegex("OFFSET\\s+200", s);

		solutionModifier.setOffset(-1);
		s = byLine(query.toString());
		assertNotContainsRegex("OFFSET.*", s);
	}

}
