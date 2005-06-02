/*
 * Created on May 29, 2005
 */
package net.sourceforge.fullsync.rules.filefilter;

import net.sourceforge.fullsync.fs.FileAttributes;

import junit.framework.TestCase;

/**
 * @author Michele Aiello
 */
public class FileSizeFileFilterRuleTest extends TestCase {

	public void testOpIs() {
		FileSizeFileFilterRule filterRule = new FileSizeFileFilterRule(1000, FileSizeFileFilterRule.OP_IS);
		TestNode file = new TestNode("foobar.txt", "/root/foobar.txt", true, false, 1000, 0);
		
		assertTrue(filterRule.match(file));

		file.setFileAttributes(new FileAttributes(2000, 0));
		assertTrue(!filterRule.match(file));
	}

	public void testOpIsnt() {
		FileSizeFileFilterRule filterRule = new FileSizeFileFilterRule(1000, FileSizeFileFilterRule.OP_ISNT);
		TestNode file = new TestNode("foobar.txt", "/root/foobar.txt", true, false, 1000, 0);
		assertTrue(!filterRule.match(file));
		
		file.setFileAttributes(new FileAttributes(2000, 0));
		assertTrue(filterRule.match(file));
	}

	public void testOpIsGreaterThan() {
		FileSizeFileFilterRule filterRule = new FileSizeFileFilterRule(1000, FileSizeFileFilterRule.OP_IS_GREATER_THAN);
		TestNode file = new TestNode("foobar.txt", "/root/foobar.txt", true, false, 1000, 0);

		assertTrue(!filterRule.match(file));
		
		file.setFileAttributes(new FileAttributes(2000, 0));
		assertTrue(filterRule.match(file));

		file.setFileAttributes(new FileAttributes(999, 0));
		assertTrue(!filterRule.match(file));
	}

	public void testOpIsLessThan() {
		FileSizeFileFilterRule filterRule = new FileSizeFileFilterRule(1000, FileSizeFileFilterRule.OP_IS_LESS_THAN);
		TestNode file = new TestNode("foobar.txt", "/root/foobar.txt", true, false, 1000, 0);

		assertTrue(!filterRule.match(file));
		
		file.setFileAttributes(new FileAttributes(2000, 0));
		assertTrue(!filterRule.match(file));

		file.setFileAttributes(new FileAttributes(999, 0));
		assertTrue(filterRule.match(file));
	}

}
