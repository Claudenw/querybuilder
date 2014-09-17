package org.apache.jena.arq.querybuilder;

import org.junit.runner.RunWith;
import org.xenei.junit.contract.Contract;
import org.xenei.junit.contract.ContractImpl;
import org.xenei.junit.contract.ContractSuite;
import org.xenei.junit.contract.IProducer;

@RunWith(ContractSuite.class)
@ContractImpl(ConstructBuilder.class)
public class ConstructBuilderTest {

	// create the producer to inject
	private IProducer<ConstructBuilder> producer = new IProducer<ConstructBuilder>() {

		@Override
		public ConstructBuilder newInstance() {
			return new ConstructBuilder();
		}

		@Override
		public void cleanUp() {
			// no cleanup required
		}

	};

	public ConstructBuilderTest() {
	}

	@Contract.Inject
	public IProducer<ConstructBuilder> getProducer() {
		return producer;
	}

}
