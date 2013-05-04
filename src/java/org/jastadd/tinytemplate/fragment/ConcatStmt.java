package org.jastadd.tinytemplate.fragment;

import java.io.PrintStream;

import org.jastadd.tinytemplate.TemplateContext;
import org.jastadd.tinytemplate.TemplateExpansionWarning;
import org.jastadd.tinytemplate.TemplateParser.SyntaxError;

/**
 * A concatenation statement
 * @author Niklas Fors <niklas.fors@cs.lth.se>
 */
public class ConcatStmt extends NestedIndentationFragment {
	private String iterable;
	private String sep;
	private boolean isAttribute;

	public ConcatStmt(String iterable) throws SyntaxError {
		this(iterable, null);
	}
	
	public ConcatStmt(String iterable, String sep) throws SyntaxError {
		if (iterable.startsWith("#")) {
			this.iterable = iterable.substring(1);
			isAttribute = true;
		} else if (iterable.startsWith("$")) {
			this.iterable = iterable.substring(1);
			isAttribute = false;
		}
		this.sep = sep;
	}

	@Override
	public void expand(TemplateContext context, StringBuilder out) {
		if (isAttribute) {
			expandAttribute(context, out);
		} else {
			throw new TemplateExpansionWarning("Variable " + iterable + " is not iterable");
		}
	}

	@SuppressWarnings("rawtypes")
	private void expandAttribute(TemplateContext context, StringBuilder out) {
		Object itr = context.evalAttributeToObject(iterable);
		if (itr instanceof Iterable) {
			StringBuilder sb = new StringBuilder();
			boolean first = true;
			for (Object o: (Iterable) itr) {
				if (sep != null && !first) {
					sb.append(sep);					
				}
				first = false;
				sb.append(o.toString());
			}
			expandWithIndentation(sb.toString(), context, out);
		} else {
			throw new TemplateExpansionWarning("Attribute " + iterable + " is not iterable");
		}
	}

	@Override
	public boolean isConditional() {
		return false;
	}

	@Override
	public boolean isExpansion() {
		return true;
	}

	@Override
	public void printAspectCode(PrintStream out) {
		out.println("    {");
		if (sep != null) {
			out.println("      boolean first = true;");
		}
		out.print("      for (PrettyPrintable p: ");
		if (isAttribute) {
			out.print(iterable + "()");
		} else {
			out.print("get" + iterable + "()");
		}
		out.println(") {");
		if (sep != null) {
			out.println("        if (!first) {");
			out.println("          out.println(\"" + sep + "\");");
			out.println("        }");
			out.println("        first = false;");
		}
		out.println("        out.print(p);");
		out.println("      }");
		out.println("    }");
	}
}
