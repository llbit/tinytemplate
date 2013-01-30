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

import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import se.llbit.tinytemplate.Indentation.IndentationFragment;
import se.llbit.tinytemplate.fragment.AttributeReference;
import se.llbit.tinytemplate.fragment.IFragment;
import se.llbit.tinytemplate.fragment.NewlineFragment;
import se.llbit.tinytemplate.fragment.ReferenceFragment;
import se.llbit.tinytemplate.fragment.StringFragment;
import se.llbit.tinytemplate.fragment.VariableReference;

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
	public void expand(TinyTemplate template, PrintStream out) {
		for (IFragment fragment : fragments) {
			fragment.expand(template, out);
		}
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

	private void addIndentation(ReferenceFragment ref) {
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
}
