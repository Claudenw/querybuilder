package org.apache.jena.arq.querybuilder.handlers;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import org.apache.jena.arq.querybuilder.rewriters.ExprRewriter;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.SortCondition;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.core.VarExprList;
import com.hp.hpl.jena.sparql.expr.Expr;
import com.hp.hpl.jena.sparql.expr.ExprList;
import com.hp.hpl.jena.sparql.expr.ExprVar;
import com.hp.hpl.jena.sparql.lang.sparql_11.ParseException;
import com.hp.hpl.jena.sparql.lang.sparql_11.SPARQLParser11;

public class SolutionModifierHandler implements Handler {
	public enum Order {
		ASCENDING, DESCENDING
	};

	private final Query query;

	public SolutionModifierHandler(Query query) {
		this.query = query;
	}

	public void addAll(SolutionModifierHandler solutionModifier) {
		List<SortCondition> lst = solutionModifier.query.getOrderBy();
		if (lst != null) {
			for (SortCondition sc : lst) {
				query.addOrderBy(sc);
			}
		}
		query.getGroupBy().addAll(solutionModifier.query.getGroupBy());
		query.getHavingExprs().addAll(solutionModifier.query.getHavingExprs());
		query.setLimit(solutionModifier.query.getLimit());
		query.setOffset(solutionModifier.query.getOffset());
	}

	public void addOrderBy(String varName) {
		query.addOrderBy(varName, Query.ORDER_DEFAULT);
	}

	public void addOrderBy(String varName, Order order) {
		query.addOrderBy(varName,
				order == Order.ASCENDING ? Query.ORDER_ASCENDING
						: Query.ORDER_DESCENDING);
	}

	public void addOrderBy(SortCondition condition) {
		query.addOrderBy(condition);
	}

	public void addOrderBy(Expr expr) {
		query.addOrderBy(expr, Query.ORDER_DEFAULT);
	}

	public void addOrderBy(Expr expr, Order order) {
		query.addOrderBy(expr, order == Order.ASCENDING ? Query.ORDER_ASCENDING
				: Query.ORDER_DESCENDING);
	}

	public void addOrderBy(Node node) {
		query.addOrderBy(node, Query.ORDER_DEFAULT);
	}

	public void addOrderBy(Node node, Order order) {
		query.addOrderBy(node, order == Order.ASCENDING ? Query.ORDER_ASCENDING
				: Query.ORDER_DESCENDING);
	}

	public void addGroupBy(String varName) {
		query.addGroupBy(varName);
	}

	public void addGroupBy(Expr expr) {
		query.addGroupBy(expr);
	}

	public void addGroupBy(Node node) {
		query.addGroupBy(node);
	}

	public void addGroupBy(Var var, Expr expr) {
		query.addGroupBy(var, expr);
	}

	public void addHaving(String expression) throws ParseException {
		String havingClause = "HAVING (" + expression + " )";
		SPARQLParser11 parser = new SPARQLParser11(new ByteArrayInputStream(
				havingClause.getBytes()));
		parser.setQuery(query);
		parser.HavingClause();
	}

	public void addHaving(Node exprNode) {
		query.addHavingCondition(new ExprVar(exprNode));
	}

	public void addHaving(Var var) {
		query.addHavingCondition(new ExprVar(var));
	}

	public void addHaving(Expr expr) {
		query.addHavingCondition(expr);
	}

	public void setLimit(int limit) {
		query.setLimit(limit < 1 ? Query.NOLIMIT : limit);
	}

	public void setOffset(int offset) {
		query.setOffset(offset < 1 ? Query.NOLIMIT : offset);
	}

	@Override
	public void setVars(Map<Var, Node> values) {
		if (values.isEmpty()) {
			return;
		}

		ExprRewriter exprRewriter = new ExprRewriter(values);

		ExprList having = exprRewriter.rewrite(new ExprList(query
				.getHavingExprs()));
		List<SortCondition> orderBy = exprRewriter
				.rewriteSortConditionList(query.getOrderBy());

		VarExprList groupBy = exprRewriter.rewrite(query.getGroupBy());

		query.getHavingExprs().clear();
		query.getHavingExprs().addAll(having.getList());
		if (orderBy != null) {
			if (query.getOrderBy() == null) {
				for (SortCondition sc : orderBy) {
					query.addOrderBy(sc);
				}
			} else {
				query.getOrderBy().clear();
				query.getOrderBy().addAll(orderBy);
			}
		}

		try {
			Field f = Query.class.getDeclaredField("groupVars");
			f.setAccessible(true);
			f.set(query, groupBy);
		} catch (NoSuchFieldException e) {
			throw new IllegalStateException(e);
		} catch (SecurityException e) {
			throw new IllegalStateException(e);
		} catch (IllegalAccessException e) {
			throw new IllegalStateException(e);
		}

	}
}
