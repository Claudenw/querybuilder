package org.apache.jena.arq.querybuilder.clauses;

import org.apache.jena.arq.querybuilder.AbstractQueryBuilder;
import org.apache.jena.arq.querybuilder.SelectBuilder;
import org.apache.jena.arq.querybuilder.handlers.WhereHandler;

import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.sparql.lang.sparql_11.ParseException;

public interface WhereClause<T extends AbstractQueryBuilder<T>> {

	public T addWhere(Triple t);

	public T addWhere(Object s, Object p, Object o);

	public T addOptional(Triple t);

	public T addOptional(Object s, Object p, Object o);

	public T addFilter(String s) throws ParseException;

	public T addSubQuery(SelectBuilder subQuery);

	public T addUnion(SelectBuilder subQuery);

	public WhereHandler getWhereHandler();

}
