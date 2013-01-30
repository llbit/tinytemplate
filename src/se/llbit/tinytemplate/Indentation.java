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

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import se.llbit.tinytemplate.fragment.IFragment;

/**
 * Indentation fragment factory and indentation scheme
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class Indentation {

	/**
	 * Indentation fragment
	 */
	public static class IndentationFragment implements IFragment {
		private final int level;

		protected IndentationFragment(int indentLevel) {
			level = indentLevel;
		}

		@Override
		public void expand(TinyTemplate template, PrintStream out) {
			out.print(template.evalIndentation(level));
		}
	}

	private final String indentation;
	private final List<String> ind = new ArrayList<String>(32);

	private static final List<IFragment> fragments =
		new ArrayList<IFragment>(32);
	
	/**
	 * Create a new indentation scheme
	 * @param indent One level of indentation
	 */
	public Indentation(String indent) {
		indentation = indent;
		ind.add("");
	}
		
	/**
	 * @param level The level of indentation
	 * @return An indentation fragment for the given indentation level
	 */
	public static IFragment getFragment(int level) {
		while (fragments.size() < (level+1)) {
			fragments.add(new IndentationFragment(fragments.size()));
		}
		return fragments.get(level);
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

}
