package com.yrt.common.lang;

import lombok.Data;

@Data
public class Tuple3<A, B, C> {

	public static <A, B, C> Tuple3<A, B, C> of(A f1, B f2, C f3) {
		return new Tuple3<>(f1, f2, f3);
	}

	private final A f1;
	private final B f2;
	private final C f3;

}
