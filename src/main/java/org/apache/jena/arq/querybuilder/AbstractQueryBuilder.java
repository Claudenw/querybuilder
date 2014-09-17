package org.apache.jena.arq.querybuilder;

import java.util.HashMap;
import java.util.Map;

import org.apache.jena.arq.querybuilder.clauses.ConstructClause;
import org.apache.jena.arq.querybuilder.clauses.DatasetClause;
import org.apache.jena.arq.querybuilder.clauses.PrologClause;
import org.apache.jena.arq.querybuilder.clauses.SelectClause;
import org.apache.jena.arq.querybuilder.clauses.SelectClauseTest;
import org.apache.jena.arq.querybuilder.clauses.SolutionModifierClause;
import org.apache.jena.arq.querybuilder.clauses.WhereClause;
import org.apache.jena.arq.querybuilder.handlers.ConstructHandler;
import org.apache.jena.arq.querybuilder.handlers.DatasetHandler;
import org.apache.jena.arq.querybuilder.handlers.PrologHandler;
import org.apache.jena.arq.querybuilder.handlers.SelectHandler;
import org.apache.jena.arq.querybuilder.handlers.SolutionModifierHandler;
import org.apache.jena.arq.querybuilder.handlers.WhereHandler;
import org.apache.jena.riot.RiotException;
import org.apache.jena.riot.system.PrefixMapFactory;

import com.hp.hpl.jena.graph.FrontsNode;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.graph.impl.LiteralLabelFactory;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.util.NodeFactoryExtra;

/**
 * Base class for all QueryBuilders.
 * 
 * @param <T>
 *            The derived class type. Used for return types.
 */
public abstract class AbstractQueryBuilder<T extends AbstractQueryBuilder<T>>
		implements Cloneable, PrologClause<T> {

	protected PrologHandler prologHandler;
	protected Query query;
	private Map<Var, Node> values;

	public Node makeNode(Object o) {
		if (o == null) {
			return Node.ANY;
		}
		if (o instanceof FrontsNode) {
			return ((FrontsNode) o).asNode();
		}

		if (o instanceof Node) {
			return (Node) o;
		}
		if (o instanceof String) {
			try {
				return NodeFactoryExtra.parseNode((String) o, PrefixMapFactory
						.createForInput(query.getPrefixMapping()));
			} catch (RiotException e) {
				// expected in some cases -- do nothing
			}

		}
		return NodeFactory.createLiteral(LiteralLabelFactory.create(o));
	}

	protected AbstractQueryBuilder() {
		query = new Query();
		prologHandler = new PrologHandler(query);
		values = new HashMap<Var, Node>();
	}

	public void setVar(Var var, Node value) {
		values.put(var, value);
	}

	@Override
	public PrologHandler getPrologHandler() {
		return prologHandler;
	}

	@Override
	public T addPrefix(String pfx, Resource uri) {
		return addPrefix(pfx, uri.getURI());
	}

	@Override
	public T addPrefix(String pfx, Node uri) {
		return addPrefix(pfx, uri.getURI());
	}

	@Override
	public T addPrefix(String pfx, String uri) {
		prologHandler.addPrefix(pfx, uri);
		return (T) this;
	}

	@Override
	public T addPrefixes(Map<String, String> prefixes) {
		prologHandler.addPrefixes(prefixes);
		return (T) this;
	}

	@Override
	public T setBase(String base) {
		prologHandler.setBase(base);
		return (T) this;
	}

	@Override
	public T setBase(Object base) {
		setBase(makeNode(base).getURI());
		return (T) this;
	}

	@Override
	public String toString() {
		return buildString();
	}

	public final String buildString() {
		return build().toString();
	}

	public final Query build() {
		Query q = new Query();
		PrologHandler ph = new PrologHandler(q);
		ph.addAll(prologHandler);
		ph.setVars(values);
		if (this instanceof SelectClause) {
			SelectHandler sh = new SelectHandler(q);
			sh.addAll(((SelectClause<?>) this).getSelectHandler());
			sh.setVars(values);
		}
		if (this instanceof ConstructClause) {
			ConstructHandler ch = new ConstructHandler(q);
			ch.addAll(((ConstructClause<?>) this).getConstructHandler());
			ch.setVars(values);
		}
		if (this instanceof DatasetClause) {
			DatasetHandler dh = new DatasetHandler(q);
			dh.addAll(((DatasetClause<?>) this).getDatasetHandler());
			dh.setVars(values);
		}
		if (this instanceof SolutionModifierClause) {
			SolutionModifierHandler smh = new SolutionModifierHandler(q);
			smh.addAll(((SolutionModifierClause<?>) this)
					.getSolutionModifierHandler());
			smh.setVars(values);
		}
		if (this instanceof WhereClause) {
			WhereHandler wh = new WhereHandler(q);
			wh.addAll(((WhereClause<?>) this).getWhereHandler());
			wh.setVars(values);
		}

		return q;
	}

	public static Query clone(Query q2) {
		Query retval = new Query();
		new PrologHandler(retval).addAll(new PrologHandler(q2));
		new ConstructHandler(retval).addAll(new ConstructHandler(q2));
		new DatasetHandler(retval).addAll(new DatasetHandler(q2));
		new SolutionModifierHandler(retval).addAll(new SolutionModifierHandler(
				q2));
		new WhereHandler(retval).addAll(new WhereHandler(q2));
		new SelectHandler(retval).addAll(new SelectHandler(q2));
		return retval;
	}

	public static Query rewrite(Query q2, Map<Var, Node> values) {
		new PrologHandler(q2).setVars(values);
		new ConstructHandler(q2).setVars(values);
		new DatasetHandler(q2).setVars(values);
		new SolutionModifierHandler(q2).setVars(values);
		new WhereHandler(q2).setVars(values);
		new SelectHandler(q2).setVars(values);
		return q2;
	}
}