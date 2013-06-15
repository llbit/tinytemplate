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
package org.jastadd.tinytemplate.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.jastadd.tinytemplate.SimpleContext;
import org.jastadd.tinytemplate.TemplateParser.SyntaxError;
import org.jastadd.tinytemplate.TinyTemplate;
import org.junit.Test;

/**
 * Tests for conditional statements
 * @author Jesper Öqvist <jesper.oqvist@cs.lth.se>
 */
public class TestConditionals {

	private static final String NL = System.getProperty("line.separator");

	/**
	 * Constructor
	 */
	public TestConditionals() {
		TinyTemplate.printWarnings(false);
		TinyTemplate.throwExceptions(false);
	}

	/**
	 * Test alternate form of if-then-else with hash sign instead of dollar sign
	 * @throws SyntaxError
	 */
	@Test
	public void testAlternate_1() throws SyntaxError {
		TinyTemplate tt = new TinyTemplate("foo = [[#if(cond)boo!#(else)mjau#endif]]");
		SimpleContext tc = new SimpleContext(tt, new Object());

		tc.bind("cond", "");

		assertEquals("mjau", tc.expand("foo"));
	}

	/**
	 * Test an attribute condition
	 * @throws SyntaxError
	 */
	@Test
	public void testAttributeCondition_1() throws SyntaxError {
		TinyTemplate tt = new TinyTemplate("dog = [[$if(#toString)Woof!$endif]]");
		SimpleContext tc = new SimpleContext(tt, "true");
		assertEquals("Woof!", tc.expand("dog"));
	}

	/**
	 * Test an attribute condition
	 * @throws SyntaxError
	 */
	@Test
	public void testAttributeCondition_2() throws SyntaxError {
		TinyTemplate tt = new TinyTemplate("dog = [[$if(#toString)Woof!$endif]]");
		SimpleContext tc = new SimpleContext(tt, "false");
		assertEquals("", tc.expand("dog"));
	}

	/**
	 * Test a negated attribute condition
	 * @throws SyntaxError
	 */
	@Test
	public void testAttributeCondition_3() throws SyntaxError {
		TinyTemplate tt = new TinyTemplate("dog = [[$if(!#toString)Woof!$endif]]");
		SimpleContext tc = new SimpleContext(tt, "false");
		assertEquals("Woof!", tc.expand("dog"));
	}

	/**
	 * Test the if-then conditional
	 * @throws SyntaxError
	 */
	@Test
	public void testConditional_1() throws SyntaxError {
		TinyTemplate tt = new TinyTemplate("foo = [[$if(cond)boo!$endif]]");
		SimpleContext tc = new SimpleContext(tt, new Object());

		tc.bind("cond", "true");

		assertEquals("boo!", tc.expand("foo"));
	}

	/**
	 * Test the if-then conditional
	 * @throws SyntaxError
	 */
	@Test
	public void testConditional_2() throws SyntaxError {
		TinyTemplate tt = new TinyTemplate("foo = [[$if(cond)boo!$endif]]");
		SimpleContext tc = new SimpleContext(tt, new Object());

		tc.bind("cond", "not true");

		assertEquals("", tc.expand("foo"));
	}

	/**
	 * Case sensitive "true"
	 * @throws SyntaxError
	 */
	@Test
	public void testConditional_3() throws SyntaxError {
		TinyTemplate tt = new TinyTemplate("foo = [[$if(cond)boo!$endif]]");
		SimpleContext tc = new SimpleContext(tt, new Object());

		tc.bind("cond", "True");

		assertEquals("", tc.expand("foo"));
	}

	/**
	 * Test the if-then-else conditional
	 * @throws SyntaxError
	 */
	@Test
	public void testConditional_4() throws SyntaxError {
		TinyTemplate tt = new TinyTemplate("foo = [[$if(cond)boo!$(else)mjau$endif]]");
		SimpleContext tc = new SimpleContext(tt, new Object());

		tc.bind("cond", "");

		assertEquals("mjau", tc.expand("foo"));
	}

	/**
	 * Test the if-then-else conditional with negated condition
	 * @throws SyntaxError
	 */
	@Test
	public void testConditional_5() throws SyntaxError {
		TinyTemplate tt = new TinyTemplate("foo = [[$if(!cond)boo!$(else)mjau$endif]]");
		SimpleContext tc = new SimpleContext(tt, new Object());

		tc.bind("cond", "");

		assertEquals("boo!", tc.expand("foo"));
	}

	/**
	 * Test whitespace before the condition
	 * @throws SyntaxError
	 */
	@Test
	public void testConditional_6() throws SyntaxError {
		TinyTemplate tt = new TinyTemplate("dog = [[$if \t (x)Woof!$endif]]");
		SimpleContext tc = new SimpleContext(tt, new Object());

		tc.bind("x", true);

		assertEquals("Woof!", tc.expand("dog"));
	}

	/**
	 * Whitespace is trimmed from the condition string
	 * @throws SyntaxError
	 */
	@Test
	public void testConditional_7() throws SyntaxError {
		TinyTemplate tt = new TinyTemplate("dog = [[$if(\tx )Woof!$endif]]");
		SimpleContext tc = new SimpleContext(tt, new Object());

		tc.bind("x", true);

		assertEquals("Woof!", tc.expand("dog"));
	}

	/**
	 * Whitespace is trimmed from the condition string
	 * @throws SyntaxError
	 */
	@Test
	public void testConditional_8() throws SyntaxError {
		TinyTemplate tt = new TinyTemplate("dog = [[$if(  ! \tx )Woof!$(else)silence$endif]]");
		SimpleContext tc = new SimpleContext(tt, new Object());

		tc.bind("x", true);

		assertEquals("silence", tc.expand("dog"));
	}

	/**
	 * Extra exclamation marks in condition
	 * @throws SyntaxError
	 */
	@Test
	public void testConditional_9() throws SyntaxError {
		try {
			new TinyTemplate("dog = [[$if(!!x)Woof!$(else)silence$endif]]");
			fail("Expected syntax error!");
		} catch (SyntaxError e) {
			assertEquals("illegal characters in variable name '!x'", e.getMessage());
		}
	}

	/**
	 * A dollar sign is allowed for variable conditions
	 * @throws SyntaxError
	 */
	@Test
	public void testConditional_10() throws SyntaxError {
		TinyTemplate tt = new TinyTemplate("dog = [[$if($x)Woof!$endif]]");
		SimpleContext tc = new SimpleContext(tt, this);
		tc.bind("x", "true");
		assertEquals("Woof!", tc.expand("dog"));
	}

	/**
	 * A dollar sign is allowed for variable conditions
	 * @throws SyntaxError
	 */
	@Test
	public void testConditional_11() throws SyntaxError {
		TinyTemplate tt = new TinyTemplate("dog = [[$if(!$x)Woof!$endif]]");
		SimpleContext tc = new SimpleContext(tt, this);
		tc.bind("x", "true");
		assertEquals("", tc.expand("dog"));
	}

	/**
	 * Illegal characters in condition
	 * @throws SyntaxError
	 */
	@Test
	public void testConditionError_1() throws SyntaxError {
		try {
			new TinyTemplate("dog = [[$if(x%)Woof!$(else)silence$endif]]");
			fail("Expected syntax error!");
		} catch (SyntaxError e) {
			assertEquals("illegal characters in variable name 'x%'", e.getMessage());
		}
	}

	/**
	 * Attribute conditions must be legal Java identifiers
	 * @throws SyntaxError
	 */
	@Test
	public void testConditionError_2() throws SyntaxError {
		try {
			new TinyTemplate("dog = [[$if(#x.y)Woof!$(else)silence$endif]]");
			fail("Expected syntax error!");
		} catch (SyntaxError e) {
			assertEquals("the attribute name 'x.y' is not a valid Java identifier", e.getMessage());
		}
	}

	/**
	 * Test nested conditional
	 * @throws SyntaxError
	 */
	@Test
	public void testNested_1() throws SyntaxError {
		TinyTemplate tt = new TinyTemplate(
				"Father = [[" +
				"$if(x) x\n" +
				"$if(y)Wednesday$endif y" +
				"$endif" +
				"]]");
		SimpleContext tc = new SimpleContext(tt, new Object());

		tc.bind("x", "true");
		tc.bind("y", "true");

		assertEquals(" x" + NL + "Wednesday y", tc.expand("Father"));
	}

	/**
	 * Stray else
	 * @throws SyntaxError
	 */
	@Test
	public void testSyntaxError_1() throws SyntaxError {
		try {
			new TinyTemplate("dog = [[$else]]");
			fail("Expected syntax error!");
		} catch (SyntaxError e) {
			assertEquals("Syntax error at line 1: stray $else", e.getMessage());
		}
	}

	/**
	 * Stray endif
	 * @throws SyntaxError
	 */
	@Test
	public void testSyntaxError_2() throws SyntaxError {
		try {
			new TinyTemplate("dog = [[$endif]]");
			fail("Expected syntax error!");
		} catch (SyntaxError e) {
			assertEquals("Syntax error at line 1: stray $endif", e.getMessage());
		}
	}

	/**
	 * Missing endif
	 * @throws SyntaxError
	 */
	@Test
	public void testSyntaxError_3() throws SyntaxError {
		try {
			new TinyTemplate("dog = [[$if(x)]]");
			fail("Expected syntax error!");
		} catch (SyntaxError e) {
			assertEquals("Syntax error at line 1: missing $endif", e.getMessage());
		}
	}

	/**
	 * Missing endif
	 * @throws SyntaxError
	 */
	@Test
	public void testSyntaxError_4() throws SyntaxError {
		try {
			new TinyTemplate("dog = [[$if(x)$else]]");
			fail("Expected syntax error!");
		} catch (SyntaxError e) {
			assertEquals("Syntax error at line 1: missing $endif", e.getMessage());
		}
	}

	/**
	 * Extra else inside if-else
	 * @throws SyntaxError
	 */
	@Test
	public void testSyntaxError_5() throws SyntaxError {
		try {
			new TinyTemplate("dog = [[\n" +
					"$if(x)\n" +
					"$else\n" +
					"$else]]");
			fail("Expected syntax error!");
		} catch (SyntaxError e) {
			assertEquals("Syntax error at line 4: too many $else", e.getMessage());
		}
	}

	/**
	 * Test trimming leading newline in conditional body
	 * @throws SyntaxError
	 */
	@Test
	public void testTrimming_1() throws SyntaxError {
		TinyTemplate tt = new TinyTemplate(
				"dog = [[" +
				"$if(bark)  \t \n" +
				"Woof!$endif" +
				"]]");
		SimpleContext tc = new SimpleContext(tt, new Object());

		tc.bind("bark", "true");

		assertEquals("Woof!", tc.expand("dog"));
	}

	/**
	 * If the conditional is surrounded by whitespace then the surrounding
	 * whitespace up to and including the newline is removed.
	 * @throws SyntaxError
	 */
	@Test
	public void testTrimming_2() throws SyntaxError {
		TinyTemplate tt = new TinyTemplate(
				"dog = [[  $if(bark)Woof!$endif   \n]]");
		SimpleContext tc = new SimpleContext(tt, new Object());

		tc.bind("bark", "true");

		assertEquals("Woof!" + NL, tc.expand("dog"));
	}

	/**
	 * Don't trim whitespace around the conditional if it has non-whitespace
	 * surrounding it
	 * @throws SyntaxError
	 */
	@Test
	public void testTrimming_3() throws SyntaxError {
		TinyTemplate tt = new TinyTemplate(
				"dog = [[  $if(bark)Woof!$endif   ;\n]]");
		SimpleContext tc = new SimpleContext(tt, new Object());

		tc.bind("bark", "true");

		assertEquals("  Woof!   ;" + NL, tc.expand("dog"));
	}

	/**
	 * Don't trim whitespace around the conditional if there is another
	 * conditional on the same line
	 * @throws SyntaxError
	 */
	@Test
	public void testTrimming_4() throws SyntaxError {
		TinyTemplate tt = new TinyTemplate(
				"dog = [[  $if(bark)Woof!$endif   $if(bark)Woof!$endif\n]]");
		SimpleContext tc = new SimpleContext(tt, new Object());

		tc.bind("bark", "true");

		assertEquals("  Woof!   Woof!" + NL, tc.expand("dog"));
	}

	/**
	 * Trim last trailing empty line inside conditional
	 * @throws SyntaxError
	 */
	@Test
	public void testTrimming_5() throws SyntaxError {
		TinyTemplate tt = new TinyTemplate(
				"dog = [[  $if(bark)\n" +
				" Woof!\n" +
				"  $endif]]");
		SimpleContext tc = new SimpleContext(tt, new Object());

		tc.bind("bark", "true");

		assertEquals(" Woof!", tc.expand("dog"));
	}

	/**
	 * Trim last trailing empty line inside conditional
	 * @throws SyntaxError
	 */
	@Test
	public void testTrimming_6() throws SyntaxError {
		TinyTemplate tt = new TinyTemplate(
				"dog = [[  $if(!bark)\n" +
				" Notwoof  \n" +
				" \t$else  \n" +
				" Woof!\n" +
				"  $endif   ]]");
		SimpleContext tc = new SimpleContext(tt, new Object());

		tc.bind("bark", true);
		assertEquals(" Woof!", tc.expand("dog"));

		tc.bind("bark", false);
		assertEquals(" Notwoof  ", tc.expand("dog"));
	}

	/**
	 * Trim last trailing empty line inside conditional
	 * @throws SyntaxError
	 */
	@Test
	public void testTrimming_7() throws SyntaxError {
		TinyTemplate tt = new TinyTemplate(
				"t = [[a\n" +
				"$if(x)\n" +
				" x\n" +
				"  $endif\n" +
				"b]]");
		SimpleContext tc = new SimpleContext(tt, new Object());

		tc.bind("x", true);
		assertEquals("a" + NL +
				" x" + NL +
				"b", tc.expand("t"));
	}

}
