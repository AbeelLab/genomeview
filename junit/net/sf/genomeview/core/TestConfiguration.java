package net.sf.genomeview.core;

import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 
 * @author Thomas Abeel
 * 
 */

public class TestConfiguration {

	@BeforeClass
	static public  void start() {
		Configuration.set("track:feature:labelIdentifiers", "protein_id,Name,ID,gene,label,note");
	}

	@Test
	public void testGetList() {
		List<String> list = Configuration.getStringList("track:feature:labelIdentifiers");
		Assert.assertEquals(6,list.size());
		Assert.assertTrue(list.contains("protein_id"));
		Assert.assertTrue(list.contains("ID"));
		Assert.assertTrue(list.contains("note"));
		
	}
}
