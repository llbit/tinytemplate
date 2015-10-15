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

import java.util.ArrayList;
import java.util.List;

import org.jastadd.tinytemplate.fragment.AbstractFragment;
import org.jastadd.tinytemplate.fragment.Fragment;

/**
 * Indentation fragment factory and indentation scheme
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class Indentation {

  /**
   * Indentation fragment
   */
  public static class IndentationFragment extends AbstractFragment {
    protected final int level;

    protected IndentationFragment(int indentLevel) {
      level = indentLevel;
    }

    @Override
    public void expand(TemplateContext context, StringBuilder out) {
      out.append(context.evalIndentation(level));
    }

    @Override
    public boolean isWhitespace() {
      return true;
    }

    @Override
    public boolean isIndentation() {
      return true;
    }
  }

  private final String indentation;
  private final List<String> ind = new ArrayList<String>(32);

  private static final List<Fragment> fragments =
    new ArrayList<Fragment>(32);

  /**
   * Create a new indentation scheme
   * @param indent One level of indentation
   */
  public Indentation(String indent) {
    indentation = indent;
    ind.add("");
  }

  /**
   * @param level The level of indentation
   * @return An indentation fragment for the given indentation level
   */
  public static Fragment getFragment(int level) {
    while (fragments.size() < (level+1)) {
      fragments.add(new IndentationFragment(fragments.size()));
    }
    return fragments.get(level);
  }

  /**
   * @param level The level of indentation
   * @return The indentation string for the given indentation level
   */
  public String get(int level) {
    while (ind.size() < (level+1)) {
      ind.add(ind.get(ind.size()-1) + indentation);
    }
    return ind.get(level);
  }

}
