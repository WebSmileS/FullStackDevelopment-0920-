package com.yrt.common.lang;

import lombok.Data;

@Data // public constructor
public class KeyValue<K, V> {
	public static <K, V> KeyValue<K, V> of(K k, V v) {
		return new KeyValue<>(k, v);
	}

	private final K key;
	private final V value;

}
