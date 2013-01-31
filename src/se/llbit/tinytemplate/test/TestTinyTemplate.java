package se.llbit.tinytemplate.test;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Test;

import se.llbit.tinytemplate.TemplateParser.SyntaxError;
import se.llbit.tinytemplate.TinyTemplate;

@SuppressWarnings("javadoc")
public class TestTinyTemplate {
	
	@Test
	public void testUndefinedTemplate() throws SyntaxError {
		TinyTemplate tt = new TinyTemplate("");
		
		assertEquals("", tt.expand("test"));
		assertFalse("expand returns false if the template was not expanded",
				tt.expand("test", new PrintStream(new ByteArrayOutputStream())));
	}
	
	@Test
	public void testSimple_1() throws SyntaxError {
		TinyTemplate tt = new TinyTemplate("test [[hello]]");
		
		assertEquals("hello", tt.expand("test"));
		assertTrue("expand returns true if the template was expanded",
				tt.expand("test", new PrintStream(new ByteArrayOutputStream())));
	}
	
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
		assertEquals(nl + "z" + nl + nl, tt.expand("x"));
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
	 * Tests a template variable
	 * @throws SyntaxError
	 */
	@Test
	public void testVariable_1() throws SyntaxError {
		TinyTemplate tt = new TinyTemplate("test = [[$hello]]");
		
		tt.bind("hello", "hej");
		assertEquals("hej", tt.expand("test"));
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
	 * Java identifier characters and periods can be used in variable names
	 * parenthesized.
	 * @throws SyntaxError
	 */
	@Test
	public void testVariable_4() throws SyntaxError {
		TinyTemplate tt = new TinyTemplate("foo = [[$(_.00wat1..)]]");
		
		tt.bind("_.00wat1..", "batman");
		assertEquals("batman", tt.expand("foo"));
	}
	
	/**
	 * Line endings in variable expansions are preserved as-is if the
	 * expansion is not indented
	 * @throws SyntaxError
	 */
	@Test
	public void testVariable_5() throws SyntaxError {
		TinyTemplate tt = new TinyTemplate("foo = [[$block]]");

		tt.bind("block",
				"{\n" +
				"  hello\r" +
				"  you\r\n" +
				"}");
				
		assertEquals(
				"{\n" +
				"  hello\r" +
				"  you\r\n" +
				"}", tt.expand("foo"));
	}
	
	/**
	 * Line endings in variable expansions are replaced by the current
	 * system's default line ending when the expansion is indented
	 * @throws SyntaxError
	 */
	@Test
	public void testVariable_6() throws SyntaxError {
		TinyTemplate tt = new TinyTemplate("foo = [[  x$block]]");

		tt.bind("block",
				"{\n" +
				"  hello\r" +
				"  you\r\n" +
				"}");
				
		String nl = System.getProperty("line.separator");
		assertEquals(
				"  x{" + nl +
				"    hello" + nl +
				"    you" + nl +
				"  }", tt.expand("foo"));
	}
	
	/**
	 * Attribute evaluation calls the attribute method on the context object
	 * @throws SyntaxError
	 */
	@Test
	public void testAttribute_1() throws SyntaxError {
		TinyTemplate tt = new TinyTemplate("foo = [[#toString]]");
		
		tt.pushContext("the string");
		assertEquals("the string", tt.expand("foo"));
	}
	
	/**
	 * Attempting to evaluate an attribute on an object with no such method
	 * @throws SyntaxError
	 */
	@Test
	public void testAttribute_2() throws SyntaxError {
		TinyTemplate tt = new TinyTemplate("foo = [[#imaginaryMethod]]");
		
		tt.pushContext("the string");
		assertEquals("<failed to eval imaginaryMethod; reason: no such method>", tt.expand("foo"));
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
		
		tt.bind("hello", "hej");
		assertEquals("hej", tt.expand("test"));
	}
	
	/**
	 * Multiple template names are allowed for each template
	 * @throws SyntaxError
	 */
	@Test
	public void testSynonyms_1() throws SyntaxError {
		TinyTemplate tt = new TinyTemplate(
				"test == foo = = = bar [[$hello]]");
		
		tt.bind("hello", "hej");
		assertEquals("hej", tt.expand("test"));
		assertEquals("hej", tt.expand("foo"));
		assertEquals("hej", tt.expand("bar"));
	}
	
	/**
	 * Multiple template names are allowed for each template
	 * @throws SyntaxError
	 */
	@Test
	public void testSynonyms_2() throws SyntaxError {
		TinyTemplate tt = new TinyTemplate(
				"test foo bar [[$hello]]");
		
		tt.bind("hello", "hej");
		assertEquals("hej", tt.expand("test"));
		assertEquals("hej", tt.expand("foo"));
		assertEquals("hej", tt.expand("bar"));
	}
	
	@Test
	public void testComment_1() throws SyntaxError {
		TinyTemplate tt = new TinyTemplate(
				"# line comment\n" +
				"test = [[$hello]]");
		
		tt.bind("hello", "hej");
		assertEquals("hej", tt.expand("test"));
	}
	
	@Test
	public void testComment_2() throws SyntaxError {
		TinyTemplate tt = new TinyTemplate(
				"foo=[[x]]# comment\n" +
				"# test = [[y]]");
		
		assertEquals("", tt.expand("test"));
		assertEquals("x", tt.expand("foo"));
	}
	
	@Test
	public void testComment_3() throws SyntaxError {
		TinyTemplate tt = new TinyTemplate(
				"foo=[[## not a comment]]");
		
		assertEquals("hash signs inside a template body are not comments",
				"# not a comment", tt.expand("foo"));
	}
	
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

		tt.bind("block",
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
				"        }", tt.expand("foo"));
	}
	
}
