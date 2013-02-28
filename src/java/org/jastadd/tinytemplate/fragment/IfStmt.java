package org.jastadd.tinytemplate.fragment;

import java.io.PrintStream;
import java.io.PrintWriter;

import org.jastadd.tinytemplate.Template;
import org.jastadd.tinytemplate.TemplateContext;
import org.jastadd.tinytemplate.TemplateParser.SyntaxError;

/**
 * A conditional expansion
 * @author Jesper Öqvist <jesper.oqvist@cs.lth.se>
 */
public class IfStmt extends AbstractFragment {
	
	private String condition;
	private final Template thenPart;
	private Template elsePart = null;
	private final boolean negated;

	/**
	 * Create a if-then conditional
	 * @param condition
	 * @param thenPart 
	 * @throws SyntaxError 
	 */
	public IfStmt(String condition, Template thenPart) throws SyntaxError {
		this(condition, thenPart, null);
	}
	
	/**
	 * Create an if-then-else conditional
	 * @param condition
	 * @param thenPart 
	 * @param elsePart 
	 * @throws SyntaxError 
	 */
	public IfStmt(String condition, Template thenPart, Template elsePart) throws SyntaxError {
		if (condition.startsWith("!")) {
			this.negated = true;
			this.condition = condition.substring(1);
		} else {
			this.negated = false;
			this.condition = condition;
		}
		if (condition.isEmpty())
			throw new SyntaxError("empty if condition");
		this.thenPart = thenPart;
		this.elsePart = elsePart;
	}

	private boolean evalCondition(TemplateContext context) {
		boolean result = context.evalVariable(condition).equals("true");
		return negated ? !result : result;
	}

	@Override
	public void expand(TemplateContext context, PrintStream out) {
		if (evalCondition(context)) {
			thenPart.expand(context, out);
		} else if (elsePart != null) {
			elsePart.expand(context, out);
		}
	}

	@Override
	public void expand(TemplateContext context, PrintWriter out) {
		if (evalCondition(context)) {
			thenPart.expand(context, out);
		} else if (elsePart != null) {
			elsePart.expand(context, out);
		}
	}

	@Override
	public void expand(TemplateContext context, StringBuffer buf) {
		if (evalCondition(context)) {
			thenPart.expand(context, buf);
		} else if (elsePart != null) {
			elsePart.expand(context, buf);
		}
	}

	@Override
	public boolean isConditional() {
		return true;
	}
}
