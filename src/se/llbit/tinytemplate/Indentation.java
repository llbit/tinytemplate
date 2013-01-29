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

import java.util.ArrayList;
import java.util.List;

/**
 * Indentation fragment factory
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class Indentation {
	private static final String indentation = "  ";
	private static final List<String> ind = new ArrayList<String>(32);
	private static final List<IFragment> fragments = new ArrayList<IFragment>(32);
	
	static {
		resetIndentation();
	}

	private static void resetIndentation() {
		ind.clear();
		ind.add("");
		fragments.add(new StringFragment(""));
	}
		
	/**
	 * @param level The level of indentation
	 * @return StringFragment corresponding to the given indentation level
	 */
	public IFragment getIndentation(int level) {
		while (ind.size() < (level+1)) {
			String str = ind.get(ind.size()-1) + indentation;
			ind.add(str);
			fragments.add(new StringFragment(str));
		}
		return fragments.get(level);
	}

}
