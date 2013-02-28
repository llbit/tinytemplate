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
import org.jastadd.tinytemplate.TemplateParser.SyntaxError;
import org.jastadd.tinytemplate.TinyTemplate;
import org.junit.Test;

/**
 * Tests for conditional statements
 * @author Jesper Öqvist <jesper.oqvist@cs.lth.se>
 */
public class TestConditionals {
	
	/**
	 * Test the if-then conditional
	 * @throws SyntaxError
	 */
	@Test
	public void testConditional_1() throws SyntaxError {
		TinyTemplate tt = new TinyTemplate("foo = [[$(if(cond))boo!$endif]]");
		SimpleContext tc = new SimpleContext(tt, new Object());

		tc.bind("cond", "true");
		
		assertEquals("boo!", tc.expand("foo"));
	}
	
	/**
	 * Test the if-then conditional
	 * @throws SyntaxError
	 */
	@Test
	public void testConditional_2() throws SyntaxError {
		TinyTemplate tt = new TinyTemplate("foo = [[$(if(cond))boo!$endif]]");
		SimpleContext tc = new SimpleContext(tt, new Object());

		tc.bind("cond", "not true");
		
		assertEquals("", tc.expand("foo"));
	}
	
	/**
	 * Case sensitive "true"
	 * @throws SyntaxError
	 */
	@Test
	public void testConditional_3() throws SyntaxError {
		TinyTemplate tt = new TinyTemplate("foo = [[$(if(cond))boo!$endif]]");
		SimpleContext tc = new SimpleContext(tt, new Object());

		tc.bind("cond", "True");
		
		assertEquals("", tc.expand("foo"));
	}
	
	/**
	 * Test the if-then-else conditional
	 * @throws SyntaxError
	 */
	@Test
	public void testConditional_4() throws SyntaxError {
		TinyTemplate tt = new TinyTemplate("foo = [[$(if(cond))boo!$(else)mjau$endif]]");
		SimpleContext tc = new SimpleContext(tt, new Object());

		tc.bind("cond", "");
		
		assertEquals("mjau", tc.expand("foo"));
	}
	
	/**
	 * Test the if-then-else conditional with negated condition
	 * @throws SyntaxError
	 */
	@Test
	public void testConditional_5() throws SyntaxError {
		TinyTemplate tt = new TinyTemplate("foo = [[$(if(!cond))boo!$(else)mjau$endif]]");
		SimpleContext tc = new SimpleContext(tt, new Object());

		tc.bind("cond", "");
		
		assertEquals("boo!", tc.expand("foo"));
	}

	/**
	 * Test nested conditional
	 * @throws SyntaxError
	 */
	@Test
	public void testNested_1() throws SyntaxError {
		TinyTemplate tt = new TinyTemplate(
				"Father = [[" +
				"$if(x) x\n" +
				"$(if(y))Wednesday$endif\n" +
				"$endif" +
				"]]");
		SimpleContext tc = new SimpleContext(tt, new Object());

		tc.bind("x", "true");
		tc.bind("y", "true");
		
		String nl = System.getProperty("line.separator");
		assertEquals(" x" + nl + "Wednesday" + nl, tc.expand("Father"));
	}

	/**
	 * Test trimming leading newline
	 * @throws SyntaxError
	 */
	@Test
	public void testTrimming_1() throws SyntaxError {
		TinyTemplate tt = new TinyTemplate(
				"Father = [[" +
				"$if(x)  \t \n" +
				"$(if(y))Wednesday$endif\n" +
				"$endif" +
				"]]");
		SimpleContext tc = new SimpleContext(tt, new Object());

		tc.bind("x", "true");
		tc.bind("y", "true");
		
		String nl = System.getProperty("line.separator");
		assertEquals("Wednesday" + nl, tc.expand("Father"));
	}

}