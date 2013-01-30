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
package se.llbit.tinytemplate.fragment;

import java.io.PrintStream;

import se.llbit.tinytemplate.TinyTemplate;
import se.llbit.tinytemplate.Indentation.IndentationFragment;

/**
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public abstract class ReferenceFragment implements IFragment {
	
	private IndentationFragment indentation = null;
	
	protected void expandWithIndentation(String expansion,
			TinyTemplate template, PrintStream out) {
		
		if (indentation == null) {
			out.print(expansion);
		} else {
			String[] lines = expansion.split("\n|\r\n?");
			for (int i = 0; i < lines.length; ++i) {
				if (i != 0) {
					indentation.expand(template, out);
				}
				if ((i+1) < lines.length) {
					out.println(lines[i]);
				} else {
					out.print(lines[i]);
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
