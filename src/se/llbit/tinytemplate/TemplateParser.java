/* Copyright (c) 2013 Jesper Öqvist <jesper@llbit.se>
 *
 * This file is part of tinytemplate.
 *
 * tinytemplate is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * tinytemplate is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with tinytemplate.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.llbit.tinytemplate;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.LinkedList;

import se.llbit.io.LookaheadReader;

/**
 * Parses template files
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class TemplateParser {

	/**
	 * Thrown when there is a syntax error a parsed template file
	 */
	@SuppressWarnings("serial")
	public class SyntaxError extends Exception {
		/**
		 * @param line Line where the error occurred
		 * @param msg Error message
		 */
		public SyntaxError(int line, String msg) {
			super("Parse error at line " + line + ": " + msg);
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
		templates = tt;
		in = new LookaheadReader(is, 2);
	}
	
	/**
	 * @throws SyntaxError Indicates that there occurred a syntax error during
	 * parsing
	 */
	public void parse() throws SyntaxError {
		try {
			while (in.peek(0) != -1) {
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
			} else if (isWhitespace()) {
				skipWhitespace();
			} else if (isNewline()) {
				skipNewline();
			} else if (isLinecomment()) {
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
		if (in.peek(0) == '[') {
			if (in.peek(1) == '[') {
				return true;
			} else {
				throw new SyntaxError(line, "misplaced '['");
			}
		} else if (in.peek(0) == ']') {
			throw new SyntaxError(line, "misplaced ']'");
		}
		return false;
	}

	private boolean isIndentation() throws IOException {
		return in.peek(0) == ' ' && in.peek(1) == ' ';
	}

	private boolean isTemplateEnd() throws IOException {
		return in.peek(0) == ']' && in.peek(1) == ']';
	}

	private boolean isAssign() throws IOException {
		return in.peek(0) == '=';
	}

	private boolean isLinecomment() throws IOException {
		return in.peek(0) == '#';
	}

	private boolean isWhitespace() throws IOException {
		return Character.isWhitespace(in.peek(0)) && !isNewline();
	}

	private boolean isNewline() throws IOException {
		return in.peek(0) == '\n' || in.peek(0) == '\r';
	}

	private boolean isEOF() throws IOException {
		return in.peek(0) == -1;
	}

	private void skipNewline() throws IOException {
		if (in.peek(0) == '\r') {
			if (in.peek(1) == '\n') {
				in.pop();
			}
		}
		in.pop();
		
		line += 1;
	}
	
	private String nextName() throws IOException, SyntaxError {
		StringBuffer name = new StringBuffer();
		while (!isWhitespace() && !isAssign() && !isTemplateStart() &&
				!isEOF()) {
			
			name.append((char) in.pop());
		}
		return name.toString();
	}

	private Template parseTemplate() throws IOException, SyntaxError {
		
		// skip [[
		in.pop();
		in.pop();
		
		Template template = new Template();

		boolean newLine = true;
		
		while (true) {
			
			if (isEOF()) {
				throw new SyntaxError(line, "unexpected end of file while parsing template body");
			}
			
			if (newLine) {
				int levels = 0;
				while (newLine && isIndentation()) {
					skipIndentation();
					levels += 1;
				}
				template.addIndentation(levels);
				newLine = false;
				continue;
			}

			if (isVariable()) {
				String var = nextReference();
				if (var.isEmpty()) {
					throw new SyntaxError(line, "empty variable name");
				}
				template.addVariableRef(var);
			} else if (isAttribute()) {
				String attr = nextReference();
				if (attr.isEmpty()) {
					throw new SyntaxError(line, "empty attribute name");
				}
				for (int i = 0; i < attr.length(); ++i) {
					char ch = attr.charAt(i);
					if ((i == 0 && !Character.isJavaIdentifierStart(ch)) ||
							!Character.isJavaIdentifierPart(ch)) {
						
						throw new SyntaxError(line, "the attribute " + attr +
								" is not a valid Java identifier");
					}
					
				}
				template.addAttributeRef(attr);
			} else if (isNewline()) {
				template.addNewline();
				newLine = true;
				skipNewline();
			} else if (isTemplateEnd()) {
				// skip ]]
				in.pop();
				in.pop();
				break;
			} else {
				template.addString(nextString());
			}
		}
	
		return template;
	}

	private String nextString() throws IOException, SyntaxError {
		StringBuffer buf = new StringBuffer(512);
		while ( !(isEOF() || isVariable() || isAttribute() || isNewline() ||
				isTemplateEnd()) ) {
			
			if (in.peek(0) == '[' && in.peek(1) == '[')
				throw new SyntaxError(line, "double brackets are not allowed inside templates");
			
			if (in.peek(0) == '#' || in.peek(0) == '$') {
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
		
		if (in.peek(0) == '(') {
			return parseParens();
		} else {
			return parseSimpleReference();
		}
	}

	private String parseSimpleReference() throws IOException {
		StringBuffer buf = new StringBuffer(128);
		while ( !isEOF() && Character.isJavaIdentifierPart(in.peek(0)) ) {
			buf.append((char) in.pop());
		}
		return buf.toString();
	}

	private String parseParens() throws IOException, SyntaxError {
		// skip the (
		in.pop();
		
		StringBuffer buf = new StringBuffer(128);
		int depth = 1;
		while (true) {
			if (isEOF()) {
				throw new SyntaxError(line, "EOF before end of parenthesized reference");
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
		return in.peek(0) == '$' && in.peek(1) != '$';
	}

	private boolean isAttribute() throws IOException {
		// double hash is an escape for single hash
		return in.peek(0) == '#' && in.peek(1) != '#';
	}

}
