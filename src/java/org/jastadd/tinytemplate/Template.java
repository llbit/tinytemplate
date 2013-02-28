/* Copyright (c) 2013, Jesper Öqvist <jesper@cs.lth.se>
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
package org.jastadd.tinytemplate;

import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.jastadd.tinytemplate.Indentation.IndentationFragment;
import org.jastadd.tinytemplate.fragment.AttributeReference;
import org.jastadd.tinytemplate.fragment.IFragment;
import org.jastadd.tinytemplate.fragment.NewlineFragment;
import org.jastadd.tinytemplate.fragment.NestedIndentationFragment;
import org.jastadd.tinytemplate.fragment.StringFragment;
import org.jastadd.tinytemplate.fragment.VariableReference;


/**
 * Template
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class Template {
	
	private List<IFragment> fragments = new ArrayList<IFragment>();

	/**
	 * Expand the template to a PrintStream
	 * @param template 
	 * @param out
	 */
	public void expand(TemplateContext template, PrintStream out) {
		StringBuilder buf = new StringBuilder();
		boolean expanded = false;
		for (IFragment fragment : fragments) {
			expanded |= fragment.isExpansion();
			fragment.expand(template, buf);
			
			if (fragment.isNewline()) {
				if (!(expanded && isEmptyLine(buf))) {
					out.print(buf.toString());
				}
				buf.setLength(0);
				expanded = false;
			}
		}
		if (!(expanded && isEmptyLine(buf))) {
			out.print(buf.toString());
		}
	}

	/**
	 * Expand the template to a PrintWriter
	 * @param template 
	 * @param out
	 */
	public void expand(TemplateContext template, PrintWriter out) {
		// TODO remove duplicated code
		StringBuilder buf = new StringBuilder();
		boolean expanded = false;
		for (IFragment fragment : fragments) {
			expanded |= fragment.isExpansion();
			fragment.expand(template, buf);
			
			if (fragment.isNewline()) {
				if (!(expanded && isEmptyLine(buf))) {
					out.print(buf.toString());
				}
				buf.setLength(0);
				expanded = false;
			}
		}
		if (!(expanded && isEmptyLine(buf))) {
			out.print(buf.toString());
		}
	}

	/**
	 * Expand the template to a StringBuffer
	 * @param template 
	 * @param out
	 */
	public void expand(TemplateContext template, StringBuffer out) {
		// TODO remove duplicated code
		StringBuilder buf = new StringBuilder();
		boolean expanded = false;
		for (IFragment fragment : fragments) {
			expanded |= fragment.isExpansion();
			fragment.expand(template, buf);
			
			if (fragment.isNewline()) {
				if (!(expanded && isEmptyLine(buf))) {
					out.append(buf.toString());
				}
				buf.setLength(0);
				expanded = false;
			}
		}
		if (!(expanded && isEmptyLine(buf))) {
			out.append(buf.toString());
		}
	}

	/**
	 * Expand the template to a StringBuilder
	 * @param template 
	 * @param out
	 */
	public void expand(TemplateContext template, StringBuilder out) {
		// TODO remove duplicated code
		StringBuilder buf = new StringBuilder();
		boolean expanded = false;
		for (IFragment fragment : fragments) {
			expanded |= fragment.isExpansion();
			fragment.expand(template, buf);
			
			if (fragment.isNewline()) {
				if (!(expanded && isEmptyLine(buf))) {
					out.append(buf.toString());
				}
				buf.setLength(0);
				expanded = false;
			}
		}
		if (!(expanded && isEmptyLine(buf))) {
			out.append(buf.toString());
		}
	}

	private boolean isEmptyLine(StringBuilder buf) {
		for (int i = 0; i < buf.length(); ++i) {
			if (!Character.isWhitespace(buf.charAt(i)))
				return false;
		}
		return true;
	}

	/**
	 * Load templates from file
	 * @param in
	 */
	public static void load(InputStream in) {
	}

	/**
	 * Adds a newline to the template
	 */
	public void addNewline() {
		fragments.add(NewlineFragment.INSTANCE);
	}

	/**
	 * Adds a variable reference to the template
	 * @param variable Variable name
	 */
	public void addVariableRef(String variable) {
		VariableReference ref = new VariableReference(variable);
		addIndentation(ref);
		fragments.add(ref);
	}

	/**
	 * Adds an attribute reference to the template
	 * @param attribute Attribute name
	 */
	public void addAttributeRef(String attribute) {
		AttributeReference ref = new AttributeReference(attribute);
		addIndentation(ref);
		fragments.add(ref);
	}

	/**
	 * Add indentation to an indented fragment
	 * @param ref
	 */
	public void addIndentation(NestedIndentationFragment ref) {
		// find previous indentation
		int ind;
		for (ind = fragments.size()-1; ind >= 0; ind -= 1) {
			if (fragments.get(ind) instanceof IndentationFragment) {
				break;
			}
		}
		if (ind >= 0) {
			ref.setIndentation((IndentationFragment) fragments.get(ind));
		}
		
	}
	
	/**
	 * Add a fragment
	 * @param fragment
	 */
	public void addFragment(IFragment fragment) {
		fragments.add(fragment);
	}

	/**
	 * Adds a string to the template
	 * @param str String literal
	 */
	public void addString(String str) {
		fragments.add(new StringFragment(str));
	}

	/**
	 * Adds an indentation fragment
	 * @param level Indentation level
	 */
	public void addIndentation(int level) {
		fragments.add(Indentation.getFragment(level));
	}
	
	/**
	 * Trims first line from the template if it contains only whitespace.
	 * Trims leading and trailing whitespace surrounding conditionals that
	 * are alone on their line.
	 */
	public void trim() {
		trimLeadingNewline();
		trimConditionalWhitespace();
	}

	/**
	 * Trim the first line from the template if it contains only whitespace
	 */
	private void trimLeadingNewline() {
		int numToStrip = 0;
		for (IFragment fragment: fragments) {
			if (!fragment.isWhitespace()) {
				return;
			}
			numToStrip += 1;
			if (fragment.isNewline()) {
				break;
			}
		}
		for (int i = 0; i < numToStrip; ++i) {
			fragments.remove(0);
		}
	}
	
	/**
	 * Trim leading and trailing whitespace around conditionals surrounded
	 * by whitespace on their line.
	 */
	private void trimConditionalWhitespace() {
		List<IFragment> tmp = new ArrayList<IFragment>(fragments.size());
		List<IFragment> line = new ArrayList<IFragment>();
		boolean trimmable = true;
		boolean hasCond = false;
		for (IFragment fragment: fragments) {
			line.add(fragment);
			if (!fragment.isNewline()) {
				
				if ((!fragment.isWhitespace() && !fragment.isConditional())
						|| fragment.isConditional() && hasCond) {
					
					trimmable = false;
				}
				hasCond |= fragment.isConditional();
			} else {
				addLine(tmp, line, trimmable && hasCond);
				line.clear();
				trimmable = true;
				hasCond = false;
			}
		}
		addLine(tmp, line, trimmable && hasCond);
		fragments = tmp;
	}

	private void addLine(List<IFragment> tmp, List<IFragment> line,
			boolean trimmable) {
		
		for (IFragment frag: line) {
			if (!trimmable || !frag.isWhitespace()) {
				tmp.add(frag);
			}
		}
	}
}
