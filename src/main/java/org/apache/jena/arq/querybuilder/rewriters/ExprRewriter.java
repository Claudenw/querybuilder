package org.apache.jena.arq.querybuilder.rewriters;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.query.SortCondition;
import com.hp.hpl.jena.sparql.algebra.Op;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.expr.Expr;
import com.hp.hpl.jena.sparql.expr.ExprAggregator;
import com.hp.hpl.jena.sparql.expr.ExprFunction0;
import com.hp.hpl.jena.sparql.expr.ExprFunction1;
import com.hp.hpl.jena.sparql.expr.ExprFunction2;
import com.hp.hpl.jena.sparql.expr.ExprFunction3;
import com.hp.hpl.jena.sparql.expr.ExprFunctionN;
import com.hp.hpl.jena.sparql.expr.ExprFunctionOp;
import com.hp.hpl.jena.sparql.expr.ExprList;
import com.hp.hpl.jena.sparql.expr.ExprNode;
import com.hp.hpl.jena.sparql.expr.ExprVar;
import com.hp.hpl.jena.sparql.expr.ExprVisitor;
import com.hp.hpl.jena.sparql.expr.NodeValue;
import com.hp.hpl.jena.sparql.syntax.Element;

public class ExprRewriter extends AbstractRewriter<Expr> implements ExprVisitor {

	public ExprRewriter(Map<Var, Node> values) {
		super(values);
	}

	@Override
	public void startVisit() {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(ExprFunction0 func) {
		push(func);
	}

	@Override
	public void visit(ExprFunction1 func) {
		func.getArg().visit(this);
		push(func.copy(pop()));

	}

	@Override
	public void visit(ExprFunction2 func) {
		// reverse order so they pop in the right order
		func.getArg2().visit(this);
		func.getArg1().visit(this);
		push(func.copy(pop(), pop()));
	}

	@Override
	public void visit(ExprFunction3 func) {
		func.getArg3().visit(this);
		func.getArg2().visit(this);
		func.getArg1().visit(this);
		push(func.copy(pop(), pop(), pop()));
	}

	@Override
	public void visit(ExprFunctionN func) {
		ExprList exprList = rewrite(new ExprList(func.getArgs()));
		ExprFunctionN retval = (ExprFunctionN) func.deepCopy();
		setExprList(retval, exprList);
		push(retval);
	}

	private void setExprList(ExprNode n, ExprList exprList) {
		try {
			Field f = n.getClass().getField("ExprList");
			f.setAccessible(true);
			f.set(n, exprList);
		} catch (NoSuchFieldException e) {
			throw new IllegalStateException(e);
		} catch (SecurityException e) {
			throw new IllegalStateException(e);
		} catch (IllegalAccessException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public void visit(ExprFunctionOp funcOp) {
		ElementRewriter elementRewriter = new ElementRewriter(values);
		funcOp.getElement().visit(elementRewriter);
		OpRewriter opRewriter = new OpRewriter(values);
		funcOp.getGraphPattern().visit(opRewriter);

		try {
			Constructor<? extends ExprFunctionOp> con = funcOp.getClass()
					.getConstructor(Element.class, Op.class);
			push(con.newInstance(elementRewriter.pop(), opRewriter.pop()));
		} catch (NoSuchMethodException e) {
			throw new IllegalStateException(e);
		} catch (SecurityException e) {
			throw new IllegalStateException(e);
		} catch (InstantiationException e) {
			throw new IllegalStateException(e);
		} catch (IllegalAccessException e) {
			throw new IllegalStateException(e);
		} catch (InvocationTargetException e) {
			throw new IllegalStateException(e);
		}

	}

	@Override
	public void visit(NodeValue nv) {
		NodeValueRewriter rewriter = new NodeValueRewriter(values);
		nv.visit(rewriter);
		push(rewriter.pop());
	}

	@Override
	public void visit(ExprVar nv) {
		Node n = changeNode(nv.asVar());
		if (n.isVariable()) {
			push(new ExprVar(n));
		} else {
			push(NodeValue.makeNode(n));
		}
	}

	@Override
	public void visit(ExprAggregator eAgg) {
		Node n = changeNode(eAgg.getVar());
		if (n.equals(eAgg.getVar())) {
			push(eAgg);
		} else {
			push(NodeValue.makeNode(n));
		}

	}

	@Override
	public void finishVisit() {
		// TODO Auto-generated method stub

	}

	public final List<SortCondition> rewriteSortConditionList(
			List<SortCondition> lst) {
		if (lst == null) {
			return null;
		}
		List<SortCondition> retval = new ArrayList<SortCondition>();
		for (SortCondition sc : lst) {
			retval.add(rewrite(sc));
		}
		return retval;
	}

	public final SortCondition rewrite(SortCondition sortCondition) {
		sortCondition.getExpression().visit(this);
		return new SortCondition(pop(), sortCondition.getDirection());
	}

	public final ExprList rewrite(ExprList lst) {
		if (lst == null) {
			return null;
		}
		ExprList exprList = new ExprList();
		int limit = lst.size();
		for (int i = 0; i < limit; i++) {
			lst.get(i).visit(this);
			exprList.add(pop());
		}
		return exprList;
	}
}