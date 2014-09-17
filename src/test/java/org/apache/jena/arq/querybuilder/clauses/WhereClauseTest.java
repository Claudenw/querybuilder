package org.apache.jena.arq.querybuilder.clauses;

import static org.junit.Assert.assertNotNull;
import org.apache.jena.arq.querybuilder.AbstractQueryBuilder;
import org.apache.jena.arq.querybuilder.SelectBuilder;
import org.apache.jena.arq.querybuilder.clauses.WhereClause;
import org.apache.jena.arq.querybuilder.handlers.WhereHandler;
import org.junit.After;
import org.xenei.junit.contract.Contract;
import org.xenei.junit.contract.ContractTest;
import org.xenei.junit.contract.IProducer;

import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.sparql.lang.sparql_11.ParseException;

@Contract(WhereClause.class)
public class WhereClauseTest<T extends WhereClause<?>> extends
		AbstractClauseTest {

	// the producer we will user
	private IProducer<T> producer;

	@Contract.Inject
	// define the method to set producer.
	public final void setProducer(IProducer<T> producer) {
		this.producer = producer;
	}

	protected final IProducer<T> getProducer() {
		return producer;
	}

	@After
	public final void cleanupDatasetClauseTest() {
		getProducer().cleanUp(); // clean up the producer for the next run
	}

	@ContractTest
	public void testGetWhereHandler() {
		WhereClause<?> whereClause = getProducer().newInstance();
		WhereHandler handler = whereClause.getWhereHandler();
		assertNotNull(handler);
	}

	@ContractTest
	public void testAddWhereTriple() {
		WhereClause<?> whereClause = getProducer().newInstance();
		AbstractQueryBuilder<?> builder = whereClause.addWhere(new Triple(
				NodeFactory.createURI("one"), NodeFactory.createURI("two"),
				NodeFactory.createURI("three")));

		String[] s = byLine(builder);
		assertContainsRegex("<one>\\s+<two>\\s+<three>\\s*\\.", s);
	}

	@ContractTest
	public void testAddWhereObjects() {
		WhereClause<?> whereClause = getProducer().newInstance();
		AbstractQueryBuilder<?> builder = whereClause.addWhere(
				NodeFactory.createURI("one"), NodeFactory.createURI("two"),
				NodeFactory.createURI("three"));

		String[] s = byLine(builder);
		assertContainsRegex("<one>\\s+<two>\\s+<three>\\s*\\.", s);
	}

	@ContractTest
	public void testAddWhereStrings() {
		WhereClause<?> whereClause = getProducer().newInstance();
		AbstractQueryBuilder<?> builder = whereClause.addWhere("<one>",
				"<two>", "three");
		assertContainsRegex(WHERE + OPEN_CURLY + node("one") + SPACE
				+ node("two") + SPACE + quote("three") + "\\^\\^"
				+ node("http://www.w3.org/2001/XMLSchema#string") + OPT_SPACE
				+ DOT + CLOSE_CURLY, builder.buildString());
	}

	@ContractTest
	public void testAddOptionalString() {
		WhereClause<?> whereClause = getProducer().newInstance();
		AbstractQueryBuilder<?> builder = whereClause.addOptional("<one>",
				"<two>", "three");

		assertContainsRegex(WHERE + OPEN_CURLY + "OPTIONAL" + SPACE
				+ OPEN_CURLY + node("one") + SPACE + node("two") + SPACE
				+ quote("three") + "\\^\\^"
				+ node("http://www.w3.org/2001/XMLSchema#string") + OPT_SPACE
				+ DOT + CLOSE_CURLY + CLOSE_CURLY, builder.buildString());

	}

	@ContractTest
	public void testAddOptionalObjects() {
		WhereClause<?> whereClause = getProducer().newInstance();
		AbstractQueryBuilder<?> builder = whereClause.addOptional(
				NodeFactory.createURI("one"), NodeFactory.createURI("two"),
				NodeFactory.createURI("three"));
		assertContainsRegex(WHERE + OPEN_CURLY + "OPTIONAL" + SPACE
				+ OPEN_CURLY + node("one") + SPACE + node("two") + SPACE
				+ node("three") + OPT_SPACE + DOT + CLOSE_CURLY,
				builder.buildString());
	}

	@ContractTest
	public void testAddOptionalTriple() {
		WhereClause<?> whereClause = getProducer().newInstance();
		AbstractQueryBuilder<?> builder = whereClause.addOptional(new Triple(
				NodeFactory.createURI("one"), NodeFactory.createURI("two"),
				NodeFactory.createURI("three")));

		assertContainsRegex(WHERE + OPEN_CURLY + "OPTIONAL" + SPACE
				+ OPEN_CURLY + node("one") + SPACE + node("two") + SPACE
				+ node("three") + OPT_SPACE + DOT + CLOSE_CURLY,
				builder.buildString());
	}

	@ContractTest
	public void testAddFilter() throws ParseException {
		WhereClause<?> whereClause = getProducer().newInstance();
		AbstractQueryBuilder<?> builder = whereClause.addFilter("?one<10");

		assertContainsRegex(WHERE + OPEN_CURLY + "FILTER" + OPT_SPACE
				+ OPEN_PAREN + var("one") + OPT_SPACE + LT + OPT_SPACE + "10"
				+ CLOSE_PAREN + CLOSE_CURLY, builder.buildString());
	}

	@ContractTest
	public void addSubQuery() {
		SelectBuilder sb = new SelectBuilder();
		sb.addPrefix("pfx", "urn:uri").addVar("?x")
				.addWhere("pfx:one", "pfx:two", "pfx:three");
		WhereClause<?> whereClause = getProducer().newInstance();
		AbstractQueryBuilder<?> builder = whereClause.addSubQuery(sb);

		assertContainsRegex(PREFIX + "pfx:" + SPACE + node("urn:uri") + SPACE
				+ WHERE + OPEN_CURLY + OPEN_CURLY + SELECT + var("x") + SPACE
				+ WHERE + OPEN_CURLY + "pfx:one" + SPACE + "pfx:two" + SPACE
				+ "pfx:three" + OPT_SPACE + DOT + CLOSE_CURLY,
				builder.buildString());

	}

	@ContractTest
	public void testAddUnion() {
		SelectBuilder sb = new SelectBuilder();
		sb.addPrefix("pfx", "uri").addVar("?x")
				.addWhere("<one>", "<two>", "three");
		WhereClause<?> whereClause = getProducer().newInstance();
		whereClause.getWhereHandler().addWhere(Triple.ANY);
		AbstractQueryBuilder<?> builder = whereClause.addUnion(sb);

		assertContainsRegex(PREFIX + "pfx:" + SPACE + node("uri") + ".+"
				+ UNION + OPEN_CURLY + SELECT + var("x") + SPACE + WHERE
				+ OPEN_CURLY + node("one") + SPACE + node("two") + SPACE
				+ quote("three") + "\\^\\^"
				+ node("http://www.w3.org/2001/XMLSchema#string") + OPT_SPACE
				+ DOT + CLOSE_CURLY + CLOSE_CURLY, builder.buildString());

	}

}
