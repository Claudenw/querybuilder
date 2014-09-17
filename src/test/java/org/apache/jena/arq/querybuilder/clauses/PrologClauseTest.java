package org.apache.jena.arq.querybuilder.clauses;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;
import org.apache.jena.arq.querybuilder.AbstractQueryBuilder;
import org.apache.jena.arq.querybuilder.clauses.PrologClause;
import org.apache.jena.arq.querybuilder.handlers.PrologHandler;
import org.junit.After;
import org.xenei.junit.contract.Contract;
import org.xenei.junit.contract.ContractTest;
import org.xenei.junit.contract.IProducer;

import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

@Contract(PrologClause.class)
public class PrologClauseTest<T extends PrologClause<?>> extends
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
	public void testGetPrologHandler() {
		PrologClause<?> prologClause = getProducer().newInstance();
		PrologHandler handler = prologClause.getPrologHandler();
		assertNotNull(handler);
	}

	@ContractTest
	public void testAddPrefixResource() {
		PrologClause<?> prologClause = getProducer().newInstance();
		AbstractQueryBuilder<?> builder = prologClause.addPrefix("pfx",
				ResourceFactory.createResource("uri"));

		String[] s = byLine(builder);
		assertContainsRegex("PREFIX\\s+pfx:\\s+\\<uri\\>", s);
	}

	@ContractTest
	public void testAddPrefixNode() {
		PrologClause<?> prologClause = getProducer().newInstance();
		AbstractQueryBuilder<?> builder = prologClause.addPrefix("pfx",
				NodeFactory.createURI("uri"));

		String[] s = byLine(builder);
		assertContainsRegex("PREFIX\\s+pfx:\\s+\\<uri\\>", s);
	}

	@ContractTest
	public void testAddPrefixString() {
		PrologClause<?> prologClause = getProducer().newInstance();
		AbstractQueryBuilder<?> builder = prologClause.addPrefix("pfx", "uri");

		String[] s = byLine(builder);
		assertContainsRegex("PREFIX\\s+pfx:\\s+\\<uri\\>", s);
	}

	@ContractTest
	public void testAddPrefixes() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("pfx", "uri");
		map.put("pfx2", "uri2");
		PrologClause<?> prologClause = getProducer().newInstance();
		AbstractQueryBuilder<?> builder = prologClause.addPrefixes(map);
		String[] s = byLine(builder);
		assertContainsRegex("PREFIX\\s+pfx:\\s+\\<uri\\>", s);
		assertContainsRegex("PREFIX\\s+pfx2:\\s+\\<uri2\\>", s);
	}

	@ContractTest
	public void testSetBaseResource() {
		PrologClause<?> prologClause = getProducer().newInstance();
		AbstractQueryBuilder<?> builder = prologClause.setBase(ResourceFactory
				.createResource("http://example.com/uri"));

		String[] s = byLine(builder);
		assertContainsRegex("BASE\\s+\\<http://example\\.com/uri\\>", s);
	}

	@ContractTest
	public void testSetBaseNode() {
		PrologClause<?> prologClause = getProducer().newInstance();
		AbstractQueryBuilder<?> builder = prologClause.setBase(NodeFactory
				.createURI("http://example.com/uri"));

		String[] s = byLine(builder);
		assertContainsRegex("BASE\\s+\\<http://example\\.com/uri\\>", s);
	}

	@ContractTest
	public void testSetBaseString() {
		PrologClause<?> prologClause = getProducer().newInstance();
		AbstractQueryBuilder<?> builder = prologClause.setBase("uri");

		String[] s = byLine(builder);
		assertContainsRegex("BASE\\s+\\<file:\\S+/uri\\>", s);
	}

	@ContractTest
	public void testSetBaseTwice() {
		PrologClause<?> prologClause = getProducer().newInstance();
		prologClause.setBase("uri");
		AbstractQueryBuilder<?> builder = prologClause.setBase("uri2");

		String[] s = byLine(builder);
		assertContainsRegex("BASE\\s+\\<file:\\S+/uri2\\>", s);
	}

}
