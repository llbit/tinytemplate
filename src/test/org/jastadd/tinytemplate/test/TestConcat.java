/* Copyright (c) 2013, Niklas Fors <niklas.fors@cs.lth.se>
 *               2013, Jesper Öqvist <jesper.oqvist@cs.lth.se>
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

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.jastadd.tinytemplate.SimpleContext;
import org.jastadd.tinytemplate.TemplateContext;
import org.jastadd.tinytemplate.TemplateParser.SyntaxError;
import org.jastadd.tinytemplate.TinyTemplate;
import org.junit.Test;

/**
 * Tests for template concatenation statements
 * @author Niklas Fors <niklas.fors@cs.lth.se>
 */
@SuppressWarnings("javadoc")
public class TestConcat {

	private static final String NL = System.getProperty("line.separator");

	/**
	 * Constructor
	 */
	public TestConcat() {
		TinyTemplate.printWarnings(false);
		TinyTemplate.throwExceptions(true);
	}

	/**
	 * Tests simple concatenation
	 * @throws SyntaxError
	 */
	@Test
	public void testConcat_1() throws SyntaxError {
		TinyTemplate tt = new TinyTemplate(
				"t = [[$cat(#list)]]");
		TemplateContext tc = new SimpleContext(tt, new A());
		assertEquals("123", tc.expand("t"));
	}

	/**
	 * Tests concatenation with a separator
	 * @throws SyntaxError
	 */
	@Test
	public void testConcat_2() throws SyntaxError {
		TinyTemplate tt = new TinyTemplate(
				"t = [[$cat(#list, \",\")]]");
		TemplateContext tc = new SimpleContext(tt, new A());
		assertEquals("1,2,3", tc.expand("t"));
	}

	/**
	 * Tests concatenation with whitespace in separator
	 * @throws SyntaxError
	 */
	@Test
	public void testConcat_3() throws SyntaxError {
		TinyTemplate tt = new TinyTemplate(
				"t = [[$cat(#list,\", \")]]");
		TemplateContext tc = new SimpleContext(tt, new A());
		assertEquals("1, 2, 3", tc.expand("t"));
	}

	/**
	 * Tests indentation
	 * @throws SyntaxError
	 */
	@Test
	public void testIndentation_1() throws SyntaxError {
		TinyTemplate tt = new TinyTemplate(
				"t = [[  $cat(#list, \"\n\")]]");
		TemplateContext tc = new SimpleContext(tt, new A());
		assertEquals(
				"  1" + NL +
				"  2" + NL +
				"  3",
				tc.expand("t"));
	}

	public static class A {
		public ArrayList<Integer> list() {
			ArrayList<Integer> list = new ArrayList<Integer>();
			list.add(1);
			list.add(2);
			list.add(3);
			return list;
		}
	}

	@Test(expected=SyntaxError.class)
	public void testSyntaxError1_() throws SyntaxError {
		new TinyTemplate("t = [[$cat(#list, )]]");
	}

	@Test(expected=SyntaxError.class)
	public void testSyntaxError_2() throws SyntaxError {
		new TinyTemplate("t = [[$cat(list)]]");
	}

	@Test(expected=SyntaxError.class)
	public void testSyntaxError_3() throws SyntaxError {
		new TinyTemplate("t = [[$cat(#list, \")]]");
	}

	@Test(expected=SyntaxError.class)
	public void testSyntaxError_4() throws SyntaxError {
		new TinyTemplate("t = [[$cat(#list, \"\"\")]]");
	}

	@Test(expected=SyntaxError.class)
	public void testSyntaxError_5() throws SyntaxError {
		new TinyTemplate("t = [[$cat]]");
	}

	@Test(expected=SyntaxError.class)
	public void testSyntaxError_6() throws SyntaxError {
		new TinyTemplate("t = [[$cat($a]]");
	}
}
