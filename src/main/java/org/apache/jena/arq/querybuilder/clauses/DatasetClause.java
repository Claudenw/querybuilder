package org.apache.jena.arq.querybuilder.clauses;

import java.util.Collection;

import org.apache.jena.arq.querybuilder.AbstractQueryBuilder;
import org.apache.jena.arq.querybuilder.handlers.DatasetHandler;

public interface DatasetClause<T extends AbstractQueryBuilder<T>> {
	public T fromNamed(String graphName);

	public T fromNamed(Collection<String> graphNames);

	public T from(String graphName);

	public DatasetHandler getDatasetHandler();

}
