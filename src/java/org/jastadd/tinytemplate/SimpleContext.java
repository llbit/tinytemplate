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
package org.jastadd.tinytemplate;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple template context
 *
 * @author Jesper Öqvist <jesper.oqvist@cs.lth.se>
 */
public class SimpleContext extends TemplateContext {

  private final TemplateContext parentContext;

  private final Map<String, Object> variables = new HashMap<String, Object>();

  private final Object contextObject;

  /**
   * Create a new simple context
   * @param parent The parent context
   * @param context The context object
   */
  public SimpleContext(TemplateContext parent, Object context) {
    parentContext = parent;
    contextObject = context;
  }

  @Override
  public Object evalVariable(String varName) {
    Object var = variables.get(varName);
    if (var != null) {
      return var;
    } else {
      return parentContext.evalVariable(varName);
    }
  }

  @Override
  public Object evalAttribute(String attribute) {
    return TinyTemplate.evalAttribute(attribute, contextObject);
  }

  @Override
  public String evalIndentation(int levels) {
    return parentContext.evalIndentation(levels);
  }

  @Override
  public void expand(TemplateContext tc, String templateName, PrintStream out) {
    parentContext.expand(tc, templateName, out);
  }

  @Override
  public void expand(TemplateContext tc, String templateName, PrintWriter out) {
    parentContext.expand(tc, templateName, out);
  }

  @Override
  public void expand(TemplateContext tc, String templateName, StringBuffer out) {
    parentContext.expand(tc, templateName, out);
  }

  @Override
  public void expand(TemplateContext tc, String templateName, StringBuilder out) {
    parentContext.expand(tc, templateName, out);
  }

  @Override
  public void flushVariables() {
    variables.clear();
  }

  @Override
  public void bind(String varName, Object value) {
    variables.put(varName, value);
  }
}
