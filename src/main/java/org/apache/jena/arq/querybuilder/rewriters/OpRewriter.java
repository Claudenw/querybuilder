package org.apache.jena.arq.querybuilder.rewriters;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.query.SortCondition;
import com.hp.hpl.jena.sparql.algebra.Op;
import com.hp.hpl.jena.sparql.algebra.OpVisitor;
import com.hp.hpl.jena.sparql.algebra.Table;
import com.hp.hpl.jena.sparql.algebra.op.OpAssign;
import com.hp.hpl.jena.sparql.algebra.op.OpBGP;
import com.hp.hpl.jena.sparql.algebra.op.OpConditional;
import com.hp.hpl.jena.sparql.algebra.op.OpDatasetNames;
import com.hp.hpl.jena.sparql.algebra.op.OpDiff;
import com.hp.hpl.jena.sparql.algebra.op.OpDisjunction;
import com.hp.hpl.jena.sparql.algebra.op.OpDistinct;
import com.hp.hpl.jena.sparql.algebra.op.OpExt;
import com.hp.hpl.jena.sparql.algebra.op.OpExtend;
import com.hp.hpl.jena.sparql.algebra.op.OpFilter;
import com.hp.hpl.jena.sparql.algebra.op.OpGraph;
import com.hp.hpl.jena.sparql.algebra.op.OpGroup;
import com.hp.hpl.jena.sparql.algebra.op.OpJoin;
import com.hp.hpl.jena.sparql.algebra.op.OpLabel;
import com.hp.hpl.jena.sparql.algebra.op.OpLeftJoin;
import com.hp.hpl.jena.sparql.algebra.op.OpList;
import com.hp.hpl.jena.sparql.algebra.op.OpMinus;
import com.hp.hpl.jena.sparql.algebra.op.OpNull;
import com.hp.hpl.jena.sparql.algebra.op.OpOrder;
import com.hp.hpl.jena.sparql.algebra.op.OpPath;
import com.hp.hpl.jena.sparql.algebra.op.OpProcedure;
import com.hp.hpl.jena.sparql.algebra.op.OpProject;
import com.hp.hpl.jena.sparql.algebra.op.OpPropFunc;
import com.hp.hpl.jena.sparql.algebra.op.OpQuad;
import com.hp.hpl.jena.sparql.algebra.op.OpQuadBlock;
import com.hp.hpl.jena.sparql.algebra.op.OpQuadPattern;
import com.hp.hpl.jena.sparql.algebra.op.OpReduced;
import com.hp.hpl.jena.sparql.algebra.op.OpSequence;
import com.hp.hpl.jena.sparql.algebra.op.OpService;
import com.hp.hpl.jena.sparql.algebra.op.OpSlice;
import com.hp.hpl.jena.sparql.algebra.op.OpTable;
import com.hp.hpl.jena.sparql.algebra.op.OpTopN;
import com.hp.hpl.jena.sparql.algebra.op.OpTriple;
import com.hp.hpl.jena.sparql.algebra.op.OpUnion;
import com.hp.hpl.jena.sparql.algebra.table.TableN;
import com.hp.hpl.jena.sparql.core.BasicPattern;
import com.hp.hpl.jena.sparql.core.Quad;
import com.hp.hpl.jena.sparql.core.QuadPattern;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.core.VarExprList;
import com.hp.hpl.jena.sparql.engine.binding.Binding;
import com.hp.hpl.jena.sparql.expr.ExprAggregator;
import com.hp.hpl.jena.sparql.expr.ExprList;
import com.hp.hpl.jena.sparql.pfunction.PropFuncArg;

class OpRewriter extends AbstractRewriter<Op> implements OpVisitor {

	OpRewriter(Map<Var, Node> values) {
		super(values);
	}

	private Quad rewrite(Quad q) {
		return new Quad(changeNode(q.getGraph()), changeNode(q.getSubject()),
				changeNode(q.getPredicate()), changeNode(q.getObject()));
	}

	private QuadPattern rewrite(QuadPattern pattern) {
		QuadPattern qp = new QuadPattern();
		for (Quad q : pattern.getList()) {
			qp.add(rewrite(q));
		}
		return qp;
	}

	private List<Op> rewriteOpList(List<Op> lst) {
		List<Op> retval = new ArrayList<Op>();
		for (Op o : lst) {
			o.visit(this);
			retval.add(pop());
		}
		return retval;
	}

	private BasicPattern rewrite(BasicPattern pattern) {
		return BasicPattern.wrap(rewrite(pattern.getList()));
	}

	@Override
	public void visit(OpBGP opBGP) {
		push(new OpBGP(rewrite(opBGP.getPattern())));
	}

	@Override
	public void visit(OpQuadPattern quadPattern) {
		push(new OpQuadPattern(changeNode(quadPattern.getGraphNode()),
				rewrite(quadPattern.getBasicPattern())));
	}

	@Override
	public void visit(OpQuadBlock quadBlock) {
		push(new OpQuadBlock(rewrite(quadBlock.getPattern())));
	}

	@Override
	public void visit(OpTriple opTriple) {
		push(new OpTriple(rewrite(opTriple.getTriple())));
	}

	@Override
	public void visit(OpQuad opQuad) {
		push(new OpQuad(rewrite(opQuad.getQuad())));

	}

	@Override
	public void visit(OpPath opPath) {
		push(new OpPath(rewrite(opPath.getTriplePath())));
	}

	@Override
	public void visit(OpTable opTable) {
		Table tbl = opTable.getTable();
		boolean process = false;
		for (Var v : tbl.getVars()) {
			process = process | values.keySet().contains(v);
		}
		if (!process) {
			push(opTable);
		} else {
			TableN retTbl = new TableN(tbl.getVars());
			Iterator<Binding> iter = tbl.rows();
			while (iter.hasNext()) {
				retTbl.addBinding(rewrite(iter.next()));
			}
			push(OpTable.create(retTbl));
		}
	}

	@Override
	public void visit(OpNull opNull) {
		push(opNull);
	}

	@Override
	public void visit(OpProcedure opProc) {
		opProc.getSubOp().visit(this);
		Op op = pop();
		ExprList args = new ExprRewriter(values).rewrite(opProc.getArgs());
		Node procId = changeNode(opProc.getProcId());
		push(new OpProcedure(procId, args, op));
	}

	private PropFuncArg rewrite(PropFuncArg arg) {
		if (arg.isList()) {
			List<Node> lst = changeNodes(arg.getArgList());
			return new PropFuncArg(lst, null);
		}
		return new PropFuncArg(changeNode(arg.getArg()));
	}

	@Override
	public void visit(OpPropFunc opPropFunc) {
		opPropFunc.getSubOp().visit(this);
		Op op = pop();
		Node uri = changeNode(opPropFunc.getProperty());
		PropFuncArg args1 = rewrite(opPropFunc.getSubjectArgs());
		PropFuncArg args2 = rewrite(opPropFunc.getObjectArgs());
		push(new OpPropFunc(uri, args1, args2, op));
	}

	@Override
	public void visit(OpFilter opFilter) {
		opFilter.getSubOp().visit(this);
		push(OpFilter.filter(
				new ExprRewriter(values).rewrite(opFilter.getExprs()), pop()));
	}

	@Override
	public void visit(OpGraph opGraph) {
		opGraph.getSubOp().visit(this);
		push(new OpGraph(changeNode(opGraph.getNode()), pop()));
	}

	@Override
	public void visit(OpService opService) {
		opService.getSubOp().visit(this);
		push(new OpService(changeNode(opService.getService()), pop(),
				opService.getSilent()));
	}

	@Override
	public void visit(OpDatasetNames dsNames) {
		push(new OpDatasetNames(changeNode(dsNames.getGraphNode())));
	}

	@Override
	public void visit(OpLabel opLabel) {
		if (opLabel.hasSubOp()) {
			opLabel.getSubOp().visit(this);
			push(OpLabel.create(opLabel.getObject(), pop()));
		} else {
			push(opLabel);
		}
	}

	@Override
	public void visit(OpAssign opAssign) {
		opAssign.getSubOp().visit(this);
		push(OpAssign.assign(pop(), rewrite(opAssign.getVarExprList())));
	}

	@Override
	public void visit(OpExtend opExtend) {
		opExtend.getSubOp().visit(this);
		push(OpExtend.extend(pop(), rewrite(opExtend.getVarExprList())));
	}

	@Override
	public void visit(OpJoin opJoin) {
		opJoin.getRight().visit(this);
		opJoin.getLeft().visit(this);
		push(OpJoin.create(pop(), pop()));
	}

	@Override
	public void visit(OpLeftJoin opLeftJoin) {
		opLeftJoin.getRight().visit(this);
		opLeftJoin.getLeft().visit(this);
		push(OpLeftJoin.create(pop(), pop(),
				new ExprRewriter(values).rewrite(opLeftJoin.getExprs())));
	}

	@Override
	public void visit(OpUnion opUnion) {
		opUnion.getRight().visit(this);
		opUnion.getLeft().visit(this);
		push(OpUnion.create(pop(), pop()));
	}

	@Override
	public void visit(OpDiff opDiff) {
		opDiff.getRight().visit(this);
		opDiff.getLeft().visit(this);
		push(OpDiff.create(pop(), pop()));
	}

	@Override
	public void visit(OpMinus opMinus) {
		opMinus.getRight().visit(this);
		opMinus.getLeft().visit(this);
		push(OpMinus.create(pop(), pop()));
	}

	@Override
	public void visit(OpConditional opCondition) {
		opCondition.getRight().visit(this);
		opCondition.getLeft().visit(this);
		push(new OpConditional(pop(), pop()));
	}

	@Override
	public void visit(OpSequence opSequence) {
		List<Op> lst = rewriteOpList(opSequence.getElements());
		push(opSequence.copy(lst));
	}

	@Override
	public void visit(OpDisjunction opDisjunction) {
		List<Op> lst = rewriteOpList(opDisjunction.getElements());
		push(opDisjunction.copy(lst));
	}

	@Override
	public void visit(OpExt opExt) {
		push(opExt);
	}

	@Override
	public void visit(OpList opList) {
		opList.getSubOp().visit(this);
		push(new OpList(pop()));
	}

	@Override
	public void visit(OpOrder opOrder) {
		List<SortCondition> lst = new ExprRewriter(values)
				.rewriteSortConditionList(opOrder.getConditions());
		opOrder.getSubOp().visit(this);
		push(new OpOrder(pop(), lst));
	}

	@Override
	public void visit(OpProject opProject) {
		opProject.getSubOp().visit(this);
		List<Var> vars = new ArrayList<Var>();
		for (Var v : opProject.getVars()) {
			Node n = changeNode(v);
			vars.add(Var.alloc(n));
		}
		push(new OpProject(pop(), vars));
	}

	@Override
	public void visit(OpReduced opReduced) {
		opReduced.getSubOp().visit(this);
		push(opReduced.copy(pop()));
	}

	@Override
	public void visit(OpDistinct opDistinct) {
		opDistinct.getSubOp().visit(this);
		push(opDistinct.copy(pop()));
	}

	@Override
	public void visit(OpSlice opSlice) {
		opSlice.getSubOp().visit(this);
		push(opSlice.copy(pop()));
	}

	@Override
	public void visit(OpGroup opGroup) {
		opGroup.getSubOp().visit(this);
		ExprRewriter expRewriter = new ExprRewriter(values);
		VarExprList groupVars = rewrite(opGroup.getGroupVars());
		List<ExprAggregator> aggregators = new ArrayList<ExprAggregator>();
		for (ExprAggregator ea : opGroup.getAggregators()) {
			ea.visit(expRewriter);
			aggregators.add((ExprAggregator) expRewriter.pop());
		}
		push(new OpGroup(pop(), groupVars, aggregators));
	}

	@Override
	public void visit(OpTopN opTop) {
		opTop.getSubOp().visit(this);
		ExprRewriter expRewriter = new ExprRewriter(values);
		expRewriter.rewriteSortConditionList(opTop.getConditions());
		push(new OpTopN(pop(), opTop.getLimit(),
				expRewriter.rewriteSortConditionList(opTop.getConditions())));
	}
}