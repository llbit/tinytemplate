/* Copyright (c) 2013, Jesper Öqvist <jesper@cs.lth.se>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the <organization> nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.jastadd.tinytemplate;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

import org.jastadd.io.LookaheadReader;
import org.jastadd.tinytemplate.fragment.AttributeReference;
import org.jastadd.tinytemplate.fragment.Concat;
import org.jastadd.tinytemplate.fragment.Conditional;
import org.jastadd.tinytemplate.fragment.EmptyFragment;
import org.jastadd.tinytemplate.fragment.Fragment;
import org.jastadd.tinytemplate.fragment.Include;
import org.jastadd.tinytemplate.fragment.VariableReference;

/**
 * Parses template files
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class TemplateParser {

	private final FragmentBuilder builder;

	/**
	 * Thrown when there is a syntax error a parsed template file
	 */
	@SuppressWarnings("serial")
	public static class SyntaxError extends Exception {
		/**
		 * @param line Line where the error occurred
		 * @param msg Error message
		 */
		public SyntaxError(int line, String msg) {
			super("Syntax error at line " + line + ": " + msg);
		}
		/**
		 * @param msg Error message
		 */
		public SyntaxError(String msg) {
			super(msg);
		}
	}

	private final TinyTemplate templates;
	private final LookaheadReader in;
	private int line = 1;

	/**
	 * @param tt
	 * @param is
	 */
	public TemplateParser(TinyTemplate tt, InputStream is) {
		this(tt, is, FragmentBuilder.DEFAULT_BUILDER);
	}

	/**
	 * @param tt
	 * @param is
	 * @param builder fragment builder to construct the AST
	 */
	public TemplateParser(TinyTemplate tt, InputStream is, FragmentBuilder builder) {
		this.builder = builder;
		templates = tt;
		in = new LookaheadReader(is, 8);
	}

	/**
	 * @throws SyntaxError Indicates that there occurred a syntax error during
	 * parsing
	 */
	public void parse() throws SyntaxError {
		try {
			while (in.peek() != -1) {
				parseTemplates();
			}
		} catch (IOException e) {
			throw new SyntaxError("IO error during template parsing: " +
					e.getMessage());
		}
	}

	private void parseTemplates() throws IOException, SyntaxError {
		Collection<String> names = new LinkedList<String>();
		while (true) {

			skipWhitespace();

			if (isEOF()) {
				if (!names.isEmpty()) {
					throw new SyntaxError(line,
							"missing template body at end of file");
				}
				break;
			} else if (isNewline()) {
				skipNewline();
			} else if (isLineComment()) {
				skipLinecomment();
			} else if (isAssign()) {
				if (names.isEmpty()) {
					throw new SyntaxError(line, "misplaced '='");
				}

				// skip the =
				in.pop();
			} else if (isTemplateStart()) {
				if (names.isEmpty()) {
					throw new SyntaxError(line, "missing template name");
				}

				Template template = parseTemplate();
				for (String name: names) {
					templates.addTemplate(name, template);
				}
				names.clear();
			} else if (in.peek() == '[' || in.peek() == ']') {
				throw new SyntaxError(line,
						"found bracket outside template body: '"
						+ ((char) in.peek()) + "'");
			} else {
				names.add(nextName());
			}
		}
	}

	private void skipLinecomment() throws IOException {
		in.pop();
		while (!isNewline() && !isEOF()) {
			in.pop();
		}
	}

	/**
	 * Skips non-newline whitespace
	 * @throws IOException
	 */
	private void skipWhitespace() throws IOException {
		while (isWhitespace()) {
			in.pop();
		}
	}

	private void skipIndentation() throws IOException {
		in.pop();
		in.pop();
	}

	private boolean isTemplateStart() throws IOException, SyntaxError {
		return in.peek() == '[' && in.peek(1) == '[';
	}

	private boolean isIndentation() throws IOException {
		return in.peek() == ' ' && in.peek(1) == ' ';
	}

	private boolean isTemplateEnd() throws IOException {
		return in.peek() == ']' && in.peek(1) == ']' && in.peek(2) != ']';
	}

	private boolean isAssign() throws IOException {
		return in.peek() == '=';
	}

	private boolean isLineComment() throws IOException {
		return in.peek() == '#';
	}

	private boolean isWhitespace() throws IOException {
		return Character.isWhitespace(in.peek()) && !isNewline();
	}

	private boolean isNewline() throws IOException {
		return isNewline(in.peek());
	}

	private boolean isNewline(int ch) throws IOException {
		return ch == '\n' || ch == '\r';
	}

	private boolean isEOF() throws IOException {
		return in.peek() == -1;
	}

	private void skipNewline() throws IOException {
		if (in.peek() == '\\') {
			in.pop();
		}
		if (in.peek() == '\r') {
			if (in.peek(1) == '\n') {
				in.pop();
			}
		}
		in.pop();

		line += 1;
	}

	private String nextName() throws IOException, SyntaxError {
		StringBuilder name = new StringBuilder();
		while (!isWhitespace() && !isAssign() && !isTemplateStart() &&
				!isEOF()) {

			name.append((char) in.pop());
		}
		return name.toString();
	}

	/**
	 * Parse a single template declaration.
	 * @return parsed template
	 * @throws IOException
	 * @throws SyntaxError
	 */
	public Template parseSingleTemplate() throws IOException, SyntaxError {

		Template template = builder.template();
		boolean newLine = true;
		while (!isEOF()) {
			Fragment nextFragment = nextFragment(template, newLine);
			if (!nextFragment.isEmpty()) {
				if (nextFragment.isKeyword("else"))
					throw new SyntaxError(line, "stray $else");
				else if (nextFragment.isKeyword("endif"))
					throw new SyntaxError(line, "stray $endif");
				newLine = nextFragment.isNewline();
				template.addFragment(nextFragment);
			} else {
				throw new SyntaxError(line, "unexpected end of template marker");
			}
		}

		return template;
	}

	private Template parseTemplate() throws IOException, SyntaxError {

		// skip [[
		in.consume(2);

		Template template = builder.template();
		boolean newLine = true;
		while (true) {
			if (isEOF()) {
				throw new SyntaxError(line,
						"unexpected end of input while parsing template body");
			}

			Fragment nextFragment = nextFragment(template, newLine);
			if (!nextFragment.isEmpty()) {
				if (nextFragment.isKeyword("else"))
					throw new SyntaxError(line, "stray $else");
				else if (nextFragment.isKeyword("endif"))
					throw new SyntaxError(line, "stray $endif");
				newLine = nextFragment.isNewline();
				template.addFragment(nextFragment);
			} else {
				break;
			}
		}

		// skip ]]
		in.consume(2);

		template.trim();
		return template;
	}

	private Fragment nextFragment(Template template, boolean newLine)
			throws IOException, SyntaxError {

		while (true) {
			if (newLine) {
				int levels = 0;
				while (isIndentation()) {
					skipIndentation();
					levels += 1;
				}
				if (levels > 0) {
					return builder.indentation(levels);
				}
			}

			if (isKeyword("if")) {
				return parseIfStmt();
			} else if (isKeyword("include")) {
				Include include = parseIncludeStmt();
				template.addIndentation(include);
				return include;
			} else if (isKeyword("cat")) {
				Concat cat = parseConcatStmt();
				template.addIndentation(cat);
				return cat;
			} else if (isVariable()) {
				String var = nextReference();
				if (var.isEmpty()) {
					throw new SyntaxError(line, "empty variable name");
				}
				acceptVariableName(line, var);
				VariableReference ref = builder.variable(var);
				template.addIndentation(ref);
				return ref;
			} else if (isAttribute()) {
				String attr = nextReference();
				if (attr.isEmpty()) {
					throw new SyntaxError(line, "empty attribute name");
				}
				acceptAttributeName(line, attr);
				AttributeReference ref = builder.attribute(attr);
				template.addIndentation(ref);
				return ref;
			} else if (isNewline()) {
				skipNewline();
				return builder.newline();
			} else if (isTemplateEnd()) {
				return EmptyFragment.INSTANCE;
			} else {
				return builder.string(nextString());
			}
		}
	}

	/**
	 * @param keyword
	 * @return <code>true</code> if the next token matches the keyword
	 * @throws IOException
	 */
	private boolean isKeyword(String keyword) throws IOException {
		if (in.peek() != '$' && in.peek() != '#') {
			return false;
		}
		for (int i = 0; i < keyword.length(); ++i) {
			if (in.peek(i+1) != keyword.charAt(i)) {
				return false;
			}
		}
		return true;
	}

	private Conditional parseIfStmt() throws IOException, SyntaxError {
		// consume '$if'
		in.consume(3);
		String condition = parseCondition();
		Template thenPart = builder.template();
		Template elsePart = builder.template();
		Template part = thenPart;

		boolean newLine = true;
		boolean haveElse = false;
		while (true) {
			if (isEOF()) {
				throw new SyntaxError(line,
						"unexpected end of input while parsing if statement");
			}

			Fragment nextFragment = nextFragment(part, newLine);
			if (!nextFragment.isEmpty()) {
				if (nextFragment.isKeyword("else")) {
					if (haveElse) {
						throw new SyntaxError(line, "too many $else");
					}
					haveElse = true;
					part = elsePart;
				} else if (nextFragment.isKeyword("endif")) {
					break;
				} else {
					newLine = nextFragment.isNewline();
					part.addFragment(nextFragment);
				}
			} else {
				throw new SyntaxError(line, "missing $endif");
			}
		}

		thenPart.trim();
		elsePart.trim();

		return builder.conditional(condition, thenPart, elsePart);
	}

	private Concat parseConcatStmt() throws IOException, SyntaxError {
		in.consume(4);
		skipWhitespace();
		if (in.peek() != '(') {
			throw new SyntaxError(line, "missing cat parameters");
		} else {
			in.pop();
			skipWhitespace();
			String iterable;
			if (in.peek() == '#') {
				in.pop();
				iterable = "#";
			} else {
				if (in.peek() == '$') {
					in.pop();
				}
				iterable = "$";
			}
			iterable += parseSimpleReference();

			skipWhitespace();

			String sep = "";
			if (in.peek() == ',') {
				in.pop();
				skipWhitespace();
				sep = parseStringLiteral();
			}

			accept(')');
			return builder.cat(iterable, sep);
		}
	}

	private char accept(char c) throws SyntaxError, IOException {
		return acceptAlternatives(c);
	}

	private char acceptAlternatives(char ...cs) throws IOException, SyntaxError {
		for (char c: cs) {
			if (in.peek() == c) {
				in.pop();
				return c;
			}
		}
		throw new SyntaxError(line, "wanted: " + Arrays.toString(cs) + ", got: " + (char) in.peek());
	}

	private Include parseIncludeStmt() throws IOException, SyntaxError {
		// consume '$include'
		in.consume(8);
		skipWhitespace();
		if (in.peek() != '(') {
			throw new SyntaxError(line, "missing template name");
		} else {
			String template = parseParenthesizedReference().trim();
			return builder.include(template);
		}
	}

	private String parseCondition() throws IOException, SyntaxError {
		skipWhitespace();

		if (in.peek() != '(') {
			throw new SyntaxError(line, "missing if condition");
		} else {
			return parseParenthesizedReference().trim();
		}
	}

	private String parseStringLiteral() throws IOException, SyntaxError {
		StringBuilder sb = new StringBuilder();
		accept('"');

		boolean escaped = false;
		while (!isStringLiteralEnd(escaped)) {
			escaped = in.peek() == '\\' && in.peek(1) == '"';
			sb.append((char) in.pop());
		}

		accept('"');
		return sb.toString();
	}

	private boolean isStringLiteralEnd(boolean escaped) throws IOException {
		return isEOF() || (in.peek() == '\"' && !escaped);
	}

	private String nextString() throws IOException, SyntaxError {
		StringBuilder buf = new StringBuilder(512);
		while ( !(isEOF() || isVariable() || isAttribute() || isNewline() ||
				isTemplateEnd()) ) {

			if (in.peek() == '#' || in.peek() == '$') {
				// it's cool - the # or $ was escaped!
				// isAttribute() or isVariable() would have been true if not
				in.pop();
			}

			buf.append((char) in.pop());
		}
		return buf.toString();
	}

	private String nextReference() throws IOException, SyntaxError {
		// skip the # or $
		in.pop();

		if (in.peek() == '(') {
			return parseParenthesizedReference();
		} else {
			return parseSimpleReference();
		}
	}

	private String parseSimpleReference() throws IOException, SyntaxError {
		StringBuilder buf = new StringBuilder(128);
		while (!isSimpleReferenceEnd()) {
			buf.append((char) in.pop());
		}
		return buf.toString();
	}

	private boolean isSimpleReferenceEnd() throws IOException {
		return isEOF()
				|| in.peek() == '$'
				|| !Character.isJavaIdentifierPart(in.peek());
	}

	private boolean isParenthesizedReferenceEnd() throws IOException {
		return isEOF() || isNewline() ||
				in.peek() == '[' || in.peek() == ']';
	}

	private String parseParenthesizedReference() throws IOException, SyntaxError {
		// skip the (
		in.pop();

		StringBuilder buf = new StringBuilder(128);
		int depth = 1;
		while (true) {
			if (isParenthesizedReferenceEnd()) {
				throw new SyntaxError(line, "missing right parenthesis");
			}

			int c = in.pop();

			if (c == '(') {
				depth += 1;
			} else if (c == ')') {
				depth -= 1;
				if (depth == 0) {
					break;
				}
			}

			buf.append((char) c);
		}
		return buf.toString();
	}

	private boolean isVariable() throws IOException {
		// double dollar sign is an escape for single dollar sign
		return in.peek() == '$' && !isEscapable(in.peek(1));
	}

	private boolean isAttribute() throws IOException {
		// double hash is an escape for single hash
		return in.peek() == '#' && in.peek(1) != '#';
	}

	private boolean isEscapable(int chr) {
		return chr == '$' || chr == ']';
	}

	/**
	 * Throws a SyntaxError if the given string was not a valid variable name
	 * @param line
	 * @param var
	 * @throws SyntaxError
	 */
	public static void acceptVariableName(int line, String var) throws SyntaxError {
		for (int i = 0; i < var.length(); ++i) {
			char ch = var.charAt(i);
			if (!Character.isJavaIdentifierPart(ch) && ch != '.') {
				String msg = "illegal characters in variable name '" + var + "'";
				if (line == -1)
					throw new SyntaxError(msg);
				else
					throw new SyntaxError(line, msg);
			}
		}
	}

	/**
	 * Throws a SyntaxError if the given string was not a valid attribute name
	 * @param line
	 * @param attr
	 * @throws SyntaxError
	 */
	public static void acceptAttributeName(int line, String attr) throws SyntaxError {
		for (int i = 0; i < attr.length(); ++i) {
			char ch = attr.charAt(i);
			if ((i == 0 && !Character.isJavaIdentifierStart(ch)) ||
					!Character.isJavaIdentifierPart(ch)) {

				String msg =  "the attribute name '" + attr +
						"' is not a valid Java identifier";
				if (line == -1)
					throw new SyntaxError(msg);
				else
					throw new SyntaxError(line, msg);
			}

		}
	}

}
