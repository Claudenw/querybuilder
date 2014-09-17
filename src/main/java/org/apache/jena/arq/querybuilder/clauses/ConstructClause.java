package org.apache.jena.arq.querybuilder.clauses;

import org.apache.jena.arq.querybuilder.AbstractQueryBuilder;
import org.apache.jena.arq.querybuilder.handlers.ConstructHandler;

import com.hp.hpl.jena.graph.FrontsTriple;
import com.hp.hpl.jena.graph.Triple;

public interface ConstructClause<T extends AbstractQueryBuilder<T>> {

	public T addConstruct(Triple t);

	public T addConstruct(FrontsTriple t);

	public T addConstruct(Object s, Object p, Object o);

	public ConstructHandler getConstructHandler();

}
