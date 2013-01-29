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

/**
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class VariableReference implements IFragment {
	
	private final String variable;
	
	/**
	 * @param variableName
	 */
	public VariableReference(String variableName) {
		variable = variableName;
	}

	@Override
	public void expand(TinyTemplate template, PrintStream out) {
		out.print(template.evalVariable(variable));
	}

	@Override
	public String toString() {
		return "$(" + variable + ")";
	}
}
