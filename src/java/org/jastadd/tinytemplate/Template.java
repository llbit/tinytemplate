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

import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.jastadd.tinytemplate.Indentation.IndentationFragment;
import org.jastadd.tinytemplate.fragment.Fragment;
import org.jastadd.tinytemplate.fragment.NestedIndentationFragment;

/**
 * Template
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class Template {

  /**
   * NB lines should never be empty!
   */
  protected final List<List<Fragment>> lines = new ArrayList<List<Fragment>>();
  {
    lines.add(new ArrayList<Fragment>());
  }

  /**
   * Expand the template to a PrintStream
   * @param context
   * @param out
   */
  public void expand(TemplateContext context, PrintStream out) {
    StringBuilder buf = new StringBuilder();
    for (List<Fragment> line: lines) {
      expandLine(context, line, buf);
      out.print(buf.toString());
    }
  }

  /**
   * Expand the template to a PrintWriter
   * @param context
   * @param out
   */
  public void expand(TemplateContext context, PrintWriter out) {
    StringBuilder buf = new StringBuilder();
    for (List<Fragment> line: lines) {
      expandLine(context, line, buf);
      out.print(buf.toString());
    }
  }

  /**
   * Expand the template to a StringBuffer
   * @param context
   * @param out
   */
  public void expand(TemplateContext context, StringBuffer out) {
    StringBuilder buf = new StringBuilder();
    for (List<Fragment> line: lines) {
      expandLine(context, line, buf);
      out.append(buf.toString());
    }
  }

  /**
   * Expand the template to a StringBuilder
   * @param context
   * @param out
   */
  public void expand(TemplateContext context, StringBuilder out) {
    StringBuilder buf = new StringBuilder();
    for (List<Fragment> line: lines) {
      expandLine(context, line, buf);
      out.append(buf.toString());
    }
  }

  /**
   * Expand a single template line
   * @param context
   * @param line
   * @param buf
   */
  protected void expandLine(TemplateContext context, List<Fragment> line, StringBuilder buf) {
    buf.setLength(0);
    boolean expanded = false;
    for (Fragment fragment : line) {
      expanded |= fragment.isExpansion();
      fragment.expand(context, buf);
    }
    // Non-empty lines that become empty after expansion are deleted.
    if (expanded && isEmptyLine(buf)) {
      buf.setLength(0);
    }
  }

  private boolean isEmptyLine(StringBuilder buf) {
    for (int i = 0; i < buf.length(); ++i) {
      if (!Character.isWhitespace(buf.charAt(i))) {
        return false;
      }
    }
    return true;
  }

  /**
   * Load templates from file
   * @param in
   */
  public static void load(InputStream in) {
  }

  /**
   * Add the indentation of the last line to a fragment
   * @param fragment
   */
  public void addIndentation(NestedIndentationFragment fragment) {
    List<Fragment> lastLine = lines.get(lines.size()-1);
    if (!lastLine.isEmpty() && lastLine.get(0).isIndentation()) {
      fragment.setIndentation((IndentationFragment) lastLine.get(0));
    }
  }

  /**
   * Add a fragment to the last line
   * @param fragment
   */
  public void addFragment(Fragment fragment) {
    lines.get(lines.size()-1).add(fragment);
    if (fragment.isNewline()) {
      lines.add(new ArrayList<Fragment>());
    }
  }

  /**
   * Trims first line from the template if it contains only whitespace.
   * Trims leading and trailing whitespace surrounding conditionals that
   * are alone on their line.
   */
  public void trim() {
    trimLeadingEmptyLine();
    trimConditionalWhitespace();
  }

  /**
   * Trim the first line from the template if it contains only whitespace
   */
  private void trimLeadingEmptyLine() {
    trimLineIfEmpty(0);
  }

  /**
   * Trim the last line from the template if it contains only whitespace
   */
  public void trimTrailingEmptyLine() {
    trimLineIfEmpty(lines.size()-1);
  }

  /**
   * Remove the given line if it contains only whitespace
   * @param line Index of line to trim
   */
  private void trimLineIfEmpty(int line) {
    for (Fragment fragment: lines.get(line)) {
      if (!fragment.isWhitespace()) {
        return;
      }
    }
    lines.remove(line);
    if (line > 0) {
      // remove newline from previous line
      List<Fragment> prevLine = lines.get(line-1);
      int last = prevLine.size()-1;
      if (last >= 0 && prevLine.get(last).isNewline()) {
        prevLine.remove(last);
        if (last == 0) {
          lines.remove(line-1);
        }
      }
    }

    if (lines.isEmpty()) {
      // Lines must not be empty.
      lines.add(new ArrayList<Fragment>());
    }
  }

  /**
   * Trim leading and trailing whitespace around conditionals surrounded
   * by whitespace on their line.
   */
  private void trimConditionalWhitespace() {
    for (int i = 0; i < lines.size(); ++i) {
      boolean trimmable = true;
      boolean hasCond = false;
      for (Fragment fragment: lines.get(i)) {
        if ((!fragment.isWhitespace() && !fragment.isConditional())
            || fragment.isConditional() && hasCond) {
          trimmable = false;
          break;
        }
        hasCond |= fragment.isConditional();
      }
      List<Fragment> line = trimLine(lines.get(i), trimmable && hasCond);
      lines.set(i, line);
    }
  }

  private List<Fragment> trimLine(List<Fragment> line, boolean trimmable) {
    List<Fragment> tmp = new ArrayList<Fragment>(lines.size());
    for (Fragment frag: line) {
      if (!trimmable || !frag.isWhitespace() || frag.isNewline()) {
        tmp.add(frag);
      }
    }
    return tmp;
  }

  /**
   * @return <code>true</code> if the template expands to an empty string
   */
  public boolean isEmpty() {
    return lines.size() == 1 && lines.get(0).size() == 0;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    expand(new DumbContext(), sb);
    return sb.toString();
  }
}
