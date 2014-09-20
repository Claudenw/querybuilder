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
package org.apache.jena.arq.querybuilder.handlers;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.jena.riot.system.IRIResolver;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.shared.PrefixMapping;
import com.hp.hpl.jena.sparql.core.Var;

public class PrologHandler implements Handler {

	private final Query query;

	public PrologHandler(Query query) {
		this.query = query;
	}

	private String canonicalPfx(String x)
    {
        if ( x.endsWith(":") )
            return x.substring(0, x.length()-2) ;
        return x ;
    }
	
	public void setBase(IRIResolver resolver) {
		query.setBaseURI(resolver);

	}

	public void setBase(String base) {
		query.setBaseURI(base);
	}

	public void addPrefix(String pfx, String uri) {
		query.setPrefix(canonicalPfx(pfx), uri);
	}

	public void addPrefixes(Map<String, String> prefixes) {
		for (Map.Entry<String,String> e : prefixes.entrySet() )
		{
			addPrefix( e.getKey(), e.getValue());
		}
	}

	public void addPrefixes(PrefixMapping prefixes) {
		query.getPrefixMapping().setNsPrefixes(prefixes);
	}

	public void addAll(PrologHandler pfxHandler) {
		String val = StringUtils.defaultIfEmpty(pfxHandler.query.getBaseURI(),
				query.getBaseURI());
		if (val != null) {
			setBase(val);
		}
		addPrefixes(pfxHandler.query.getPrefixMapping());
	}

	@Override
	public void setVars(Map<Var, Node> values) {
		// nothing to do
	}

	@Override
	public void build() {
		// TODO Auto-generated method stub

	}
}
