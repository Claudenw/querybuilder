package org.apache.jena.arq.querybuilder.handlers;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.sparql.core.Var;

public class SelectHandler implements Handler {

	private final Query query;
	private String distinctOrReduced;

	public SelectHandler(Query query) {
		this.query = query;
		setDistinct(query.isDistinct());
		setReduced(query.isReduced());
	}

	public void setDistinct(boolean state) {
		query.setDistinct(state);
		if (state) {
			query.setReduced(false);
		} else {
			if ("DISTINCT".equals(distinctOrReduced)) {
				distinctOrReduced = null;
			}
		}

	}

	public void setReduced(boolean state) {
		query.setReduced(state);
		if (state) {
			distinctOrReduced = "REDUCED";
			query.setDistinct(false);
		} else {
			if ("REDUCED".equals(distinctOrReduced)) {
				distinctOrReduced = null;
			}
		}
	}

	public void addVar(String var) {
		query.addResultVar(var);
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
		if (selectHandler.distinctOrReduced != null) {
			distinctOrReduced = selectHandler.distinctOrReduced;
		}

		try {
			Field f = Query.class.getDeclaredField("projectVars");
			f.setAccessible(true);
			f.set(query, f.get(selectHandler.query));
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
}
