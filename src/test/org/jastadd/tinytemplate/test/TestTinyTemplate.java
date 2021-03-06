/* Copyright (c) 2013, Jesper Öqvist <jesper.oqvist@cs.lth.se>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its
 * contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package org.jastadd.tinytemplate.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.jastadd.tinytemplate.SimpleContext;
import org.jastadd.tinytemplate.TemplateParser.SyntaxError;
import org.jastadd.tinytemplate.TinyTemplate;
import org.junit.Test;

/**
 * General unit tests for tinytemplate
 * @author Jesper Öqvist <jesper.oqvist@cs.lth.se>
 */
public class TestTinyTemplate {

  private static final String NL = System.getProperty("line.separator");

  /**
   * Missing end of template body
   */
  @Test
  public void testBrackets_1() {
    try {
      new TinyTemplate("x = [[  ");
      fail("Expected syntax error!");
    } catch (SyntaxError e) {
      assertEquals("Syntax error at line 1: unexpected end of input while parsing template body", e.getMessage());
    }
  }

  /**
   * Missing end of template body
   */
  @Test
  public void testBrackets_2() {
    try {
      new TinyTemplate("x = [[  ]");
      fail("Expected syntax error!");
    } catch (SyntaxError e) {
      assertEquals("Syntax error at line 1: unexpected end of input while parsing template body", e.getMessage());
    }
  }

  /**
   * Missing start of template body
   */
  @Test
  public void testBrackets_3() {
    try {
      new TinyTemplate("x = ]]");
      fail("Expected syntax error!");
    } catch (SyntaxError e) {
      assertEquals("Syntax error at line 1: found bracket outside template body: ']'", e.getMessage());
    }
  }

  /**
   * Missing start of template body
   */
  @Test
  public void testBrackets_4() {
    try {
      new TinyTemplate("x = [  ]]");
      fail("Expected syntax error!");
    } catch (SyntaxError e) {
      assertEquals("Syntax error at line 1: found bracket outside template body: '['",
          e.getMessage());
    }
  }

  /**
   * Double brackets are allowed inside template body as long as they are not
   * closing brackets.
   */
  @Test
  public void testBrackets_5() {
    try {
      new TinyTemplate("x = [[ [[ [[[ ]]");
    } catch (SyntaxError e) {
      fail("template parsing failed: " + e.getMessage());
    }
  }

  /**
   * Brackets are not allowed outside of template body. The template body
   * ends at the first pair of closing brackets.
   */
  @Test
  public void testBrackets_6() {
    try {
      new TinyTemplate("x = [[ ]] ]]");
      fail("Expected syntax error!");
    } catch (SyntaxError e) {
      assertEquals("Syntax error at line 1: found bracket outside template body: ']'",
          e.getMessage());
    }
  }

  /**
   * A dollar sign can be used to escape a closing bracket inside
   * the template body.
   * @throws SyntaxError
   */
  @Test
  public void testBrackets_7() throws SyntaxError {
    TinyTemplate tt = new TinyTemplate("x = [[ $] ]$] ]]");
    assertEquals(" ] ]] ", tt.expand("x"));
  }

  /**
   * There can be extra closing brackets at the very end of the template body.
   * @throws SyntaxError
   */
  @Test
  public void testBrackets_8() throws SyntaxError {
    TinyTemplate tt = new TinyTemplate("test [[anArray[]]]");

    assertEquals("anArray[]", tt.expand("test"));
  }

  /**
   * Constructor
   */
  public TestTinyTemplate() {
    TinyTemplate.printWarnings(false);
    TinyTemplate.throwExceptions(false);
  }

  /**
   * Tests expanding an undefined template
   * @throws SyntaxError
   */
  @Test
  public void testUndefinedTemplate() throws SyntaxError {
    TinyTemplate tt = new TinyTemplate("");

    assertEquals("", tt.expand("test"));
  }

  /**
   * Tests expanding a simple template
   * @throws SyntaxError
   */
  @Test
  public void testSimple_1() throws SyntaxError {
    TinyTemplate tt = new TinyTemplate("test [[hello]]");

    assertEquals("hello", tt.expand("test"));
  }

  /**
   * Tests expanding an empty template
   * @throws SyntaxError
   */
  @Test
  public void testSimple_2() throws SyntaxError {
    TinyTemplate tt = new TinyTemplate("foo [[]]");

    assertEquals("", tt.expand("foo"));
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

    assertEquals("z" + NL + NL, tt.expand("x"));
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

    assertEquals(
        "void a() {" + NL +
        "  baa;" + NL +
        "}" + NL +
        NL +
        "void b() {" + NL +
        "  boo(hoo);" + NL +
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

    assertEquals(
        "void m() {" + NL +
        "\t" + NL +
        "\t\t2  " + NL +
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

    assertEquals(
        "        {" + NL +
        "          hello" + NL +
        "          you" + NL +
        "        }", tc.expand("foo"));
  }

  /**
   * Line splicing is not enabled
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

    assertEquals(
        "  \\" + NL +
        "{\n" +
        "  hello\\\n" +
        "  you\n" +
        "}\\" + NL + NL, tc.expand("foo"));
  }

}
