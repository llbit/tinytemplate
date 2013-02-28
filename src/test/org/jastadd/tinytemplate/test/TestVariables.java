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
public class TestVariables {
	
	/**
	 * Set up the tests
	 */
	public TestVariables() {
		TinyTemplate.printWarnings(false);
	}
	
	/**
	 * Tests a template variable
	 * @throws SyntaxError
	 */
	@Test
	public void testVariable_1() throws SyntaxError {
		TinyTemplate tt = new TinyTemplate("test = [[$hello]]");
		SimpleContext tc = new SimpleContext(tt, new Object());
		
		tc.bind("hello", "hej");
		assertEquals("hej", tc.expand("test"));
	}
	
	/**
	 * Tests an unbound variable
	 * @throws SyntaxError
	 */
	@Test
	public void testVariable_2() throws SyntaxError {
		TinyTemplate tt = new TinyTemplate("test = [[x $hello y]]");
		
		assertEquals("x <unbound variable hello> y", tt.expand("test"));
	}
	
	/**
	 * Double dollar signs escape to a single dollar sign
	 * @throws SyntaxError
	 */
	@Test
	public void testVariable_3() throws SyntaxError {
		TinyTemplate tt = new TinyTemplate("test = [[ $$ not a variable ]]");
		
		assertEquals(" $ not a variable ", tt.expand("test"));
	}
	
	/**
	 * Simple variable names can contain any valid Java identifier character
	 * @throws SyntaxError
	 */
	@Test
	public void testVariable_4() throws SyntaxError {
		TinyTemplate tt = new TinyTemplate("foo = [[$8_wat]]");
		SimpleContext tc = new SimpleContext(tt, new Object());
		
		tc.bind("8_wat", "batman");
		assertEquals("batman", tc.expand("foo"));
	}
	
	/**
	 * Line endings in variable expansions are preserved as-is if the
	 * expansion is not indented
	 * @throws SyntaxError
	 */
	@Test
	public void testVariable_5() throws SyntaxError {
		TinyTemplate tt = new TinyTemplate("foo = [[$block]]");
		SimpleContext tc = new SimpleContext(tt, new Object());

		tc.bind("block",
				"{\n" +
				"  hello\r" +
				"  you\r\n" +
				"}");
				
		assertEquals(
				"{\n" +
				"  hello\r" +
				"  you\r\n" +
				"}", tc.expand("foo"));
	}
	
	/**
	 * Line endings in variable expansions are replaced by the current
	 * system's default line ending when the expansion is indented
	 * @throws SyntaxError
	 */
	@Test
	public void testVariable_6() throws SyntaxError {
		TinyTemplate tt = new TinyTemplate("foo = [[  x$block]]");
		SimpleContext tc = new SimpleContext(tt, new Object());

		tc.bind("block",
				"{\n" +
				"  hello\r" +
				"  you\r\n" +
				"}");
				
		String nl = System.getProperty("line.separator");
		assertEquals(
				"  x{" + nl +
				"    hello" + nl +
				"    you" + nl +
				"  }", tc.expand("foo"));
	}
	
	/**
	 * Tests variable shadowing in subcontext
	 * @throws SyntaxError
	 */
	@Test
	public void testVariable_7() throws SyntaxError {
		TinyTemplate tt = new TinyTemplate("a[[$x]]");
		SimpleContext c0 = new SimpleContext(tt, new Object());
		SimpleContext c1 = new SimpleContext(c0, new Object());
		
		c0.bind("x", "123");
		c1.bind("x", "UIO");
		
		assertEquals("UIO", c1.expand("a"));
		assertEquals("123", c0.expand("a"));
	}
	
	/**
	 * Parenthesized variable names can contain many different special characters
	 * @throws SyntaxError
	 */
	@Test
	public void testVariable_8() throws SyntaxError {
		try {
			new TinyTemplate("foo = [[$(:;^(xyz)) wat1..]]");
			fail("Expected syntax error!");
		} catch (SyntaxError e) {
			assertEquals("Parse error at line 1: illegal characters in variable name :;^(xyz)", e.getMessage());
		}
	}
	
	/**
	 * Parenthesized variable names can contain many different special characters
	 * @throws SyntaxError
	 */
	@Test
	public void testVariable_9() throws SyntaxError {
		try {
			new TinyTemplate("foo = [[$abc(%(!&*))=\n" +
					"wat1..]]");
			fail("Expected syntax error!");
		} catch (SyntaxError e) {
			assertEquals("Parse error at line 1: illegal characters in variable name abc(%(!&*))", e.getMessage());
		}
	}
	
	/**
	 * Parenthesis inside a variable name are parsed but not accepted
	 * @throws SyntaxError
	 */
	@Test
	public void testVariable_10() throws SyntaxError {
		try {
			new TinyTemplate("foo = [[$abc(xyz)]]");
			fail("Expected syntax error!");
		} catch (SyntaxError e) {
			assertEquals("Parse error at line 1: illegal characters in variable name abc(xyz)", e.getMessage());
		}
	}
}
