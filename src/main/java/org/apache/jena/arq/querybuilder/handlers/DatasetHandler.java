package org.apache.jena.arq.querybuilder.handlers;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.sparql.core.Var;

public class DatasetHandler implements Handler {

	private final Query query;

	public DatasetHandler(Query query) {
		this.query = query;
	}

	public void addAll(DatasetHandler datasetHandler) {
		from(datasetHandler.query.getGraphURIs());
		fromNamed(datasetHandler.query.getNamedGraphURIs());
	}

	public void fromNamed(String graphName) {
		query.addNamedGraphURI(graphName);
	}

	public void fromNamed(Collection<String> graphNames) {
		for (String uri : graphNames) {
			query.addNamedGraphURI(uri);
		}
	}

	public void from(String graphName) {
		query.addGraphURI(graphName);
	}

	public void from(Collection<String> graphNames) {
		for (String uri : graphNames) {
			query.addGraphURI(uri);
		}
	}

	private void setVars(Map<Var, Node> values, String fieldName)
	{
		if (values.isEmpty()) {
			return;
		}
		try {
			Field f = Query.class.getDeclaredField( fieldName );
			f.setAccessible(true);
			List<String> orig = (List<String>) f.get( query );
			List<String> lst = null;
			if (orig != null)
			{
				lst = new ArrayList<String>();
				for (String s : orig)
				{
					Node n = null;
					if (s.startsWith( "?"))
					{
						Var v = Var.alloc( s.substring(1));
						n = values.get( v );
					}
					lst.add( n==null?s:n.toString());
				}
				f.set( query, lst);
			}
		} catch (NoSuchFieldException e) {
			throw new IllegalStateException( e.getMessage(), e );
		} catch (SecurityException e) {
			throw new IllegalStateException( e.getMessage(), e );
		} catch (IllegalAccessException e) {
			throw new IllegalStateException( e.getMessage(), e );
		}
	}
	@Override
	public void setVars(Map<Var, Node> values) {
		setVars( values, "namedGraphURIs");
		setVars( values, "graphURIs");
	}

}
