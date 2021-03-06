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

import org.jastadd.tinytemplate.TemplateContext;
import org.jastadd.tinytemplate.Indentation.IndentationFragment;

/**
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public abstract class NestedIndentationFragment extends AbstractFragment {

  private IndentationFragment indentation = null;
  private static final String SYS_NL = System.getProperty("line.separator");

  protected void expandWithIndentation(String expansion,
      TemplateContext context, StringBuilder out) {

    if (indentation == null) {
      out.append(expansion);
    } else {
      String[] lines = expansion.split("\n|\r\n?");
      for (int i = 0; i < lines.length; ++i) {
        if (i != 0) {
          indentation.expand(context, out);
        }
        if ((i+1) < lines.length) {
          out.append(lines[i]);
          out.append(SYS_NL);
        } else {
          out.append(lines[i]);
        }
      }
    }
  }

  /**
   * Set the indentation for this reference expansion fragment
   * @param indent
   */
  public void setIndentation(IndentationFragment indent) {
    indentation = indent;
  }

  @Override
  public boolean isExpansion() {
    return true;
  }
}
