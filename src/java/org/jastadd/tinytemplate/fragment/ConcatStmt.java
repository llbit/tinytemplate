/* Copyright (c) 2013, Niklas Fors <niklas.fors@cs.lth.se>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the <organization> nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
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
	private final String sep;
	private boolean isAttribute;

	/**
	 * @param iterable
	 * @throws SyntaxError
	 */
	public ConcatStmt(String iterable) throws SyntaxError {
		this(iterable, null);
	}

	/**
	 * @param iterable
	 * @param sep
	 * @throws SyntaxError
	 */
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

	private void expandAttribute(TemplateContext context, StringBuilder out) {
		Object value = context.evalAttribute(iterable);
		if (value instanceof Iterable) {
			Iterable<?> itr = (Iterable<?>) value;
			StringBuilder sb = new StringBuilder();
			boolean first = true;
			for (Object o: itr) {
				if (sep != null && !first) {
					sb.append(sep);
				}
				first = false;
				sb.append(String.valueOf(o));
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
			StringBuilder buf = new StringBuilder();
			for (int i = 0; i < sep.length()-1; ++i) {
				if (sep.charAt(i) == '\\' && sep.charAt(i+1) == 'n') {
					i += 1;
					if (buf.length() > 0) {
						out.println("          out.print(\"" + buf.toString() + "\");");
					}
					out.println("          out.println();");
					buf.setLength(0);
				} else {
					buf.append(sep.charAt(i));
				}
			}
			if (buf.length() > 0) {
				out.println("          out.print(\"" + buf.toString() + "\");");
			}
			out.println("        }");
			out.println("        first = false;");
		}
		out.println("        out.print(p);");
		out.println("      }");
		out.println("    }");
	}
}
