/* Copyright (c) 2013 Jesper Öqvist <jesper@llbit.se>
 *
 * This file is part of tinytemplate.
 *
 * tinytemplate is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * tinytemplate is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with tinytemplate.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.llbit.tinytemplate;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import se.llbit.tinytemplate.TemplateParser.SyntaxError;

/**
 * Tiny template engine.
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class TinyTemplate {
	
	/**
	 * If <code>true</code> variables are not flushed after each expansion
	 */
	private boolean persistentVariables = true;
	
	private Map<String, String> variables = new HashMap<String, String>();
	
	private Map<String, Template> templates = new HashMap<String, Template>();
	
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
			if (!persistentVariables) {
				variables.clear();
			}
			return true;
		}
	}
	
	/**
	 * Bind a string value to a variable
	 * @param var
	 * @param value
	 */
	public void bind(String var, String value) {
		variables.put(var, value);
	}

	/**
	 * Set the PersistentVariables option.
	 * @param b New value for the option
	 */
	public void setPersistentVariables(boolean b) {
		persistentVariables = b;
	}

	/**
	 * @param variable
	 * @return The variable value, or the string "&lt;unbound variable NAME&gt;"
	 * if the variable was not bound
	 */
	public String evalVariable(String variable) {
		String var = variables.get(variable);
		return var != null ? var : ("<unbound variable " + variable + ">");
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
		// TODO Auto-generated method stub
		return null;
	}
}
