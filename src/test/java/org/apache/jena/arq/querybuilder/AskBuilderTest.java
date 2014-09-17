package org.apache.jena.arq.querybuilder;

import org.junit.runner.RunWith;
import org.xenei.junit.contract.Contract;
import org.xenei.junit.contract.ContractImpl;
import org.xenei.junit.contract.ContractSuite;
import org.xenei.junit.contract.IProducer;

@RunWith(ContractSuite.class)
@ContractImpl(AskBuilder.class)
public class AskBuilderTest {

	// create the producer to inject
	private IProducer<AskBuilder> producer = new IProducer<AskBuilder>() {

		@Override
		public AskBuilder newInstance() {
			return new AskBuilder();
		}

		@Override
		public void cleanUp() {
			// no cleanup required
		}

	};

	public AskBuilderTest() {
	}

	@Contract.Inject
	public IProducer<AskBuilder> getProducer() {
		return producer;
	}

}
