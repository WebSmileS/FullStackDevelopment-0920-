package com.yrt.common.lang;

import lombok.Data;

@Data
public class Tuple5<A, B, C, D, E> {

	public static <A, B, C, D, E> Tuple5<A, B, C, D, E> of(A f1, B f2, C f3, D f4, E f5) {
		return new Tuple5<>(f1, f2, f3, f4, f5);
	}

	private final A f1;
	private final B f2;
	private final C f3;
	private final D f4;
	private final E f5;

}
