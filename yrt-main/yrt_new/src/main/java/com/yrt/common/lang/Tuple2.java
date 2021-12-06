package com.yrt.common.lang;

import lombok.Data;

@Data
public class Tuple2<A, B> {
	
	public static <A, B> Tuple2<A, B> of(A f1, B f2) {
		return new Tuple2<>(f1, f2);
	}

	private final A f1;
	private final B f2;

}
