
import piglet.syntaxtree.*;
import piglet.visitor.*;

public class PigletFormatter extends TreeFormatter {

    public PigletFormatter() {
	super(2, 0);
    }
    
    protected void processList(NodeListInterface n) {
	processList(n, force());
    }

    public void visit(NodeToken n) {
	super.visit(n);
	    // add a space after each token
	add(space());
    }

    public void visit(Goal n) {
	n.nodeToken.accept(this);
	add(indent());
	add(force());
	n.stmtList.accept(this);
	add(outdent());
	add(force());
	n.nodeToken1.accept(this);
	n.nodeListOptional.accept(this);
	add(force());
	n.nodeToken2.accept(this);
    }

    public void visit(Procedure n) {
	add(force());
	add(force());
	n.label.accept(this);
	n.nodeToken.accept(this);
	n.integerLiteral.accept(this);
	n.nodeToken1.accept(this);
	n.stmtExp.accept(this);
    }

    public void visit(StmtExp n) {
	add(indent());
	add(force());
	n.nodeToken.accept(this);
	if(n.stmtList.nodeListOptional.present()) {
	    add(indent());
	    add(force());
	    n.stmtList.accept(this);
	    add(outdent());
	}
	add(force());
	n.nodeToken1.accept(this);
	n.exp.accept(this);
	add(force());
	n.nodeToken2.accept(this);
	add(outdent());
    }

    public void visit(Call n) {
      n.nodeToken.accept(this);
      n.exp.accept(this);
      n.nodeToken1.accept(this);
      if ( n.nodeListOptional.present() ) {
	  processList(n.nodeListOptional, null);
      }
      n.nodeToken2.accept(this);
   }

}