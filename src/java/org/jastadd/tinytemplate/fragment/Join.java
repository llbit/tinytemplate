/* Copyright (c) 2013, Niklas Fors <niklas.fors@cs.lth.se>
 *               2013, Jesper Öqvist <jesper.oqvist@cs.lth.se>
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

import org.jastadd.tinytemplate.TemplateContext;
import org.jastadd.tinytemplate.TemplateExpansionWarning;
import org.jastadd.tinytemplate.TemplateParser.SyntaxError;

/**
 * A string joining template fragment. Joins an iterable list of objects with
 * an optional separator string.
 *
 * @author Niklas Fors <niklas.fors@cs.lth.se>
 */
public class Join extends NestedIndentationFragment {
  protected String iterable;
  protected final String sep;
  protected boolean isAttribute;

  /**
   * @param iterable
   * @throws SyntaxError
   */
  public Join(String iterable) throws SyntaxError {
    this(iterable, null);
  }

  /**
   * @param iterable
   * @param separator
   * @throws SyntaxError
   */
  public Join(String iterable, String separator) throws SyntaxError {
    if (iterable.startsWith("#")) {
      this.iterable = iterable.substring(1);
      isAttribute = true;
    } else {
      if (iterable.startsWith("$")) {
        this.iterable = iterable.substring(1);
      }
      isAttribute = false;
    }
    if (separator == null) {
      throw new NullPointerException("Separator must be non-null.");
    }
    this.sep = separator;
  }

  @Override
  public void expand(TemplateContext context, StringBuilder out) {
    if (isAttribute) {
      expandAttribute(context, out);
    } else {
      expandVariable(context, out);
    }
  }

  private void expandAttribute(TemplateContext context, StringBuilder out) {
    Object value = context.evalAttribute(iterable);
    if (value instanceof Iterable) {
      expandIterable(context, out, value);
    } else {
      throw new TemplateExpansionWarning("Attribute '" + iterable + "' is not iterable");
    }
  }

  private void expandVariable(TemplateContext context, StringBuilder out) {
    Object value = context.evalVariable(iterable);
    if (value instanceof Iterable) {
      expandIterable(context, out, value);
    } else {
      throw new TemplateExpansionWarning("Variable '" + iterable + "' is not iterable");
    }
  }

  private void expandIterable(TemplateContext context, StringBuilder out, Object value) {
    Iterable<?> itr = (Iterable<?>) value;
    StringBuilder sb = new StringBuilder();
    boolean first = true;
    for (Object o : itr) {
      if (sep != null && !first) {
        sb.append(sep);
      }
      first = false;
      sb.append(String.valueOf(o));
    }
    expandWithIndentation(sb.toString(), context, out);
  }

  @Override
  public boolean isConditional() {
    return false;
  }

  @Override
  public boolean isExpansion() {
    return true;
  }
}
