package org.apache.jena.arq.querybuilder.clauses;

import org.apache.jena.arq.querybuilder.AbstractQueryBuilder;
import org.apache.jena.arq.querybuilder.handlers.SolutionModifierHandler;

import com.hp.hpl.jena.sparql.lang.sparql_11.ParseException;

public interface SolutionModifierClause<T extends AbstractQueryBuilder<T>> {

	public T addOrderBy(String orderBy);

	public T addGroupBy(String groupBy);

	public T addHaving(String expression) throws ParseException;

	public T setLimit(int limit);

	public T setOffset(int offset);

	public SolutionModifierHandler getSolutionModifierHandler();

}
