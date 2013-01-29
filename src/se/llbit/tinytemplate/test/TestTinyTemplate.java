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
		TinyTemplate tt = new TinyTemplate();
		tt.loadTemplates("");
		
		assertEquals("", tt.expand("test"));
		assertFalse("expand should return false if the template was not expanded",
				tt.expand("test", new PrintStream(new ByteArrayOutputStream())));
	}
	
	@Test
	public void testSimple1() throws SyntaxError {
		TinyTemplate tt = new TinyTemplate();
		tt.loadTemplates("test [[hello]]");
		
		assertEquals("hello", tt.expand("test"));
		assertTrue("expand should return true if the template was expanded",
				tt.expand("test", new PrintStream(new ByteArrayOutputStream())));
	}
	
	@Test
	public void testSimple2() throws SyntaxError {
		TinyTemplate tt = new TinyTemplate();
		tt.loadTemplates("test = [[$hello]]");
		
		tt.bind("hello", "hej");
		assertEquals("hej", tt.expand("test"));
	}
	
	/**
	 * Multiple assign are allowed between template name and template body
	 * @throws SyntaxError
	 */
	@Test
	public void testMultiAssign1() throws SyntaxError {
		TinyTemplate tt = new TinyTemplate();
		tt.loadTemplates("test == == [[$hello]]");
		
		tt.bind("hello", "hej");
		assertEquals("hej", tt.expand("test"));
	}
	
	/**
	 * Multiple template names are allowed for each template
	 * @throws SyntaxError
	 */
	@Test
	public void testMultiAssign2() throws SyntaxError {
		TinyTemplate tt = new TinyTemplate();
		tt.loadTemplates("test == foo = = = bar [[$hello]]");
		
		tt.bind("hello", "hej");
		assertEquals("hej", tt.expand("test"));
		assertEquals("hej", tt.expand("foo"));
		assertEquals("hej", tt.expand("bar"));
	}
	
	@Test
	public void testComment() throws SyntaxError {
		TinyTemplate tt = new TinyTemplate();
		tt.loadTemplates(
				"# line comment\n" +
				"test = [[$hello]]");
		
		tt.bind("hello", "hej");
		assertEquals("hej", tt.expand("test"));
	}
}
