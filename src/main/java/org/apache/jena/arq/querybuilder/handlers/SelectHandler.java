package org.apache.jena.arq.querybuilder.handlers;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.core.VarExprList;

public class SelectHandler implements Handler {

	private final Query query;

	public SelectHandler(Query query) {
		this.query = query;
		setDistinct(query.isDistinct());
		setReduced(query.isReduced());
	}

	public void setDistinct(boolean state) {
		query.setDistinct(state);
		if (state) {
			query.setReduced(false);
		} 
	}

	public void setReduced(boolean state) {
		query.setReduced(state);
		if (state) {
			query.setDistinct(false);
		}
	}

	public void addVar(String var) {
		if ("*".equals(var))
		{
			query.setQueryResultStar(true);
		}
		else
		{
			query.setQueryResultStar(false);
			query.addResultVar(var);
		}
	}

	public void addVar(Var var) {
		query.addResultVar(var);
	}

	public void addVar(Node var) {
		query.addResultVar(var);
	}

	public List<Var> getVars() {
		return query.getProjectVars();
	}

	public void addAll(SelectHandler selectHandler) {

		setReduced( selectHandler.query.isReduced() );
		setDistinct( selectHandler.query.isDistinct());
		query.setQueryResultStar( selectHandler.query.isQueryResultStar() );

		try {
			Field f = Query.class.getDeclaredField("projectVars");
			f.setAccessible(true);
			VarExprList projectVars = (VarExprList)f.get(selectHandler.query);
			f.set(query, new VarExprList(projectVars));
		} catch (NoSuchFieldException e) {
			throw new IllegalStateException(e);
		} catch (SecurityException e) {
			throw new IllegalStateException(e);
		} catch (IllegalAccessException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public void setVars(Map<Var, Node> values) {
		// nothing to do
	}
	
	public void build() {
		// handle the SELECT * case
		query.getProjectVars();
	}
}
