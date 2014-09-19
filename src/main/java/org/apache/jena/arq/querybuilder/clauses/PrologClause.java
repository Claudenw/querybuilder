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

import java.util.Map;

import org.apache.jena.arq.querybuilder.AbstractQueryBuilder;
import org.apache.jena.arq.querybuilder.handlers.PrologHandler;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * Interface that defines the PrologClause as per 
 * http://www.w3.org/TR/2013/REC-sparql11-query-20130321/#rPrologue
 *
 * @param <T> The Builder type that the clause is part of.
 */
public interface PrologClause<T extends AbstractQueryBuilder<T>> {
	/**
	 * @return The PrologHandler this clause is using.
	 */
	public PrologHandler getPrologHandler();

	/**
	 * Add a prefix.
	 * @param pfx The prefix.
	 * @param uri The URI for the prefix
	 * @return The builder for chaining.
	 */
	public T addPrefix(String pfx, Resource uri);

	/**
	 * Add a prefix.
	 * @param pfx The prefix.
	 * @param uri The URI for the prefix
	 * @return The builder for chaining.
	 */
	public T addPrefix(String pfx, Node uri);

	/**
	 * Add a prefix.
	 * @param pfx The prefix.
	 * @param uri The URI for the prefix
	 * @return The builder for chaining.
	 */
	public T addPrefix(String pfx, String uri);

	/**
	 * Add a prefix.
	 * @param prefixes A mapping of prefix to URI to add.
	 * @return The builder for chaining.
	 */
	public T addPrefixes(Map<String, String> prefixes);

	/**
	 * Set the base URI.
	 * @param uri The base URI to use.
	 * @return The builder for chaining.
	 */
	public T setBase(Object uri);

	/**
	 * Set the base URI.
	 * @param uri The base URI to use.
	 * @return The builder for chaining.
	 */
	public T setBase(String uri);

}
