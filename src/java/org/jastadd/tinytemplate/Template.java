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
import org.jastadd.tinytemplate.fragment.IFragment;
import org.jastadd.tinytemplate.fragment.NestedIndentationFragment;

/**
 * Template
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class Template {
	
	private List<List<IFragment>> lines = new ArrayList<List<IFragment>>();
	
	{
		// lines should never be empty
		lines.add(new ArrayList<IFragment>());
	}

	/**
	 * Expand the template to a PrintStream
	 * @param context 
	 * @param out
	 */
	public void expand(TemplateContext context, PrintStream out) {
		StringBuilder buf = new StringBuilder();
		for (List<IFragment> line: lines) {
			expandLine(context, line, buf);
			out.print(buf.toString());
		}
	}

	/**
	 * Expand the template to a PrintWriter
	 * @param context 
	 * @param out
	 */
	public void expand(TemplateContext context, PrintWriter out) {
		StringBuilder buf = new StringBuilder();
		for (List<IFragment> line: lines) {
			expandLine(context, line, buf);
			out.print(buf.toString());
		}
	}

	/**
	 * Expand the template to a StringBuffer
	 * @param context 
	 * @param out
	 */
	public void expand(TemplateContext context, StringBuffer out) {
		StringBuilder buf = new StringBuilder();
		for (List<IFragment> line: lines) {
			expandLine(context, line, buf);
			out.append(buf.toString());
		}
	}

	/**
	 * Expand the template to a StringBuilder
	 * @param context 
	 * @param out
	 */
	public void expand(TemplateContext context, StringBuilder out) {
		StringBuilder buf = new StringBuilder();
		for (List<IFragment> line: lines) {
			expandLine(context, line, buf);
			out.append(buf.toString());
		}
	}
	
	private void expandLine(TemplateContext context, List<IFragment> line, StringBuilder buf) {
		buf.setLength(0);
		boolean expanded = false;
		for (IFragment fragment : line) {
			expanded |= fragment.isExpansion();
			fragment.expand(context, buf);
		}
		if (expanded && isEmptyLine(buf)) {
			buf.setLength(0);
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
	 * Add the indentation of the last line to a fragment
	 * @param fragment
	 */
	public void addIndentation(NestedIndentationFragment fragment) {
		List<IFragment> lastLine = lines.get(lines.size()-1);
		if (!lastLine.isEmpty() && lastLine.get(0).isIndentation()) {
			fragment.setIndentation((IndentationFragment) lastLine.get(0));
		}
		
	}
	
	/**
	 * Add a fragment to the last line
	 * @param fragment
	 */
	public void addFragment(IFragment fragment) {
		lines.get(lines.size()-1).add(fragment);
		if (fragment.isNewline()) {
			lines.add(new ArrayList<IFragment>());
		}
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
		for (IFragment fragment: lines.get(0)) {
			if (!fragment.isWhitespace()) {
				return;
			}
		}
		lines.remove(0);
		if (lines.isEmpty()) {
			lines.add(new ArrayList<IFragment>());
		}
	}
	
	/**
	 * Trim leading and trailing whitespace around conditionals surrounded
	 * by whitespace on their line.
	 */
	private void trimConditionalWhitespace() {
		for (int i = 0; i < lines.size(); ++i) {
			boolean trimmable = true;
			boolean hasCond = false;
			for (IFragment fragment: lines.get(i)) {
				if ((!fragment.isWhitespace() && !fragment.isConditional())
						|| fragment.isConditional() && hasCond) {
					trimmable = false;
					break;
				}
				hasCond |= fragment.isConditional();
			}
			List<IFragment> line = trimLine(lines.get(i), trimmable && hasCond);
			lines.set(i, line);
		}
	}

	private List<IFragment> trimLine(List<IFragment> line, boolean trimmable) {
		List<IFragment> tmp = new ArrayList<IFragment>(lines.size());
		for (IFragment frag: line) {
			if (!trimmable || !frag.isWhitespace()) {
				tmp.add(frag);
			}
		}
		return tmp;
	}
}
