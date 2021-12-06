package com.yrt.common.lang;

import lombok.Data;

@Data
public class Tuple4<A, B, C, D> {

	public static <A, B, C, D> Tuple4<A, B, C, D> of(A f1, B f2, C f3, D f4) {
		return new Tuple4<>(f1, f2, f3, f4);
	}

	private final A f1;
	private final B f2;
	private final C f3;
	private final D f4;

}
