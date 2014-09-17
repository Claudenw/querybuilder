package org.apache.jena.arq.querybuilder.handlers;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.util.Arrays;
import java.util.List;
import org.apache.jena.arq.AbstractRegexpBasedTest;

public abstract class AbstractHandlerTest extends AbstractRegexpBasedTest {

	protected final String[] byLine(String s) {
		return s.split("\n");
	}

	protected final void assertContains(String expected, String[] lst) {
		List<String> s = Arrays.asList(lst);
		assertTrue(String.format("%s not found in %s", expected, s),
				s.contains(expected));
	}

	protected final void assertNotContains(String expected, String[] lst) {
		List<String> s = Arrays.asList(lst);
		assertFalse(String.format("%s found in %s", expected, s),
				s.contains(expected));
	}

}
