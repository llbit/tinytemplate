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
import java.util.Collection;

/**
 * Template
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class Template {
	
	private Collection<IFragment> fragments = new ArrayList<IFragment>();

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

}
