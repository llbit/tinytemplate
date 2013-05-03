/* Copyright (c) 2013, Jesper Öqvist <jesper.oqvist@cs.lth.se>
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
package org.jastadd.util;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Stack;

/**
 * @author Jesper Öqvist <jesper.oqvist@cs.lth.se>
 */
public class PrettyPrinter {
	private final String indentation;
	private final java.util.List<String> ind = new ArrayList<String>(32);
	{
		ind.add("");
	}
	private final Stack<Integer> indentStack = new Stack<Integer>();
	{
		indentStack.push(0);
	}
	private int currentIndent = 0;

	private PrintStream out = System.out;
	private boolean newline = false;

	/**
	 * @param ind
	 */
	public PrettyPrinter(String ind) {
		this.indentation = ind;
	}

	/**
	 * @param target
	 */
	public void setTarget(PrintStream target) {
		out = target;
	}

	/**
 	 * @param level The level of indentation
 	 * @return The indentation string for the given indentation level
 	 */
	public String getIndentation(int level) {
		while (ind.size() < (level+1)) {
			ind.add(ind.get(ind.size()-1) + indentation);
		}
		return ind.get(level);
	}


	/**
	 * @param str
	 */
	public void print(String str) {
		indentNewline();
		out.print(str);
	}

	/**
	 *
	 */
	public void println() {
		out.println();
		newline = true;
	}

	/**
	 * @param node
	 */
	public void print(PrettyPrintable node) {
		pushIndentation();
		node.prettyPrint(this);
		popIndentation();
	}

	/**
	 * @param level
	 */
	public void indent(int level) {
		currentIndent = level;
		out.print(getIndentation(level));
	}

	private void pushIndentation() {
		indentStack.push(currentIndent + indentStack.peek());
	}

	private void popIndentation() {
		currentIndent = indentStack.pop();
		currentIndent -= indentStack.peek();
	}

	private void indentNewline() {
		if (newline) {
			out.println(getIndentation(indentStack.peek()));
			newline = false;
		}
	}
}
