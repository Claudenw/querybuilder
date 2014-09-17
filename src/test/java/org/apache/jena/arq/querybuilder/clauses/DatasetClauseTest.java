package org.apache.jena.arq.querybuilder.clauses;

import static org.junit.Assert.assertNotNull;
import java.util.Arrays;
import org.apache.jena.arq.querybuilder.AbstractQueryBuilder;
import org.apache.jena.arq.querybuilder.clauses.DatasetClause;
import org.apache.jena.arq.querybuilder.handlers.DatasetHandler;
import org.junit.After;
import org.xenei.junit.contract.Contract;
import org.xenei.junit.contract.ContractTest;
import org.xenei.junit.contract.IProducer;

@Contract(DatasetClause.class)
public class DatasetClauseTest<T extends DatasetClause<?>> extends
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
	public void testFromNamed() {
		DatasetClause<?> datasetClause = getProducer().newInstance();
		AbstractQueryBuilder<?> builder = datasetClause.fromNamed("name");
		String[] s = byLine(builder);
		assertContains("FROM NAMED <name>", s);
		builder = datasetClause.fromNamed("name2");
		s = byLine(builder);
		assertContains("FROM NAMED <name>", s);
		assertContains("FROM NAMED <name2>", s);
	}

	@ContractTest
	public void testFromNamedCollection() {
		String[] names = { "name", "name2" };
		DatasetClause<?> datasetClause = getProducer().newInstance();
		AbstractQueryBuilder<?> builder = datasetClause.fromNamed(Arrays
				.asList(names));
		String[] s = byLine(builder);
		assertContains("FROM NAMED <name>", s);
		assertContains("FROM NAMED <name2>", s);
	}

	@ContractTest
	public void testFrom() {
		DatasetClause<?> datasetClause = getProducer().newInstance();
		AbstractQueryBuilder<?> builder = datasetClause.from("name");
		String[] s = byLine(builder);
		assertContains("FROM <name>", s);
		builder = datasetClause.from("name2");
		s = byLine(builder);
		assertContains("FROM <name2>", s);
	}

	@ContractTest
	public void testGetDatasetHandler() {
		DatasetClause<?> datasetClause = getProducer().newInstance();
		DatasetHandler dsHandler = datasetClause.getDatasetHandler();
		assertNotNull(dsHandler);
	}

	@ContractTest
	public void testAll() {
		DatasetClause<?> datasetClause = getProducer().newInstance();
		datasetClause.fromNamed("name");
		datasetClause.fromNamed("name2");
		AbstractQueryBuilder<?> builder = datasetClause.from("name3");
		String[] s = byLine(builder);
		assertContains("FROM NAMED <name>", s);
		assertContains("FROM NAMED <name2>", s);
		assertContains("FROM <name3>", s);
	}

}
