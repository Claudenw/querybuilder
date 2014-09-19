package org.apache.jena.arq.querybuilder.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.jena.arq.querybuilder.rewriters.AbstractRewriter;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.sparql.core.BasicPattern;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.syntax.Template;

public class ConstructHandler implements Handler {
	private final Query query;
	private List<Triple> constructs;

	public ConstructHandler(Query query) {
		this.query = query;
		constructs = new ArrayList<Triple>();
		if (query.getConstructTemplate() != null) {
			for (Triple t : query.getConstructTemplate().getTriples()) {
				constructs.add(t);
			}
		}
	}

	public void addConstruct(Triple t) {
		constructs.add(t);
		query.setConstructTemplate(new Template(BasicPattern.wrap(constructs)));
	}

	public void addAll(ConstructHandler handler) {
		constructs.addAll(handler.constructs);
		query.setConstructTemplate(new Template(BasicPattern.wrap(constructs)));
	}

	@Override
	public void setVars(Map<Var, Node> values) {
		if (values.isEmpty()) {
			return;
		}
		AbstractRewriter<Node> rw = new AbstractRewriter<Node>(values) {
		};
		query.setConstructTemplate(new Template(BasicPattern.wrap(rw
				.rewrite(constructs))));
	}

	@Override
	public void build() {
		// TODO Auto-generated method stub
		
	}

}
