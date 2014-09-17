package org.apache.jena.arq.querybuilder.handlers;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.jena.riot.system.IRIResolver ;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.shared.PrefixMapping;
import com.hp.hpl.jena.sparql.core.Var;

public class PrologHandler implements Handler {

	private final Query query;

	public PrologHandler(Query query) {
		this.query = query;
	}

	public void setBase(IRIResolver resolver) {
		query.setBaseURI(resolver);

	}

	public void setBase(String base) {
		query.setBaseURI(base);
	}

	public void addPrefix(String pfx, String uri) {
		query.setPrefix(pfx, uri);
	}

	public void addPrefixes(Map<String, String> prefixes) {
		query.getPrefixMapping().setNsPrefixes(prefixes);
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
}
