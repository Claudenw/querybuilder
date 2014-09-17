package org.apache.jena.arq.querybuilder.handlers;

import java.util.Map;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.sparql.core.Var;

public interface Handler {
	public void setVars(Map<Var, Node> values);
}
