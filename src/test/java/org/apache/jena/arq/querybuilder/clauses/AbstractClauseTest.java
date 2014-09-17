package org.apache.jena.arq.querybuilder.clauses;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import org.apache.jena.arq.AbstractRegexpBasedTest;
import org.apache.jena.arq.querybuilder.AbstractQueryBuilder;

import com.hp.hpl.jena.query.Query;

public abstract class AbstractClauseTest extends AbstractRegexpBasedTest {

	protected final String[] byLine(AbstractQueryBuilder<?> builder) {
		return builder.buildString().split("\n");
	}

	protected final Query getQuery(AbstractQueryBuilder<?> builder)
			throws NoSuchFieldException, SecurityException,
			IllegalArgumentException, IllegalAccessException {
		Field f = AbstractQueryBuilder.class.getDeclaredField("query");
		f.setAccessible(true);
		return (Query) f.get(builder);
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
