/* Copyright (c) 2013-2014, Jesper Öqvist <jesper.oqvist@cs.lth.se>
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

import static org.junit.Assert.*;

import org.jastadd.tinytemplate.SimpleContext;
import org.jastadd.tinytemplate.TemplateExpansionWarning;
import org.jastadd.tinytemplate.TinyTemplate;
import org.jastadd.tinytemplate.TemplateParser.SyntaxError;
import org.jastadd.tinytemplate.test.mock.MThrowsRuntimeException;
import org.junit.Test;

/**
 * Tests template expansion warnings
 * @author Jesper Öqvist <jesper.oqvist@cs.lth.se>
 */
public class TestWarnings {

  /**
   * Constructor
   */
  public TestWarnings() {
    TinyTemplate.printWarnings(false);
    TinyTemplate.throwExceptions(true);
  }

  /**
   * Tests expanding an undefined template
   * @throws SyntaxError
   */
  @Test
  public void testUndefinedTemplate_1() throws SyntaxError {
    try {
      new TinyTemplate("").expand("test");
      fail("Expected template expansion warning!");
    } catch (TemplateExpansionWarning e) {
      assertEquals("Template expansion warning: unknown template 'test'", e.getMessage());
    }
  }

  /**
   * Tests expanding an unbound variable
   * @throws SyntaxError
   */
  @Test
  public void testUnboundVariable_1() throws SyntaxError {
    try {
      new TinyTemplate("test[[$a]]").expand("test");
      fail("Expected template expansion warning!");
    } catch (TemplateExpansionWarning e) {
      assertEquals("Template expansion warning: " +
        "while expanding template 'test': " +
        "unbound variable 'a'",
        e.getMessage());
    }
  }

  /**
   * Tests expanding an unbound variable
   * @throws SyntaxError
   */
  @Test
  public void testUnboundVariable_2() throws SyntaxError {
    try {
      new TinyTemplate("test1[[$include(test2)]]test2[[$a]]").expand("test1");
      fail("Expected template expansion warning!");
    } catch (TemplateExpansionWarning e) {
      assertEquals("Template expansion warning: " +
        "while expanding template 'test1': " +
        "while expanding template 'test2': " +
        "unbound variable 'a'",
        e.getMessage());
    }
  }

  /**
   * Tests expanding an unbound variable
   * @throws SyntaxError
   */
  @Test
  public void testInvocationTargetException_1() throws SyntaxError {
    try {
      TinyTemplate tt = new TinyTemplate("test[[#m]]");
      SimpleContext tc = new SimpleContext(tt, new MThrowsRuntimeException());
      tc.expand("test");
      fail("Expected template expansion warning!");
    } catch (TemplateExpansionWarning e) {
      assertEquals("Template expansion warning: " +
        "while expanding template 'test': " +
        "failed to eval attribute 'm'; reason: " +
        "invocation target exception (org.jastadd.tinytemplate.test.mock.MThrowsRuntimeException.m())",
        e.getMessage());
    }
  }
}
