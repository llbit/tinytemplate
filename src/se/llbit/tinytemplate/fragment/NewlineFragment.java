package se.llbit.tinytemplate.fragment;

import java.io.PrintStream;

import se.llbit.tinytemplate.TinyTemplate;

/**
 * Represents the system-dependent newline character sequence.
 * 
 * This is a singleton class.
 * 
 * @author Jesper Öqvist <jesper.oqvist@cs.lth.se>
 */
public class NewlineFragment implements IFragment {
	
	/**
	 * Singleton instance
	 */
	public static IFragment INSTANCE = new NewlineFragment();
	
	private NewlineFragment() {}

	@Override
	public void expand(TinyTemplate template, PrintStream out) {
		out.println();
	}
	
	@Override
	public String toString() {
		return "\n";
	}
}
