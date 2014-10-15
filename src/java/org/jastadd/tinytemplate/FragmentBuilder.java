/* Copyright (c) 2014, Jesper Öqvist <jesper.oqvist@cs.lth.se>
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

import org.jastadd.tinytemplate.TemplateParser.SyntaxError;
import org.jastadd.tinytemplate.fragment.AttributeReference;
import org.jastadd.tinytemplate.fragment.Concat;
import org.jastadd.tinytemplate.fragment.Conditional;
import org.jastadd.tinytemplate.fragment.Include;
import org.jastadd.tinytemplate.fragment.StringFragment;
import org.jastadd.tinytemplate.fragment.VariableReference;

/**
 * @author Jesper Öqvist <jesper.oqvist@cs.lth.se>
 */
public interface FragmentBuilder {
	Conditional newIf(String condition, Template thenPart) throws SyntaxError;
	Conditional newIf(String condition, Template thenPart, Template elsePart) throws SyntaxError;
	Include newInclude(String template);
	VariableReference newVariableReference(String var);
	AttributeReference newAttributeReference(String attr);
	Template newTemplate();
	StringFragment newStringFragment(String theString);
	Concat newConcat(String iterable, String sep) throws SyntaxError;

	public static final FragmentBuilder DEFAULT_BUILDER = new FragmentBuilder() {
		@Override
		public Conditional newIf(String condition, Template thenPart) throws SyntaxError {
			return new Conditional(condition, thenPart);
		}

		@Override
		public Conditional newIf(String condition, Template thenPart, Template elsePart) throws SyntaxError {
			return new Conditional(condition, thenPart, elsePart);
		}

		@Override
		public Include newInclude(String template) {
			return new Include(template);
		}

		@Override
		public VariableReference newVariableReference(String var) {
			return new VariableReference(var);
		}

		@Override
		public AttributeReference newAttributeReference(String attr) {
			return new AttributeReference(attr);
		}

		@Override
		public Template newTemplate() {
			return new Template();
		}

		@Override
		public StringFragment newStringFragment(String theString) {
			return new StringFragment(theString);
		}

		@Override
		public Concat newConcat(String iterable, String sep) throws SyntaxError {
			return new Concat(iterable, sep);
		}


	};

}
