/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jena.arq.querybuilder.clauses;

import org.apache.jena.arq.querybuilder.AbstractQueryBuilder;
import org.apache.jena.arq.querybuilder.SelectBuilder;
import org.apache.jena.arq.querybuilder.handlers.WhereHandler;

import com.hp.hpl.jena.graph.FrontsTriple;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.sparql.lang.sparql_11.ParseException;

/**
 * Interface that defines the WhereClause as per
 * http://www.w3.org/TR/2013/REC-sparql11-query-20130321/#rWhereClause
 * 
 * @param <T>
 *            The Builder type that the clause is part of.
 */
public interface WhereClause<T extends AbstractQueryBuilder<T>> {

	/**
	 * Adds a triple as to the where clause.
	 * 
	 * @param t
	 *            The triple to add
	 * @return The Builder for chaining.
	 */
	public T addWhere(Triple t);

	/**
	 * Adds a triple as to the where clause.
	 * 
	 * @param t
	 *            The triple to add
	 * @return The Builder for chaining.
	 */
	public T addWhere(FrontsTriple t);

	/**
	 * Adds a triple to the where clause.
	 * 
	 * See {@link #makeNode} for conversion of the param values.
	 * 
	 * @param s
	 *            The subject.
	 * @param p
	 *            The predicate.
	 * @param o
	 *            The object.
	 * @return The Builder for chaining.
	 */
	public T addWhere(Object s, Object p, Object o);

	/**
	 * Adds an optional triple as to the where clause.
	 * 
	 * @param t
	 *            The triple to add
	 * @return The Builder for chaining.
	 */
	public T addOptional(Triple t);

	/**
	 * Adds an optional triple as to the where clause.
	 * 
	 * @param t
	 *            The triple to add
	 * @return The Builder for chaining.
	 */
	public T addOptional(FrontsTriple t);

	/**
	 * Adds an optional triple to the where clause.
	 * 
	 * See {@link #makeNode} for conversion of the param values.
	 * 
	 * @param s
	 *            The subject.
	 * @param p
	 *            The predicate.
	 * @param o
	 *            The object.
	 * @return The Builder for chaining.
	 */
	public T addOptional(Object s, Object p, Object o);

	/**
	 * Adds a filter to the where clause
	 * 
	 * @param expression
	 *            the expression to evaluate for the filter.
	 * @return @return The Builder for chaining.
	 * @throws ParseException
	 *             If the expression can not be parsed.
	 */
	public T addFilter(String expression) throws ParseException;

	/**
	 * Add a sub query.
	 * 
	 * @param subQuery
	 *            The subquery as defined by a SelectBuilder.
	 * @return This builder for chaining.
	 */
	public T addSubQuery(SelectBuilder subQuery);

	/**
	 * Add a union.
	 * 
	 * @param union
	 *            The union as defined by a SelectBuilder.
	 * @return This builder for chaining.
	 */
	public T addUnion(SelectBuilder union);

	/**
	 * Add a graph statement to the query as per
	 * http://www.w3.org/TR/2013/REC-sparql11
	 * -query-20130321/#rGraphGraphPattern.
	 * 
	 * See {@link #makeNode} for conversion of the graph param.
	 * 
	 * @param graph
	 *            The iri or variable identifying the graph.
	 * @param subQuery
	 *            The graph to add.
	 * @return This builder for chaining.
	 */
	public T addGraph(Object graph, SelectBuilder subQuery);

	/**
	 * @return The WhereHandler used by this clause.
	 */
	public WhereHandler getWhereHandler();

}
