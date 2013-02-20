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
package org.jastadd.tinytemplate.fragment;

import java.io.PrintStream;
import java.io.PrintWriter;

import org.jastadd.tinytemplate.TemplateContext;
import org.jastadd.tinytemplate.Indentation.IndentationFragment;

/**
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public abstract class ReferenceFragment implements IFragment {
	
	private IndentationFragment indentation = null;
	private static final String SYS_NL = System.getProperty("line.separator");
	
	protected void expandWithIndentation(String expansion,
			TemplateContext context, PrintStream out) {
		
		if (indentation == null) {
			out.print(expansion);
		} else {
			String[] lines = expansion.split("\n|\r\n?");
			for (int i = 0; i < lines.length; ++i) {
				if (i != 0) {
					indentation.expand(context, out);
				}
				if ((i+1) < lines.length) {
					out.println(lines[i]);
				} else {
					out.print(lines[i]);
				}
			}
		}
	}

	// TODO remove duplicated code
	protected void expandWithIndentation(String expansion,
			TemplateContext context, PrintWriter out) {
		
		if (indentation == null) {
			out.print(expansion);
		} else {
			String[] lines = expansion.split("\n|\r\n?");
			for (int i = 0; i < lines.length; ++i) {
				if (i != 0) {
					indentation.expand(context, out);
				}
				if ((i+1) < lines.length) {
					out.println(lines[i]);
				} else {
					out.print(lines[i]);
				}
			}
		}
	}

	// TODO remove duplicated code
	protected void expandWithIndentation(String expansion,
			TemplateContext context, StringBuffer buf) {
		
		if (indentation == null) {
			buf.append(expansion);
		} else {
			String[] lines = expansion.split("\n|\r\n?");
			for (int i = 0; i < lines.length; ++i) {
				if (i != 0) {
					indentation.expand(context, buf);
				}
				if ((i+1) < lines.length) {
					buf.append(lines[i]);
					buf.append(SYS_NL);
				} else {
					buf.append(lines[i]);
				}
			}
		}
	}

	/**
	 * Set the indentation for this reference expansion fragment
	 * @param indent
	 */
	public void setIndentation(IndentationFragment indent) {
		indentation = indent;
	}
}
