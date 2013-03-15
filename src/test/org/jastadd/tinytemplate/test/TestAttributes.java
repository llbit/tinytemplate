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
import org.jastadd.tinytemplate.TinyTemplate;
import org.jastadd.tinytemplate.TemplateParser.SyntaxError;
import org.junit.Test;

/**
 * Test template variables
 * @author Jesper Öqvist <jesper.oqvist@cs.lth.se>
 */
public class TestAttributes {
	
	/**
	 * Set up the tests
	 */
	public TestAttributes() {
		TinyTemplate.printWarnings(false);
		TinyTemplate.throwExceptions(false);
	}
	
	/**
	 * Attribute evaluation calls the attribute method on the context object
	 * @throws SyntaxError
	 */
	@Test
	public void testEvaluation_1() throws SyntaxError {
		TinyTemplate tt = new TinyTemplate("foo = [[#toString]]");
		SimpleContext tc = new SimpleContext(tt, "the string");
		
		assertEquals("the string", tc.expand("foo"));
	}
	
	/**
	 * Attempting to evaluate an attribute on an object with no such method
	 * @throws SyntaxError
	 */
	@Test
	public void testEvaluation_2() throws SyntaxError {
		TinyTemplate tt = new TinyTemplate("foo = [[#imaginaryMethod]]");
		SimpleContext tc = new SimpleContext(tt, "the string");
		
		assertEquals("<failed to eval attribute 'imaginaryMethod'; reason: no such method>", tc.expand("foo"));
	}
	
	/**
	 * Attempting to evaluate attribute without context
	 * @throws SyntaxError
	 */
	@Test
	public void testEvaluation_3() throws SyntaxError {
		TinyTemplate tt = new TinyTemplate("foo = [[#imaginaryMethod]]");
		
		assertEquals("<failed to eval imaginaryMethod; reason: no context>", tt.expand("foo"));
	}
	
	/**
	 * Empty attribute names are not allowed
	 * @throws SyntaxError
	 */
	@Test
	public void testNameError_1() throws SyntaxError {
		try {
			new TinyTemplate("foo = [[#]]");
			fail("Expected syntax error!");
		} catch (SyntaxError e) {
			assertEquals("Syntax error at line 1: empty attribute name", e.getMessage());
		}
	}
	
	/**
	 * Attribute names must be valid Java identifiers
	 * @throws SyntaxError
	 */
	@Test
	public void testNameError_2() throws SyntaxError {
		try {
			new TinyTemplate("foo = [[#0x]]");
			fail("Expected syntax error!");
		} catch (SyntaxError e) {
			assertEquals("Syntax error at line 1: the attribute name '0x' is not a valid Java identifier", e.getMessage());
		}
	}
	
}
