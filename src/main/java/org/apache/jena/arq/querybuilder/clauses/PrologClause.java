package org.apache.jena.arq.querybuilder.clauses;

import java.util.Map;

import org.apache.jena.arq.querybuilder.AbstractQueryBuilder;
import org.apache.jena.arq.querybuilder.handlers.PrologHandler;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.rdf.model.Resource;

public interface PrologClause<T extends AbstractQueryBuilder<T>> {
	public PrologHandler getPrologHandler();

	public T addPrefix(String pfx, Resource uri);

	public T addPrefix(String pfx, Node uri);

	public T addPrefix(String pfx, String uri);

	public T addPrefixes(Map<String, String> prefixes);

	public T setBase(Object uri);

	public T setBase(String uri);

}
