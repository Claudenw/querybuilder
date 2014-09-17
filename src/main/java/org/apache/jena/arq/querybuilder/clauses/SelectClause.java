package org.apache.jena.arq.querybuilder.clauses;

import java.util.List;
import org.apache.jena.arq.querybuilder.AbstractQueryBuilder;
import org.apache.jena.arq.querybuilder.handlers.SelectHandler;

import com.hp.hpl.jena.sparql.core.Var;

public interface SelectClause<T extends AbstractQueryBuilder<T>> {
	public SelectHandler getSelectHandler();

	public T setDistinct(boolean state);

	public T setReduced(boolean state);

	public T addVar(Object var);

	public List<Var> getVars();

}
