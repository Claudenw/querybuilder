package org.apache.jena.arq.querybuilder;

import org.junit.runner.RunWith;
import org.xenei.junit.contract.Contract;
import org.xenei.junit.contract.ContractImpl;
import org.xenei.junit.contract.ContractSuite;
import org.xenei.junit.contract.IProducer;

@RunWith(ContractSuite.class)
@ContractImpl(SelectBuilder.class)
public class SelectBuilderTest {

	// create the producer to inject
	private IProducer<SelectBuilder> producer = new IProducer<SelectBuilder>() {

		@Override
		public SelectBuilder newInstance() {
			return new SelectBuilder();
		}

		@Override
		public void cleanUp() {
			// no cleanup required
		}

	};

	public SelectBuilderTest() {
	}

	@Contract.Inject
	public IProducer<SelectBuilder> getProducer() {
		return producer;
	}

}
