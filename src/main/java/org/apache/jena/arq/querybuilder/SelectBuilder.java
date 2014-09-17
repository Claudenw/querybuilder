package org.apache.jena.arq.querybuilder;

import java.util.Collection;
import java.util.List;
import org.apache.jena.arq.querybuilder.clauses.DatasetClause;
import org.apache.jena.arq.querybuilder.clauses.SelectClause;
import org.apache.jena.arq.querybuilder.clauses.SolutionModifierClause;
import org.apache.jena.arq.querybuilder.clauses.WhereClause;
import org.apache.jena.arq.querybuilder.handlers.DatasetHandler;
import org.apache.jena.arq.querybuilder.handlers.SelectHandler;
import org.apache.jena.arq.querybuilder.handlers.SolutionModifierHandler;
import org.apache.jena.arq.querybuilder.handlers.WhereHandler;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.lang.sparql_11.ParseException;

public class SelectBuilder extends AbstractQueryBuilder<SelectBuilder>
		implements DatasetClause<SelectBuilder>, WhereClause<SelectBuilder>,
		SolutionModifierClause<SelectBuilder>, SelectClause<SelectBuilder> {

	private DatasetHandler datasetHandler;
	private WhereHandler whereHandler;
	private SolutionModifierHandler solutionModifier;
	private SelectHandler selectHandler;

	public SelectBuilder() {
		super();
		query.setQueryConstructType();
		datasetHandler = new DatasetHandler(query);
		whereHandler = new WhereHandler(query);
		solutionModifier = new SolutionModifierHandler(query);
		selectHandler = new SelectHandler(query);
	}

	@Override
	public DatasetHandler getDatasetHandler() {
		return datasetHandler;
	}

	@Override
	public WhereHandler getWhereHandler() {
		return whereHandler;
	}

	@Override
	public SelectBuilder clone() {
		SelectBuilder qb = new SelectBuilder();
		qb.prologHandler.addAll(prologHandler);
		qb.datasetHandler.addAll(datasetHandler);
		qb.whereHandler.addAll(whereHandler);
		qb.solutionModifier.addAll(solutionModifier);
		qb.selectHandler.addAll(selectHandler);

		return qb;
	}

	@Override
	public SelectBuilder setDistinct(boolean state) {
		selectHandler.setDistinct(state);
		return this;
	}

	@Override
	public SelectBuilder setReduced(boolean state) {
		selectHandler.setReduced(state);
		return this;
	}

	@Override
	public SelectBuilder addVar(Object var) {
		if (var instanceof String) {
			selectHandler.addVar((String) var);
		} else if (var instanceof Var) {
			selectHandler.addVar((Var) var);
		} else {
			selectHandler.addVar(makeNode(var));
		}
		return this;
	}

	@Override
	public List<Var> getVars() {
		return selectHandler.getVars();
	}

	@Override
	public SelectBuilder fromNamed(String graphName) {
		datasetHandler.fromNamed(graphName);
		return this;
	}

	@Override
	public SelectBuilder fromNamed(Collection<String> graphNames) {
		datasetHandler.fromNamed(graphNames);
		return this;
	}

	@Override
	public SelectBuilder from(String graphName) {
		datasetHandler.from(graphName);
		return this;
	}

	@Override
	public SelectBuilder addOrderBy(String orderBy) {
		solutionModifier.addOrderBy(orderBy);
		return this;
	}

	@Override
	public SelectBuilder addGroupBy(String groupBy) {
		solutionModifier.addGroupBy(groupBy);
		return this;
	}

	@Override
	public SolutionModifierHandler getSolutionModifierHandler() {
		return solutionModifier;
	}

	@Override
	public SelectBuilder addHaving(String having) throws ParseException {
		solutionModifier.addHaving(having);
		return this;
	}

	@Override
	public SelectBuilder setLimit(int limit) {
		solutionModifier.setLimit(limit);
		return this;
	}

	@Override
	public SelectBuilder setOffset(int offset) {
		solutionModifier.setOffset(offset);
		return this;
	}

	private static String toString(Node node) {
		if (node.isBlank()) {
			return node.getBlankNodeLabel();
		}
		if (node.isLiteral()) {
			return node.toString();
		}
		if (node.isURI()) {
			return String.format("<%s>", node.getURI());
		}
		if (node.isVariable()) {
			return String.format("?%s", node.getName());
		}
		return node.toString();
	}

	public static String makeString(Object o) {
		if (o instanceof RDFNode) {
			return toString(((RDFNode) o).asNode());
		}
		if (o instanceof Node) {
			return toString((Node) o);
		}
		return o.toString();
	}

	@Override
	public SelectBuilder addWhere(Triple t) {
		whereHandler.addWhere(t);
		return this;
	}

	@Override
	public SelectBuilder addWhere(Object s, Object p, Object o) {
		addWhere(new Triple(makeNode(s), makeNode(p), makeNode(o)));
		return this;
	}

	@Override
	public SelectBuilder addOptional(Triple t) {
		whereHandler.addOptional(t);
		return this;
	}

	@Override
	public SelectBuilder addOptional(Object s, Object p, Object o) {
		addOptional(new Triple(makeNode(s), makeNode(p), makeNode(o)));
		return this;
	}

	@Override
	public SelectBuilder addFilter(String s) throws ParseException {
		whereHandler.addFilter(s);
		return this;
	}

	@Override
	public SelectBuilder addSubQuery(SelectBuilder subQuery) {
		prologHandler.addAll(subQuery.prologHandler);
		whereHandler.addSubQuery(subQuery);
		return this;
	}

	@Override
	public SelectBuilder addUnion(SelectBuilder subQuery) {
		whereHandler.addUnion(subQuery);
		return this;
	}

	public SelectBuilder addGraph(Object graph, SelectBuilder subQuery) {
		prologHandler.addAll(subQuery.prologHandler);
		whereHandler.addGraph(makeNode(graph), subQuery.whereHandler);
		return this;
	}

	@Override
	public SelectHandler getSelectHandler() {
		return selectHandler;
	}

}