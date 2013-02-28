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

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.jastadd.tinytemplate.SimpleContext;
import org.jastadd.tinytemplate.TinyTemplate;
import org.jastadd.tinytemplate.TemplateParser.SyntaxError;
import org.junit.Test;

/**
 * General unit tests for tinytemplate
 * @author Jesper Öqvist <jesper.oqvist@cs.lth.se>
 */
public class TestTinyTemplate {
	
	/**
	 * Constructor
	 */
	public TestTinyTemplate() {
		TinyTemplate.printWarnings(false);
	}
	
	@SuppressWarnings("javadoc")
	@Test
	public void testUndefinedTemplate() throws SyntaxError {
		TinyTemplate tt = new TinyTemplate("");
		
		assertEquals("", tt.expand("test"));
		assertFalse("expand returns false if the template was not expanded",
				tt.expand("test", new PrintStream(new ByteArrayOutputStream())));
	}
	
	@SuppressWarnings("javadoc")
	@Test
	public void testSimple_1() throws SyntaxError {
		TinyTemplate tt = new TinyTemplate("test [[hello]]");
		
		assertEquals("hello", tt.expand("test"));
		assertTrue("expand returns true if the template was expanded",
				tt.expand("test", new PrintStream(new ByteArrayOutputStream())));
	}
	
	@SuppressWarnings("javadoc")
	@Test
	public void testSimple_2() throws SyntaxError {
		TinyTemplate tt = new TinyTemplate("foo [[]]");
		
		assertEquals("", tt.expand("foo"));
		assertTrue("expand returns true if the template was expanded",
				tt.expand("foo", new PrintStream(new ByteArrayOutputStream())));
	}
	
	/**
	 * Multiple templates in one "file"
	 * @throws SyntaxError
	 */
	@Test
	public void testSimple_3() throws SyntaxError {
		TinyTemplate tt = new TinyTemplate(
				"foo [[ baa ]] bar [[ faa ]]");
		
		assertEquals(" baa ", tt.expand("foo"));
		assertEquals(" faa ", tt.expand("bar"));
	}
	
	/**
	 * Very many special characters can be used in template names
	 * @throws SyntaxError
	 */
	@Test
	public void testSimple_4() throws SyntaxError {
		TinyTemplate tt = new TinyTemplate("$(%)!}@{ [[=)]]");
		
		assertEquals("=)", tt.expand("$(%)!}@{"));
	}
	
	/**
	 * Newlines in template body
	 * @throws SyntaxError
	 */
	@Test
	public void testSimple_5() throws SyntaxError {
		TinyTemplate tt = new TinyTemplate(
				"x= [[\n" +
				"z\r" +
				"\r\n" +
				"]]");
		
		String nl = System.getProperty("line.separator");
		assertEquals("z" + nl + nl, tt.expand("x"));
	}
	
	/**
	 * Newlines in template body
	 * @throws SyntaxError
	 */
	@Test
	public void testSimple_6() throws SyntaxError {
		TinyTemplate tt = new TinyTemplate(
				"foo =\n" +
				"[[void a() {\n" +
				"  baa;\n" +
				"}\n" +
				"\n" +
				"void b() {\n" +
				"  boo(hoo);\n" +
				"}]]");
		
		String nl = System.getProperty("line.separator");
		assertEquals(
				"void a() {" + nl +
				"  baa;" + nl +
				"}" + nl +
				nl +
				"void b() {" + nl +
				"  boo(hoo);" + nl +
				"}", tt.expand("foo"));
	}
	
	/**
	 * Missing template name
	 * @throws SyntaxError 
	 */
	@Test(expected=SyntaxError.class)
	public void testSyntaxError_1() throws SyntaxError {
		new TinyTemplate("[[]]");
	}
	
	/**
	 * Missing template name
	 * @throws SyntaxError 
	 */
	@Test(expected=SyntaxError.class)
	public void testSyntaxError_2() throws SyntaxError {
		new TinyTemplate(" = [[]]");
	}
	
	/**
	 * Missing end of template body
	 * @throws SyntaxError 
	 */
	@Test(expected=SyntaxError.class)
	public void testSyntaxError_3() throws SyntaxError {
		new TinyTemplate("x = [[  ");
	}
	
	/**
	 * Missing end of template body
	 * @throws SyntaxError 
	 */
	@Test(expected=SyntaxError.class)
	public void testSyntaxError_4() throws SyntaxError {
		new TinyTemplate("x = [[  ]");
	}
	
	/**
	 * Missing start of template body
	 * @throws SyntaxError 
	 */
	@Test(expected=SyntaxError.class)
	public void testSyntaxError_5() throws SyntaxError {
		new TinyTemplate("x = ]]");
	}
	
	/**
	 * Missing start of template body
	 * @throws SyntaxError 
	 */
	@Test(expected=SyntaxError.class)
	public void testSyntaxError_6() throws SyntaxError {
		new TinyTemplate("x = [  ]]");
	}
	
	/**
	 * Double brackets not allowed inside template body
	 * @throws SyntaxError
	 */
	@Test(expected=SyntaxError.class)
	public void testSyntaxError_7() throws SyntaxError {
		new TinyTemplate("x = [[ [[ ]]");
	}
	
	/**
	 * Double brackets not allowed inside template body
	 * @throws SyntaxError
	 */
	@Test(expected=SyntaxError.class)
	public void testSyntaxError_8() throws SyntaxError {
		new TinyTemplate("x = [[ ]] ]]");
	}
	
	/**
	 * Missing template body
	 * @throws SyntaxError
	 */
	@Test(expected=SyntaxError.class)
	public void testSyntaxError_9() throws SyntaxError {
		new TinyTemplate("x = ");
	}
	
	/**
	 * Missing template body
	 * @throws SyntaxError
	 */
	@Test(expected=SyntaxError.class)
	public void testSyntaxError_10() throws SyntaxError {
		new TinyTemplate("x");
	}
	
	/**
	 * Attribute evaluation calls the attribute method on the context object
	 * @throws SyntaxError
	 */
	@Test
	public void testAttribute_1() throws SyntaxError {
		TinyTemplate tt = new TinyTemplate("foo = [[#toString]]");
		SimpleContext tc = new SimpleContext(tt, "the string");
		
		assertEquals("the string", tc.expand("foo"));
	}
	
	/**
	 * Attempting to evaluate an attribute on an object with no such method
	 * @throws SyntaxError
	 */
	@Test
	public void testAttribute_2() throws SyntaxError {
		TinyTemplate tt = new TinyTemplate("foo = [[#imaginaryMethod]]");
		SimpleContext tc = new SimpleContext(tt, "the string");
		
		assertEquals("<failed to eval imaginaryMethod; reason: no such method>", tc.expand("foo"));
	}
	
	/**
	 * Attempting to evaluate attribute without context
	 * @throws SyntaxError
	 */
	@Test
	public void testAttribute_3() throws SyntaxError {
		TinyTemplate tt = new TinyTemplate("foo = [[#imaginaryMethod]]");
		
		assertEquals("<failed to eval imaginaryMethod; reason: no context>", tt.expand("foo"));
	}
	
	/**
	 * Multiple assign are allowed between template name and template body
	 * @throws SyntaxError
	 */
	@Test
	public void testMultiAssign_1() throws SyntaxError {
		TinyTemplate tt = new TinyTemplate(
				"test == \n" +
				"== ==== = ===== [[$hello]]");
		SimpleContext tc = new SimpleContext(tt, new Object());
		
		tc.bind("hello", "hej");
		assertEquals("hej", tc.expand("test"));
	}
	
	/**
	 * Multiple template names are allowed for each template
	 * @throws SyntaxError
	 */
	@Test
	public void testSynonyms_1() throws SyntaxError {
		TinyTemplate tt = new TinyTemplate(
				"test == foo = = = bar [[$hello]]");
		SimpleContext tc = new SimpleContext(tt, new Object());
		
		tc.bind("hello", "hej");
		assertEquals("hej", tc.expand("test"));
		assertEquals("hej", tc.expand("foo"));
		assertEquals("hej", tc.expand("bar"));
	}
	
	/**
	 * Multiple template names are allowed for each template
	 * @throws SyntaxError
	 */
	@Test
	public void testSynonyms_2() throws SyntaxError {
		TinyTemplate tt = new TinyTemplate(
				"test foo bar [[$hello]]");
		SimpleContext tc = new SimpleContext(tt, new Object());
		
		tc.bind("hello", "hej");
		assertEquals("hej", tc.expand("test"));
		assertEquals("hej", tc.expand("foo"));
		assertEquals("hej", tc.expand("bar"));
	}
	
	/**
	 * Tests a template comment
	 * @throws SyntaxError
	 */
	@Test
	public void testComment_1() throws SyntaxError {
		TinyTemplate tt = new TinyTemplate(
				"# line comment\n" +
				"test = [[$hello]]");
		SimpleContext tc = new SimpleContext(tt, new Object());
		
		tc.bind("hello", "hej");
		assertEquals("hej", tc.expand("test"));
	}
	
	/**
	 * Tests a template comment
	 * @throws SyntaxError
	 */
	@Test
	public void testComment_2() throws SyntaxError {
		TinyTemplate tt = new TinyTemplate(
				"foo=[[x]]# comment\n" +
				"# test = [[y]]");
		
		assertEquals("", tt.expand("test"));
		assertEquals("x", tt.expand("foo"));
	}
	
	/**
	 * Tests a template comment
	 * @throws SyntaxError
	 */
	@Test
	public void testComment_3() throws SyntaxError {
		TinyTemplate tt = new TinyTemplate(
				"foo=[[## not a comment]]");
		
		assertEquals("hash signs inside a template body are not comments",
				"# not a comment", tt.expand("foo"));
	}
	
	/**
	 * Tests that indentation is replaced correctly
	 * @throws SyntaxError
	 */
	@Test
	public void testIndentation_1() throws SyntaxError {
		TinyTemplate tt = new TinyTemplate(
				"foo = \n" +
				"[[void m() {\n" +
				"  \n" +
				"    2  \n" +
				"        ]]");

		tt.setIndentation("\t");

		String nl = System.getProperty("line.separator");
		assertEquals(
				"void m() {" + nl +
				"\t" + nl +
				"\t\t2  " + nl +
				"\t\t\t\t", tt.expand("foo"));
	}
	
	/**
	 * Expansions can be correctly indented, but the indentation inside the
	 * expansion is not touched.
	 * @throws SyntaxError
	 */
	@Test
	public void testIndentationExpansion_1() throws SyntaxError {
		TinyTemplate tt = new TinyTemplate(
				"foo = \n" +
				"[[    $block]]");
		SimpleContext tc = new SimpleContext(tt, new Object());

		tc.bind("block",
				"{\n" +
				"  hello\n" +
				"  you\n" +
				"}");
		
		tt.setIndentation("    ");
				
		String nl = System.getProperty("line.separator");
		assertEquals(
				"        {" + nl +
				"          hello" + nl +
				"          you" + nl +
				"        }", tc.expand("foo"));
	}
	
	/**
	 * Tests line splicing
	 * @throws SyntaxError
	 */
	@Test
	public void testLineSplicing_1() throws SyntaxError {
		TinyTemplate tt = new TinyTemplate(
				"foo[[    \\\n" +
				"$boo\\\n" +
				"\n" +
				"]]");
		SimpleContext tc = new SimpleContext(tt, new Object());

		tc.bind("boo",
				"{\n" +
				"  hello\\\n" +
				"  you\n" +
				"}");
		
		tt.setIndentation(" ");
				
		String nl = System.getProperty("line.separator");
		assertEquals(
				"  {" + nl +
				"    hello\\" + nl +
				"    you" + nl +
				"  }" + nl, tc.expand("foo"));
	}
}
