/* Copyright (c) 2013, Jesper Öqvist <jesper@cs.lth.se>
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
package org.jastadd.tinytemplate.test;

import static org.junit.Assert.*;

import org.jastadd.tinytemplate.SimpleContext;
import org.jastadd.tinytemplate.TemplateContext;
import org.jastadd.tinytemplate.TinyTemplate;
import org.jastadd.tinytemplate.TemplateParser.SyntaxError;
import org.junit.Test;

/**
 * Tests for template include directives
 * @author Jesper Öqvist <jesper.oqvist@cs.lth.se>
 */
public class TestInclude {
	
	/**
	 * Constructor
	 */
	public TestInclude() {
		TinyTemplate.printWarnings(false);
		TinyTemplate.throwExceptions(false);
	}
	
	/**
	 * Tests expanding an included template
	 * @throws SyntaxError
	 */
	@Test
	public void testInclude_1() throws SyntaxError {
		TinyTemplate tt = new TinyTemplate(
				"a = [[boop]]\n" +
				"b = [[$include(a)]]");
		
		assertEquals("boop", tt.expand("b"));
	}
	
	/**
	 * Tests expanding a variable in an included template
	 * @throws SyntaxError
	 */
	@Test
	public void testInclude_2() throws SyntaxError {
		TinyTemplate tt = new TinyTemplate(
				"a = [[$boop]]\n" +
				"b = [[$include(a)]]");
		TemplateContext tc = new SimpleContext(tt, this);
		tc.bind("boop", "beep");
		assertEquals("beep", tc.expand("b"));
	}
	
	/**
	 * Test the include keyword with a hash instead of dollar sign
	 * @throws SyntaxError
	 */
	@Test
	public void testInclude_3() throws SyntaxError {
		TinyTemplate tt = new TinyTemplate(
				"a = [[$boop]]\n" +
				"b = [[#include(a)]]");
		TemplateContext tc = new SimpleContext(tt, this);
		tc.bind("boop", "beep");
		assertEquals("beep", tc.expand("b"));
	}
}
