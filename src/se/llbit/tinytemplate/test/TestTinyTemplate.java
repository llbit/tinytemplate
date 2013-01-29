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
}
