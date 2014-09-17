package org.apache.jena.arq.querybuilder;

import java.util.Collection;

import org.apache.jena.arq.querybuilder.clauses.DatasetClause;
import org.apache.jena.arq.querybuilder.clauses.SolutionModifierClause;
import org.apache.jena.arq.querybuilder.clauses.WhereClause;
import org.apache.jena.arq.querybuilder.handlers.DatasetHandler;
import org.apache.jena.arq.querybuilder.handlers.SolutionModifierHandler;
import org.apache.jena.arq.querybuilder.handlers.WhereHandler;

import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.sparql.lang.sparql_11.ParseException;

public class AskBuilder extends AbstractQueryBuilder<AskBuilder> implements
		DatasetClause<AskBuilder>, WhereClause<AskBuilder>,
		SolutionModifierClause<AskBuilder> {
	private final DatasetHandler datasetHandler;
	private final WhereHandler whereHandler;
	private final SolutionModifierHandler solutionModifier;

	public AskBuilder() {
		super();
		query.setQueryAskType();
		datasetHandler = new DatasetHandler(query);
		whereHandler = new WhereHandler(query);
		solutionModifier = new SolutionModifierHandler(query);
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
	public AskBuilder clone() {
		AskBuilder qb = new AskBuilder();
		qb.prologHandler.addAll(prologHandler);
		qb.datasetHandler.addAll(datasetHandler);
		qb.solutionModifier.addAll(solutionModifier);
		return qb;
	}

	@Override
	public AskBuilder fromNamed(String graphName) {
		datasetHandler.fromNamed(graphName);
		return this;
	}

	@Override
	public AskBuilder fromNamed(Collection<String> graphNames) {
		datasetHandler.fromNamed(graphNames);
		return this;
	}

	@Override
	public AskBuilder from(String graphName) {
		datasetHandler.from(graphName);
		return this;
	}

	@Override
	public AskBuilder addWhere(Triple t) {
		whereHandler.addWhere(t);
		return this;
	}

	@Override
	public AskBuilder addWhere(Object s, Object p, Object o) {
		addWhere(new Triple(makeNode(s), makeNode(p), makeNode(o)));
		return this;
	}

	@Override
	public AskBuilder addOptional(Triple t) {
		whereHandler.addOptional(t);
		return this;
	}

	@Override
	public AskBuilder addOptional(Object s, Object p, Object o) {
		addOptional(new Triple(makeNode(s), makeNode(p), makeNode(o)));
		return this;
	}

	@Override
	public AskBuilder addFilter(String s) throws ParseException {
		whereHandler.addFilter(s);
		return this;
	}

	@Override
	public AskBuilder addSubQuery(SelectBuilder subQuery) {
		prologHandler.addAll(subQuery.getPrologHandler());
		whereHandler.addSubQuery(subQuery);
		return this;
	}

	@Override
	public AskBuilder addUnion(SelectBuilder subQuery) {
		whereHandler.addUnion(subQuery);
		return this;
	}

	public AskBuilder addGraph(Object graph, SelectBuilder subQuery) {
		prologHandler.addAll(subQuery.getPrologHandler());
		whereHandler.addGraph(makeNode(graph), subQuery.getWhereHandler());
		return this;
	}

	@Override
	public AskBuilder addOrderBy(String orderBy) {
		solutionModifier.addOrderBy(orderBy);
		return this;
	}

	@Override
	public AskBuilder addGroupBy(String groupBy) {
		solutionModifier.addGroupBy(groupBy);
		return this;
	}

	@Override
	public AskBuilder addHaving(String having) throws ParseException {
		solutionModifier.addHaving(having);
		return this;
	}

	@Override
	public AskBuilder setLimit(int limit) {
		solutionModifier.setLimit(limit);
		return this;
	}

	@Override
	public AskBuilder setOffset(int offset) {
		solutionModifier.setOffset(offset);
		return this;
	}

	@Override
	public SolutionModifierHandler getSolutionModifierHandler() {
		return solutionModifier;
	}

}