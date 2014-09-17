package org.apache.jena.arq.querybuilder.rewriters;

import java.util.Map;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.expr.NodeValue;
import com.hp.hpl.jena.sparql.expr.nodevalue.NodeValueBoolean;
import com.hp.hpl.jena.sparql.expr.nodevalue.NodeValueDT;
import com.hp.hpl.jena.sparql.expr.nodevalue.NodeValueDecimal;
import com.hp.hpl.jena.sparql.expr.nodevalue.NodeValueDouble;
import com.hp.hpl.jena.sparql.expr.nodevalue.NodeValueDuration;
import com.hp.hpl.jena.sparql.expr.nodevalue.NodeValueFloat;
import com.hp.hpl.jena.sparql.expr.nodevalue.NodeValueInteger;
import com.hp.hpl.jena.sparql.expr.nodevalue.NodeValueNode;
import com.hp.hpl.jena.sparql.expr.nodevalue.NodeValueString;
import com.hp.hpl.jena.sparql.expr.nodevalue.NodeValueVisitor;

class NodeValueRewriter extends AbstractRewriter<NodeValue> implements
		NodeValueVisitor {

	public NodeValueRewriter(Map<Var, Node> values) {
		super(values);
	}

	@Override
	public void visit(NodeValueBoolean nv) {
		push(new NodeValueBoolean(nv.getBoolean(), changeNode(nv.getNode())));
	}

	@Override
	public void visit(NodeValueDecimal nv) {
		push(new NodeValueDecimal(nv.getDecimal(), changeNode(nv.getNode())));
	}

	@Override
	public void visit(NodeValueDouble nv) {
		push(new NodeValueDouble(nv.getDouble(), changeNode(nv.getNode())));
	}

	@Override
	public void visit(NodeValueFloat nv) {
		push(new NodeValueFloat(nv.getFloat(), changeNode(nv.getNode())));
	}

	@Override
	public void visit(NodeValueInteger nv) {
		push(new NodeValueInteger(nv.getInteger(), changeNode(nv.getNode())));
	}

	@Override
	public void visit(NodeValueNode nv) {
		push(new NodeValueNode(changeNode(nv.getNode())));
	}

	@Override
	public void visit(NodeValueString nv) {
		push(new NodeValueString(nv.getString(), changeNode(nv.getNode())));
	}

	@Override
	public void visit(NodeValueDT nv) {
		push(new NodeValueDT(nv.getDateTime().toXMLFormat(),
				changeNode(nv.getNode())));
	}

	@Override
	public void visit(NodeValueDuration nodeValueDuration) {
		push(new NodeValueDuration(nodeValueDuration.getDuration(),
				changeNode(nodeValueDuration.getNode())));
	}
}