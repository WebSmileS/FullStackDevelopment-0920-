package com.yrt.common.utils;

import org.junit.Assert;
import org.junit.Test;

public class UtilTest {

	
	@Test
	public void nextName() {
		Assert.assertEquals("(1)", Utils.nextName("", 5));
		Assert.assertEquals("大(1)", Utils.nextName("大", 5));
		Assert.assertEquals("大((1)", Utils.nextName("大()", 5));
		Assert.assertEquals("大(2)", Utils.nextName("大(1)", 5));
		Assert.assertEquals("大(10)", Utils.nextName("大(9)", 5));
		Assert.assertEquals("(100)", Utils.nextName("大(99)", 5));
	}
}
