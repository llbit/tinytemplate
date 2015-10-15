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
package org.jastadd.tinytemplate.fragment;

import org.jastadd.tinytemplate.EmptyTemplate;
import org.jastadd.tinytemplate.Template;
import org.jastadd.tinytemplate.TemplateContext;
import org.jastadd.tinytemplate.TemplateParser;
import org.jastadd.tinytemplate.TemplateParser.SyntaxError;

/**
 * A conditional expansion
 * @author Jesper Öqvist <jesper.oqvist@cs.lth.se>
 */
public class Conditional extends AbstractFragment {

  protected String condition;
  protected final Template thenPart;
  protected final Template elsePart;
  protected final boolean negated;
  protected final boolean isAttribute;

  /**
   * Create a if-then conditional
   * @param condition
   * @param thenPart
   * @throws SyntaxError
   */
  public Conditional(String condition, Template thenPart) throws SyntaxError {
    this(condition, thenPart, EmptyTemplate.INSTANCE);
  }

  /**
   * Create an if-then-else conditional
   * @param cond
   * @param thenPart
   * @param elsePart
   * @throws SyntaxError
   */
  public Conditional(String cond, Template thenPart, Template elsePart) throws SyntaxError {
    if (cond.startsWith("!")) {
      this.negated = true;
      cond = cond.substring(1).trim();
    } else {
      this.negated = false;
    }
    if (cond.startsWith("#")) {
      this.isAttribute = true;
      this.condition = cond.substring(1);
    } else if (cond.startsWith("$")) {
      this.isAttribute = false;
      this.condition = cond.substring(1);
    } else {
      this.isAttribute = false;
      this.condition = cond;
    }
    if (cond.isEmpty()) {
      throw new SyntaxError("empty if condition");
    }
    if (isAttribute) {
      TemplateParser.acceptAttributeName(-1, this.condition);
    } else {
      TemplateParser.acceptVariableName(-1, this.condition);
    }
    this.thenPart = thenPart;
    this.thenPart.trimTrailingEmptyLine();
    this.elsePart = elsePart;
    this.elsePart.trimTrailingEmptyLine();
  }

  private boolean evalCondition(TemplateContext context) {
    Object value;
    if (isAttribute) {
      value = context.evalAttribute(condition);
    } else {
      value = context.evalVariable(condition);
    }
    boolean result = value.toString().equals("true");
    return negated ? !result : result;
  }

  @Override
  public void expand(TemplateContext context, StringBuilder out) {
    if (evalCondition(context)) {
      thenPart.expand(context, out);
    } else {
      elsePart.expand(context, out);
    }
  }

  @Override
  public boolean isConditional() {
    return true;
  }

  @Override
  public boolean isExpansion() {
    return true;
  }

}
