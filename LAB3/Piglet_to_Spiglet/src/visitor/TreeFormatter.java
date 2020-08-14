//
// Generated by JTB 1.3.2
//
package visitor;

import syntaxtree.*;
import java.util.*;

/**
 * A skeleton output formatter for your language grammar.  Using the
 * add() method along with force(), indent(), and outdent(), you can
 * easily specify how this visitor will format the given syntax tree.
 * See the JTB documentation for more details.
 *
 * Pass your syntax tree to this visitor, and then to the TreeDumper
 * visitor in order to "pretty print" your tree.
 */
public class TreeFormatter extends DepthFirstVisitor {
   private Vector<FormatCommand> cmdQueue = new Vector<FormatCommand>();
   private boolean lineWrap;
   private int wrapWidth;
   private int indentAmt;
   private int curLine = 1;
   private int curColumn = 1;
   private int curIndent = 0;

   /**
    * The default constructor assumes an indentation amount of 3 spaces
    * and no line-wrap.  You may alternately use the other constructor to
    * specify your own indentation amount and line width.
    */
   public TreeFormatter() { this(3, 0); }

   /**
    * This constructor accepts an indent amount and a line width which is
    * used to wrap long lines.  If a token's beginColumn value is greater
    * than the specified wrapWidth, it will be moved to the next line and
    * indented one extra level.  To turn off line-wrapping, specify a
    * wrapWidth of 0.
    *
    * @param   indentAmt   Amount of spaces per indentation level.
    * @param   wrapWidth   Wrap lines longer than wrapWidth.  0 for no wrap.
    */
   public TreeFormatter(int indentAmt, int wrapWidth) {
      this.indentAmt = indentAmt;
      this.wrapWidth = wrapWidth;

      if ( wrapWidth > 0 )
         lineWrap = true;
      else
         lineWrap = false;
   }

   /**
    * Accepts a NodeListInterface object and performs an optional format
    * command between each node in the list (but not after the last node).
    */
   protected void processList(NodeListInterface n) {
      processList(n, null);
   }

   protected void processList(NodeListInterface n, FormatCommand cmd) {
      for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
         e.nextElement().accept(this);
         if ( cmd != null && e.hasMoreElements() )
            cmdQueue.addElement(cmd);
      }
   }

   /**
    * A Force command inserts a line break and indents the next line to
    * the current indentation level.  Use "add(force());".
    */
   protected FormatCommand force() { return force(1); }
   protected FormatCommand force(int i) {
      return new FormatCommand(FormatCommand.FORCE, i);
   }

   /**
    * An Indent command increases the indentation level by one (or a
    * user-specified amount).  Use "add(indent());".
    */
   protected FormatCommand indent() { return indent(1); }
   protected FormatCommand indent(int i) {
      return new FormatCommand(FormatCommand.INDENT, i);
   }

   /**
    * An Outdent command is the reverse of the Indent command: it reduces
    * the indentation level.  Use "add(outdent());".
    */
   protected FormatCommand outdent() { return outdent(1); }
   protected FormatCommand outdent(int i) {
      return new FormatCommand(FormatCommand.OUTDENT, i);
   }

   /**
    * A Space command simply adds one or a user-specified number of
    * spaces between tokens.  Use "add(space());".
    */
   protected FormatCommand space() { return space(1); }
   protected FormatCommand space(int i) {
      return new FormatCommand(FormatCommand.SPACE, i);
   }

   /**
    * Use this method to add FormatCommands to the command queue to be
    * executed when the next token in the tree is visited.
    */
   protected void add(FormatCommand cmd) {
      cmdQueue.addElement(cmd);
   }

   /**
    * Executes the commands waiting in the command queue, then inserts the
    * proper location information into the current NodeToken.
    *
    * If there are any special tokens preceding this token, they will be
    * given the current location information.  The token will follow on
    * the next line, at the proper indentation level.  If this is not the
    * behavior you want from special tokens, feel free to modify this
    * method.
    */
   public void visit(NodeToken n) {
      for ( Enumeration<FormatCommand> e = cmdQueue.elements(); e.hasMoreElements(); ) {
         FormatCommand cmd = e.nextElement();
         switch ( cmd.getCommand() ) {
         case FormatCommand.FORCE :
            curLine += cmd.getNumCommands();
            curColumn = curIndent + 1;
            break;
         case FormatCommand.INDENT :
            curIndent += indentAmt * cmd.getNumCommands();
            break;
         case FormatCommand.OUTDENT :
            if ( curIndent >= indentAmt )
               curIndent -= indentAmt * cmd.getNumCommands();
            break;
         case FormatCommand.SPACE :
            curColumn += cmd.getNumCommands();
            break;
         default :
            throw new TreeFormatterException(
               "Invalid value in command queue.");
         }
      }

      cmdQueue.removeAllElements();

      //
      // Handle all special tokens preceding this NodeToken
      //
      if ( n.numSpecials() > 0 )
         for ( Enumeration<NodeToken> e = n.specialTokens.elements();
               e.hasMoreElements(); ) {
            NodeToken special = e.nextElement();

            //
            // -Place the token.
            // -Move cursor to next line after the special token.
            // -Don't update curColumn--want to keep current indent level.
            //
            placeToken(special, curLine, curColumn);
            curLine = special.endLine + 1;
         }

      placeToken(n, curLine, curColumn);
      curLine = n.endLine;
      curColumn = n.endColumn;
   }

   /**
    * Inserts token location (beginLine, beginColumn, endLine, endColumn)
    * information into the NodeToken.  Takes into account line-wrap.
    * Does not update curLine and curColumn.
    */
   private void placeToken(NodeToken n, int line, int column) {
      int length = n.tokenImage.length();

      //
      // Find beginning of token.  Only line-wrap for single-line tokens
      //
      if ( !lineWrap || n.tokenImage.indexOf('\n') != -1 ||
           column + length <= wrapWidth )
         n.beginColumn = column;
      else {
         ++line;
         column = curIndent + indentAmt + 1;
         n.beginColumn = column;
      }

      n.beginLine = line;

      //
      // Find end of token; don't count \n if it's the last character
      //
      for ( int i = 0; i < length; ++i ) {
         if ( n.tokenImage.charAt(i) == '\n' && i < length - 1 ) {
            ++line;
            column = 1;
         }
         else
            ++column;
      }

      n.endLine = line;
      n.endColumn = column;
   }

   //
   // User-generated visitor methods below
   //

   /**
    * f0 -> "MAIN"
    * f1 -> StmtList()
    * f2 -> "END"
    * f3 -> ( Procedure() )*
    * f4 -> <EOF>
    */
   public void visit(Goal n) {
      n.f0.accept(this);
      n.f1.accept(this);
      n.f2.accept(this);
      if ( n.f3.present() ) {
         processList(n.f3);
      }
      n.f4.accept(this);
   }

   /**
    * f0 -> ( ( Label() )? Stmt() )*
    */
   public void visit(StmtList n) {
      if ( n.f0.present() ) {
         processList(n.f0);
      }
   }

   /**
    * f0 -> Label()
    * f1 -> "["
    * f2 -> IntegerLiteral()
    * f3 -> "]"
    * f4 -> StmtExp()
    */
   public void visit(Procedure n) {
      n.f0.accept(this);
      n.f1.accept(this);
      n.f2.accept(this);
      n.f3.accept(this);
      n.f4.accept(this);
   }

   /**
    * f0 -> NoOpStmt()
    *       | ErrorStmt()
    *       | CJumpStmt()
    *       | JumpStmt()
    *       | HStoreStmt()
    *       | HLoadStmt()
    *       | MoveStmt()
    *       | PrintStmt()
    */
   public void visit(Stmt n) {
      n.f0.accept(this);
   }

   /**
    * f0 -> "NOOP"
    */
   public void visit(NoOpStmt n) {
      n.f0.accept(this);
   }

   /**
    * f0 -> "ERROR"
    */
   public void visit(ErrorStmt n) {
      n.f0.accept(this);
   }

   /**
    * f0 -> "CJUMP"
    * f1 -> Exp()
    * f2 -> Label()
    */
   public void visit(CJumpStmt n) {
      n.f0.accept(this);
      n.f1.accept(this);
      n.f2.accept(this);
   }

   /**
    * f0 -> "JUMP"
    * f1 -> Label()
    */
   public void visit(JumpStmt n) {
      n.f0.accept(this);
      n.f1.accept(this);
   }

   /**
    * f0 -> "HSTORE"
    * f1 -> Exp()
    * f2 -> IntegerLiteral()
    * f3 -> Exp()
    */
   public void visit(HStoreStmt n) {
      n.f0.accept(this);
      n.f1.accept(this);
      n.f2.accept(this);
      n.f3.accept(this);
   }

   /**
    * f0 -> "HLOAD"
    * f1 -> Temp()
    * f2 -> Exp()
    * f3 -> IntegerLiteral()
    */
   public void visit(HLoadStmt n) {
      n.f0.accept(this);
      n.f1.accept(this);
      n.f2.accept(this);
      n.f3.accept(this);
   }

   /**
    * f0 -> "MOVE"
    * f1 -> Temp()
    * f2 -> Exp()
    */
   public void visit(MoveStmt n) {
      n.f0.accept(this);
      n.f1.accept(this);
      n.f2.accept(this);
   }

   /**
    * f0 -> "PRINT"
    * f1 -> Exp()
    */
   public void visit(PrintStmt n) {
      n.f0.accept(this);
      n.f1.accept(this);
   }

   /**
    * f0 -> StmtExp()
    *       | Call()
    *       | HAllocate()
    *       | BinOp()
    *       | Temp()
    *       | IntegerLiteral()
    *       | Label()
    */
   public void visit(Exp n) {
      n.f0.accept(this);
   }

   /**
    * f0 -> "BEGIN"
    * f1 -> StmtList()
    * f2 -> "RETURN"
    * f3 -> Exp()
    * f4 -> "END"
    */
   public void visit(StmtExp n) {
      n.f0.accept(this);
      n.f1.accept(this);
      n.f2.accept(this);
      n.f3.accept(this);
      n.f4.accept(this);
   }

   /**
    * f0 -> "CALL"
    * f1 -> Exp()
    * f2 -> "("
    * f3 -> ( Exp() )*
    * f4 -> ")"
    */
   public void visit(Call n) {
      n.f0.accept(this);
      n.f1.accept(this);
      n.f2.accept(this);
      if ( n.f3.present() ) {
         processList(n.f3);
      }
      n.f4.accept(this);
   }

   /**
    * f0 -> "HALLOCATE"
    * f1 -> Exp()
    */
   public void visit(HAllocate n) {
      n.f0.accept(this);
      n.f1.accept(this);
   }

   /**
    * f0 -> Operator()
    * f1 -> Exp()
    * f2 -> Exp()
    */
   public void visit(BinOp n) {
      n.f0.accept(this);
      n.f1.accept(this);
      n.f2.accept(this);
   }

   /**
    * f0 -> "LT"
    *       | "PLUS"
    *       | "MINUS"
    *       | "TIMES"
    */
   public void visit(Operator n) {
      n.f0.accept(this);
   }

   /**
    * f0 -> "TEMP"
    * f1 -> IntegerLiteral()
    */
   public void visit(Temp n) {
      n.f0.accept(this);
      n.f1.accept(this);
   }

   /**
    * f0 -> <INTEGER_LITERAL>
    */
   public void visit(IntegerLiteral n) {
      n.f0.accept(this);
   }

   /**
    * f0 -> <IDENTIFIER>
    */
   public void visit(Label n) {
      n.f0.accept(this);
   }

}

class FormatCommand {
   public static final int FORCE = 0;
   public static final int INDENT = 1;
   public static final int OUTDENT = 2;
   public static final int SPACE = 3;

   private int command;
   private int numCommands;

   FormatCommand(int command, int numCommands) {
      this.command = command;
      this.numCommands = numCommands;
   }

   public int getCommand()             { return command; }
   public int getNumCommands()         { return numCommands; }
   public void setCommand(int i)       { command = i; }
   public void setNumCommands(int i)   { numCommands = i; }
}

class TreeFormatterException extends RuntimeException {
   TreeFormatterException()         { super(); }
   TreeFormatterException(String s) { super(s); }
}
