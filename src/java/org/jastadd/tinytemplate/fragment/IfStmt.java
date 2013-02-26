package org.jastadd.tinytemplate.fragment;

import java.io.PrintStream;
import java.io.PrintWriter;

import org.jastadd.tinytemplate.Template;
import org.jastadd.tinytemplate.TemplateContext;

/**
 * A conditional expansion
 * @author Jesper Öqvist <jesper.oqvist@cs.lth.se>
 */
public class IfStmt extends AbstractFragment {
	
	private String condition;
	private final Template thenPart;
	private Template elsePart = null;

	/**
	 * Create a if-then conditional
	 * @param condition
	 * @param thenPart 
	 */
	public IfStmt(String condition, Template thenPart) {
		this.condition = condition;
		this.thenPart = thenPart;
		this.elsePart = null;
	}
	
	/**
	 * Create an if-then-else conditional
	 * @param condition
	 * @param thenPart 
	 * @param elsePart 
	 */
	public IfStmt(String condition, Template thenPart, Template elsePart) {
		this.condition = condition;
		this.thenPart = thenPart;
		this.elsePart = elsePart;
	}

	private boolean evalCondition(TemplateContext context) {
		return context.evalVariable(condition).equals("true");
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

}
