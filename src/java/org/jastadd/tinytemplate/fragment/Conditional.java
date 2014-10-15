package org.jastadd.tinytemplate.fragment;

import org.jastadd.tinytemplate.EmptyTemplate;
import org.jastadd.tinytemplate.Template;
import org.jastadd.tinytemplate.TemplateContext;
import org.jastadd.tinytemplate.TemplateParser;
import org.jastadd.tinytemplate.TemplateParser.SyntaxError;

/**
 * A conditional expansion
 * @author Jesper Öqvist <jesper.oqvist@cs.lth.se>
 */
public class Conditional extends AbstractFragment {

	protected String condition;
	protected final Template thenPart;
	protected final Template elsePart;
	protected final boolean negated;
	protected final boolean isAttribute;

	/**
	 * Create a if-then conditional
	 * @param condition
	 * @param thenPart
	 * @throws SyntaxError
	 */
	public Conditional(String condition, Template thenPart) throws SyntaxError {
		this(condition, thenPart, EmptyTemplate.INSTANCE);
	}

	/**
	 * Create an if-then-else conditional
	 * @param cond
	 * @param thenPart
	 * @param elsePart
	 * @throws SyntaxError
	 */
	public Conditional(String cond, Template thenPart, Template elsePart) throws SyntaxError {
		if (cond.startsWith("!")) {
			this.negated = true;
			cond = cond.substring(1).trim();
		} else {
			this.negated = false;
		}
		if (cond.startsWith("#")) {
			this.isAttribute = true;
			this.condition = cond.substring(1);
		} else if (cond.startsWith("$")) {
			this.isAttribute = false;
			this.condition = cond.substring(1);
		} else {
			this.isAttribute = false;
			this.condition = cond;
		}
		if (cond.isEmpty()) {
			throw new SyntaxError("empty if condition");
		}
		if (isAttribute) {
			TemplateParser.acceptAttributeName(-1, this.condition);
		} else {
			TemplateParser.acceptVariableName(-1, this.condition);
		}
		this.thenPart = thenPart;
		this.thenPart.trimTrailingEmptyLine();
		this.elsePart = elsePart;
		this.elsePart.trimTrailingEmptyLine();
	}

	private boolean evalCondition(TemplateContext context) {
		Object value;
		if (isAttribute) {
			value = context.evalAttribute(condition);
		} else {
			value = context.evalVariable(condition);
		}
		boolean result = value.toString().equals("true");
		return negated ? !result : result;
	}

	@Override
	public void expand(TemplateContext context, StringBuilder out) {
		if (evalCondition(context)) {
			thenPart.expand(context, out);
		} else {
			elsePart.expand(context, out);
		}
	}

	@Override
	public boolean isConditional() {
		return true;
	}

	@Override
	public boolean isExpansion() {
		return true;
	}

}
