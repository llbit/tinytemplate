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

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * Template context is needed to expand a template. The template context
 * is responsible for evaluating variables and attributes.
 * @author Jesper Öqvist <jesper.oqvist@cs.lth.se>
 */
public abstract class TemplateContext {
	/**
 	 * Lookup variable on the variable stack and return the variable expansion
 	 * if it was found.
	 * @param varName
	 * @return The variable value, or the string "&lt;unbound variable varName&gt;"
	 * if the variable was not bound
	 */
	abstract public String evalVariable(String varName);
	
	/**
	 * Evaluate an attribute
	 * @param attribute
	 * @return The string value returned from the attribute
	 */
	abstract public String evalAttribute(String attribute);

	/**
 	 * @param levels Number of indentation levels
 	 * @return The cumulative indentation corresponding to the given
 	 * indentation level
 	 */
	abstract public String evalIndentation(int levels);
	
	/**
	 * Expand a template
	 * @param templateName
	 * @return The template expansion
	 */
	final public String expand(String templateName) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		expand(templateName, new PrintStream(out));
		return out.toString();
	}
	
	/**
	 * Expand a template
	 * @param templateName
	 * @param out
	 */
	final public void expand(String templateName, OutputStream out) {
		PrintStream ps = new PrintStream(new BufferedOutputStream(out));
		expand(templateName, ps);
	}
	
	/**
	 * Expand a template
	 * @param templateName
	 * @param out
	 * @return <code>true</code> if the template was expanded,
	 * <code>false</code> if no such template exists
	 */
	final public boolean expand(String templateName, PrintStream out) {
		return expand(this, templateName, out);
	}
	
	/**
	 * Expand a template
	 * @param templateName
	 * @param out
	 * @return <code>true</code> if the template was expanded,
	 * <code>false</code> if no such template exists
	 */
	final public boolean expand(String templateName, PrintWriter out) {
		return expand(this, templateName, out);
	}
	
	/**
	 * Expand a template
	 * @param tc Context to expand the template in
	 * @param templateName
	 * @param out
	 * @return <code>true</code> if the template was expanded,
	 * <code>false</code> if no such template exists
	 */
	abstract public boolean expand(TemplateContext tc, String templateName, PrintStream out);

	/**
	 * Expand a template
	 * @param tc Context to expand the template in
	 * @param templateName
	 * @param out
	 * @return <code>true</code> if the template was expanded,
	 * <code>false</code> if no such template exists
	 */
	abstract public boolean expand(TemplateContext tc, String templateName, PrintWriter out);

	/**
	 * Bind a string value to a variable
	 * @param varName Variable to bind
	 * @param value Value to bind
	 */
	abstract public void bind(String varName, String value);

	/**
	 * Bind a template expansion to a variable.
	 * Synonymous to <code>bind(varName, expand(templateName))</code>.
	 * @param varName Variable to bind
	 * @param templateName The template to expand
	 */
	final public void bindExpansion(String varName, String templateName) {
		bind(varName, expand(templateName));
	}

	/**
 	 * Unbinds all currently bound variables.
 	 */
	abstract public void flushVariables();
}
