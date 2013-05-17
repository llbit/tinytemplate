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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.jastadd.tinytemplate.TemplateParser.SyntaxError;

/**
 * Tiny template engine.
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class TinyTemplate extends TemplateContext {

	/**
 	 * Output indentation scheme
 	 */
	private Indentation indentation = new Indentation("  ");

	/**
	 * Template map
	 */
	private final Map<String, Template> templates = new HashMap<String, Template>();

	static private boolean throwExceptions = false;

	static private boolean printWarnings = true;

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
	 * Toggle whether exceptions shall be thrown whenever a template expansion
	 * fails.
	 * @param b
	 */
	public static void throwExceptions(boolean b) {
		throwExceptions = b;
	}

	/**
	 * Toggle whether warnings shall be printed to stderr.
	 * @param b
	 */
	public static void printWarnings(boolean b) {
		printWarnings = b;
	}

	@Override
	public void expand(TemplateContext tc, String templateName, PrintStream out) {
		Template temp = lookupTemplate(templateName);
		if (temp != null) {
			temp.expand(tc, out);
		}
	}

	@Override
	public void expand(TemplateContext tc, String templateName, PrintWriter out) {
		Template temp = lookupTemplate(templateName);
		if (temp != null) {
			temp.expand(tc, out);
		}
	}

	@Override
	public void expand(TemplateContext tc, String templateName, StringBuffer out) {
		Template temp = lookupTemplate(templateName);
		if (temp != null) {
			temp.expand(tc, out);
		}
	}

	@Override
	public void expand(TemplateContext tc, String templateName, StringBuilder out) {
		Template temp = lookupTemplate(templateName);
		if (temp != null) {
			temp.expand(tc, out);
		}
	}

	/**
	 * Search for a template with a given name. If the template does not
	 * exist a warning is generated.
	 * @param name
	 * @return <code>null</code> if no template with the given name exists
	 */
	private Template lookupTemplate(String name) {
		Template temp = templates.get(name);
		if (temp == null) {
			expansionWarning("unknown template '" + name + "'");
		}
		return temp;
	}

	@Override
	public void flushVariables() {
	}

	@Override
	public void bind(String varName, Object value) {
		throw new UnsupportedOperationException("Can not bind variable on root template context");
	}

	@Override
	public void bind(String varName, boolean value) {
		throw new UnsupportedOperationException("Can not bind variable on root template context");
	}

	@Override
	public Object evalVariable(String varName) {
		String msg = "unbound variable " + varName;
		return expansionWarning(msg);
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

	@Override
	public Object evalAttribute(String attribute) {
		return evalAttribute(attribute, null);
	}

	/**
	 * Eval attribute on context object
	 * @param attribute
	 * @param context
	 * @return The value of the attribute on context object
	 */
	public static Object evalAttribute(String attribute, Object context) {
		try {
			if (context == null) {
				String msg = "failed to eval " + attribute + "; reason: no context";
				return expansionWarning(msg);
			}
			Method method = context.getClass().getMethod(attribute, new Class[] {});
			return method.invoke(context, new Object[] {});
		} catch (SecurityException e) {
			String msg = "failed to eval attribute '" + attribute + "'; reason: security exception";
			return expansionWarning(msg);
		} catch (NoSuchMethodException e) {
			String msg = "failed to eval attribute '" + attribute + "'; reason: no such method";
			return expansionWarning(msg);
		} catch (IllegalArgumentException e) {
			String msg = "failed to eval attribute '" + attribute + "'; reason: illegal argument exception";
			return expansionWarning(msg);
		} catch (IllegalAccessException e) {
			String msg = "failed to eval attribute '" + attribute + "'; reason: illegal access exception";
			return expansionWarning(msg);
		} catch (InvocationTargetException e) {
			String msg = "failed to eval attribute '" + attribute + "'; reason: invocation target exception";
			return expansionWarning(msg);
		}
	}

	/**
	 * Prints a template expansion warning to stderr
	 * @param msg
	 * @return Expansion-replacing error message
	 */
	private static String expansionWarning(String msg) {
		if (throwExceptions) {
			throw new TemplateExpansionWarning(msg);
		}
		if (printWarnings) {
			System.err.println("Template expansion warning: " + msg);
		}
		return "<" + msg + ">";
	}

	@Override
	public String evalIndentation(int levels) {
		return indentation.get(levels);
	}

	/**
 	 * Set new indentation scheme
 	 * @param indent A single indentation
 	 */
	public void setIndentation(String indent) {
		indentation = new Indentation(indent);
	}

	/**
	 * @param out
	 */
	public void printTemplateAspect(PrintStream out) {
		Indentation ind = new Indentation("\t");
		out.println("aspect TemplateAspect {");
		for (Map.Entry<String, Template> entry: templates.entrySet()) {
			String name = entry.getKey();
			Template template = entry.getValue();
			out.println(ind.get(1) + "public void " + name + ".prettyPrint(PrettyPrinter out) {");
			template.printITD(ind, 2, out);
			out.println(ind.get(1) + "}");
		}
		out.println("}");
	}
}
