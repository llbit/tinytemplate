/* Copyright (c) 2010-2013, Jesper Öqvist <jesper@cs.lth.se>
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
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

import org.jastadd.tinytemplate.TemplateParser.SyntaxError;


/**
 * Tiny template engine.
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class TinyTemplate {

	/**
 	 * Output indentation scheme
 	 */
	private Indentation indentation = new Indentation("  ");
	
	private Map<String, Template> templates = new HashMap<String, Template>();
	
	private Stack<Object> context = new Stack<Object>();

	/**
 	 * Variable stack
 	 */
	private List<Map<String, String>> variables =
		new ArrayList<Map<String, String>>();
	
	{
		variables.add(new HashMap<String, String>());
	}

	/**
 	 * Start with empty template set
 	 */
	public TinyTemplate() {
	}
	
	/**
	 * Load templates from input stream
	 * @param in
	 * @throws SyntaxError 
	 */
	public TinyTemplate(InputStream in) throws SyntaxError {
		loadTemplates(in);
	}
	
	/**
	 * Load a templates from string
	 * @param string
	 * @throws SyntaxError 
	 */
	public TinyTemplate(String string) throws SyntaxError {
		loadTemplates(string);
	}
	
	/**
	 * Push a context object on the context stack.
	 * Call this when entering a context.
	 * 
	 * @param obj
	 */
	public void pushContext(Object obj) {
		context.push(obj);
		variables.add(new HashMap<String, String>());
	}
	
	/**
	 * Remove the top object from the context stack.
	 * Call this when leaving a context.
	 */
	public void popContext() {
		if (!context.isEmpty()) {
			context.pop();
			variables.remove(variables.size()-1);
		} else {
			throw new RuntimeException("Can not pop empty context stack!");
		}
	}

	/**
	 * Expand a template
	 * @param templateName
	 * @return The template expansion
	 */
	public String expand(String templateName) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		expand(templateName, new PrintStream(out));
		return out.toString();
	}
	
	/**
	 * Expand a template
	 * @param templateName
	 * @param out
	 */
	public void expand(String templateName, OutputStream out) {
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
	public boolean expand(String templateName, PrintStream out) {
		Template temp = templates.get(templateName);
		if (temp == null) {
			return false;
		} else {
			temp.expand(this, out);
			return true;
		}
	}

	/**
 	 * Unbinds all currently bound variables.
 	 */
	public void flushVariables() {
		variables.clear();
	}
	
	/**
	 * Bind a string value to a variable
	 * @param varName Variable to bind
	 * @param value Value to bind
	 */
	public void bind(String varName, String value) {
		variables.get(variables.size()-1).put(varName, value);
	}

	/**
	 * Bind a template expansion to a variable.
	 * Synonymous to <code>bind(varName, expand(templateName))</code>.
	 * @param varName Variable to bind
	 * @param templateName The template to expand
	 */
	public void bindExpansion(String varName, String templateName) {
		bind(varName, expand(templateName));
	}

	/**
 	 * Lookup variable on the variable stack and return the variable expansion
 	 * if it was found.
	 * @param varName
	 * @return The variable value, or the string "&lt;unbound variable varName&gt;"
	 * if the variable was not bound
	 */
	public String evalVariable(String varName) {
		for (int i = variables.size()-1; i >= 0; i -= 1) {
			String var = variables.get(i).get(varName);
			if (var != null) {
				return var;
			}
		}
		String msg = "unbound variable " + varName;
		expansionWarning(msg);
		return "<" + msg + ">";
	}
	
	
	/**
	 * Load a template file
	 * @param in
	 * @throws SyntaxError 
	 */
	public void loadTemplates(InputStream in) throws SyntaxError {
		TemplateParser parser = new TemplateParser(this, in);
		parser.parse();
	}

	/**
	 * Load templates from string literal
	 * @param str
	 * @throws SyntaxError 
	 */
	public void loadTemplates(String str) throws SyntaxError {
		TemplateParser parser = new TemplateParser(this,
				new ByteArrayInputStream(str.getBytes()));
		parser.parse();
	}

	/**
	 * Add a template to the template map
	 * @param templateName
	 * @param template
	 */
	public void addTemplate(String templateName, Template template) {
		templates.put(templateName, template);
	}

	/**
	 * Evaluate an attribute
	 * @param attribute
	 * @return The string value returned from the attribute
	 */
	public String evalAttribute(String attribute) {
		try {
			if (context.isEmpty()) {
				String msg = "failed to eval " + attribute + "; reason: no context";
				expansionWarning(msg);
				return "<" + msg + ">";
			}
			Object contextObj = context.peek();
			Method method = contextObj.getClass().getMethod(attribute, new Class[] {});
			return "" + method.invoke(contextObj, new Object[] {});
		} catch (SecurityException e) {
			String msg = "failed to eval " + attribute + "; reason: security exception";
			expansionWarning(msg);
			return "<" + msg + ">";
		} catch (NoSuchMethodException e) {
			String msg = "failed to eval " + attribute + "; reason: no such method";
			expansionWarning(msg);
			return "<" + msg + ">";
		} catch (IllegalArgumentException e) {
			String msg = "failed to eval " + attribute + "; reason: illegal argument exception";
			expansionWarning(msg);
			return "<" + msg + ">";
		} catch (IllegalAccessException e) {
			String msg = "failed to eval " + attribute + "; reason: illegal access exception";
			expansionWarning(msg);
			return "<" + msg + ">";
		} catch (InvocationTargetException e) {
			String msg = "failed to eval " + attribute + "; reason: invocation target exception";
			expansionWarning(msg);
			return "<" + msg + ">";
		}
	}

	/**
	 * Prints a template expansion warning to stderr
	 * @param msg
	 */
	private static void expansionWarning(String msg) {
		System.err.println("Template expansion warning: " + msg);
	}

	/**
 	 * @param levels Number of indentation levels
 	 * @return The cumulative indentation corresponding to the given
 	 * indentation level
 	 */
	public String evalIndentation(int levels) {
		return indentation.getIndentation(levels);
	}

	/**
 	 * Set new indentation scheme
 	 * @param indent A single indentation
 	 */
	public void setIndentation(String indent) {
		indentation = new Indentation(indent);
	}
}
