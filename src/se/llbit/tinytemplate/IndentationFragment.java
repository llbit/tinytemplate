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

/**
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class IndentationFragment implements IFragment {
	
	private final int level;
	
	private static final String indentation = "  ";
	private static final List<String> ind = new ArrayList<String>(10);
	
	static {
		ind.add(indentation);
	}
	
	/**
	 * @param indentationLevel
	 */
	public IndentationFragment(int indentationLevel) {
		level = indentationLevel;
	}

	@Override
	public void expand(TinyTemplate template, PrintStream out) {
		ind(level);
	}
	
	private static String ind(int lvl) {
		while (lvl < (ind.size()-1)) {
			ind.add(ind.get(ind.size()-1) + indentation);
		}
		return ind.get(lvl-1);
	}

}
