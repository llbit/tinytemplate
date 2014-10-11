/* Copyright (c) 2013, Jesper Öqvist <jesper.oqvist@cs.lth.se>
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

import java.io.PrintStream;
import java.io.PrintWriter;

public class SingleTemplateProcessor {
	static class SystemPropertyContext extends TemplateContext {
		
		private final TemplateContext parentContext;
		
		/**
	 	 * Create a new simple context
	 	 * @parent parent context
	 	 */
		public SystemPropertyContext(TemplateContext parent) {
			parentContext = parent;
		}

		@Override
		public Object evalVariable(String varName) {
			return System.getProperty(varName);
		}

		@Override
		public Object evalAttribute(String attribute) {
			return parentContext.evalAttribute(attribute);
		}

		@Override
		public String evalIndentation(int levels) {
			return parentContext.evalIndentation(levels);
		}

		@Override
		public void expand(TemplateContext tc, String templateName, PrintStream out) {
			parentContext.expand(tc, templateName, out);
		}

		@Override
		public void expand(TemplateContext tc, String templateName, PrintWriter out) {
			parentContext.expand(tc, templateName, out);
		}

		@Override
		public void expand(TemplateContext tc, String templateName, StringBuffer out) {
			parentContext.expand(tc, templateName, out);
		}

		@Override
		public void expand(TemplateContext tc, String templateName, StringBuilder out) {
			parentContext.expand(tc, templateName, out);
		}

		@Override
		public void flushVariables() {
		}
		
		@Override
		public void bind(String varName, Object value) {
		}

		@Override
		public void bind(String varName, boolean value) {
		}

	}
	public static void main(String[] args) {
		for (String arg: args) {
			if (arg.equals("-h") || arg.equals("-help")) {
				System.out.println("usage: tinytemplate < single template declaration");
				System.exit(0);
				return;
			}
		}
		try {
			TinyTemplate tt = new TinyTemplate();
			TemplateParser parser = new TemplateParser(tt, System.in);
			Template template = parser.parseSingleTemplate();
			template.expand(new SystemPropertyContext(tt), System.out);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
