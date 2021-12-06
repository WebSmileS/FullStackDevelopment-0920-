package com.yrt.common.utils;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.BeanUtils;

import com.yrt.common.lang.KeyValue;

public class Utils {
	private Utils() {
	}

	public static class ObjectWrapper<T> {
		private T value;

		public T get() {
			return value;
		}

		public void set(T value) {
			this.value = value;
		}
	}

	public static <T> boolean equals(T o1, T o2) {
		if (isEmpty(o1)) {
			return isEmpty(o2);
		}
		return Objects.equals(o1, o2);
	}

	public static boolean isEmpty(String s) {
		return s == null || s.trim().isEmpty();
	}

	public static boolean isEmpty(Object o) {
		if (o == null) {
			return true;
		}
		if (o instanceof String) {
			return isEmpty((String) o);
		}
		if (o instanceof Collection) {
			return ((Collection<?>) o).isEmpty();
		}
		return false;
	}

	public static boolean allEmpty(Object... a) {
		if (a == null) {
			return true;
		}
		for (Object o : a) {
			if (isNotEmpty(o)) {
				return false;
			}
		}
		return true;
	}

	public static boolean isNotEmpty(Object o) {
		return !isEmpty(o);
	}

	public static boolean allNotEmpty(Object... a) {
		if (a == null) {
			return false;
		}
		for (Object o : a) {
			if (isEmpty(o)) {
				return false;
			}
		}
		return true;
	}

	public static String join(Object... parts) {
		return join(", ", Arrays.asList(parts));
	}

	public static String join(List<?> parts) {
		return join(",", parts);
	}

	public static String join(String sep, List<?> parts) {
		return parts.stream().map(t -> Objects.toString(t)).reduce("", (a, b) -> a + (a.isEmpty() ? "" : sep) + b);
	}

	public static <T> T ifNull(T v, T dft) {
		return v == null ? dft : v;
	}

	public static <T> T ifEmpty(T v, T dft) {
		return isEmpty(v) ? dft : v;
	}

	public static <T> T ifEmpty(Object v, T nv, T nev) {
		return isEmpty(v) ? nv : nev;
	}

	public static boolean falseIfNull(Boolean b) {
		return b == null ? false : b;
	}

	public static int zeroIfNull(Integer i) {
		return i == null ? 0 : i;
	}

	public static int zeroIfNegtive(Integer i) {
		return (i == null || i < 0) ? 0 : i;
	}

	public static String endWith(String s, String end) {
		if (s == null) {
			s = "";
		}
		if (s.endsWith(end)) {
			return s;
		}
		return s + end;
	}

	@SafeVarargs
	public static <T> T noneEmpty(T... a) {
		if (a == null) {
			return null;
		}
		for (T o : a) {
			if (isNotEmpty(o)) {
				return o;
			}
		}
		return null;
	}

	public static void assertTrue(boolean b, String msg) {
		if (b) {
			return;
		}
		throw new RuntimeException(msg);
	}

	public static void assertFalse(boolean b, String msg) {
		if (b) {
			throw new RuntimeException(msg);
		}
	}

	public static void assertNotEmpty(Object o, String msg) {
		if (isEmpty(o)) {
			throw new RuntimeException(msg);
		}
	}

	public static void assertEquals(Object o1, Object o2, String msg) {
		assertTrue(equals(o1, o2), msg);
	}

	public static void assertNotEquals(Object o1, Object o2, String msg) {
		assertTrue(equals(o1, o2), msg);
	}

	public static void assertNull(Object o, String msg) {
		if (o != null) {
			throw new RuntimeException(msg);
		}
	}

	public static <T> List<T> list(T o) {
		List<T> ret = new ArrayList<>();
		ret.add(o);
		return ret;
	}

	public static <T> List<T> merge(List<T> a, List<T> b) {
		List<T> ret = new ArrayList<>(a);
		ret.addAll(b);
		return ret;
	}

	public static <T, R> List<R> map(List<T> list, Function<T, R> f) {
		if (list == null) {
			return null;
		}
		return list.stream().map(f).collect(Collectors.toList());
	}

	public static <T, R> List<R> map(List<T> list, Class<R> targetClass) {
		return map(list, t -> clone(t, targetClass));
	}

	public static <T, R> R clone(T o, Class<R> targetClass) {
		R r;
		try {
			r = targetClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException("clone error");
		}
		BeanUtils.copyProperties(o, r);
		return r;
	}

	public static <T extends Comparable<T>> int compare(T o1, T o2) {
		return o1.compareTo(o2);
	}

	public static <K, V> V mapGetOrDefault(Map<K, V> map, K key, Supplier<V> s) {
		V value = map.getOrDefault(key, s.get());
		map.putIfAbsent(key, value);
		return value;
	}

	public static String maxLength(String s, int len) {
		if (s == null) {
			return null;
		}
		return s.length() > len ? s.substring(0, len) : s;
	}

	/**
	 * generate a new name which represents a copy of the originalName.
	 * <p>
	 * if originalName endwith "(n)" return name with inc n, otherwise return
	 * originalName + "(1)"
	 * 
	 * @param originalName
	 *            original name
	 * @param maxLen
	 * @return
	 */
	public static String nextName(String originalName, int maxLen) {
		Pattern p = Pattern.compile("(.*)\\((\\d+)\\)$");
		Matcher m = p.matcher(originalName);
		String prefix = originalName;
		String suffix = "(1)";
		if (m.matches()) {
			prefix = m.group(1);
			suffix = String.format("(%d)", Integer.valueOf(m.group(2)) + 1);
		}
		String newName = Utils.maxLength(prefix, maxLen - suffix.length())//
				+ suffix;
		return newName;
	}

	/**
	 * trim obj's fields(deep into its descendants String fields). but
	 * <strong>NOT-TRIM</strong> element of Collection&lt;String&gt; & String[].
	 * 
	 * @param <T>
	 * @param obj
	 * @return
	 */
	public static <T> T trimObject(T obj) {
		return trimObject(obj, false, null);
	}

	/**
	 * trim obj String fields(including descendants String fields).
	 * <strong>NOT-TRIM</strong> fields of Collection&lt;String&gt; & String[].
	 * 
	 * @param <T>
	 * @param obj
	 * @param nullIfEmpty
	 *            true set
	 * @param rootPackage
	 *            if not empty skip input obj which not under the rootPackage(except
	 *            String/Array/Collection/Map).
	 * @return trimed Object(deep into nested fields)
	 */
	@SuppressWarnings("unchecked")
	public static <T> T trimObject(T obj, boolean nullIfEmpty, String rootPackage, String... excludingFields) {
		if (obj == null) {
			return obj;
		}

		Class<?> cls = obj.getClass();

		if (cls == String.class) {
			return (T) trim(obj, nullIfEmpty);
		}

		if (obj.getClass().isArray()) {
			int len = Array.getLength(obj);
			for (int i = 0; i < len; i++) {
				Object e = Array.get(obj, i);
				Array.set(obj, i, trimObject(e, nullIfEmpty, rootPackage, excludingFields));
			}
			return obj;
		}

		if (obj instanceof Set) {
			Set<?> c = (Set<?>) obj;
			return (T) c.stream().map(e -> trimObject(e, nullIfEmpty, rootPackage, excludingFields))
					.collect(Collectors.toSet());
		}
		if (obj instanceof List) {
			List<?> c = (List<?>) obj;
			return (T) c.stream().map(e -> trimObject(e, nullIfEmpty, rootPackage, excludingFields))
					.collect(Collectors.toList());
		}

		if (obj instanceof Map) {
			Map<?, ?> m = (Map<?, ?>) obj;
			return (T) m.entrySet().stream()
					.map(e -> KeyValue.of(trimObject(e.getKey(), nullIfEmpty, rootPackage, excludingFields),
							trimObject(e.getValue(), nullIfEmpty, rootPackage, excludingFields)))
					.collect(Collectors.toMap(KeyValue::getKey, KeyValue::getValue));
		}
		if (obj.getClass().isArray()) {

		}

		if (isNotEmpty(rootPackage) && !cls.getName().startsWith(rootPackage)) {
			return obj;
		}

		Set<String> names = Stream.of(excludingFields).collect(Collectors.toSet());
		ReflectionUtils.processFields(obj, String.class, (f, v) -> {
			if (names.contains(f.getName())) {
				return v;
			}
			return trim(v, nullIfEmpty);
		});
		return obj;
	}

	private static String trim(Object s, boolean nullIfEmpty) {
		if (s == null) {
			return null;
		}
		String r = s.toString().trim();
		return (r.isEmpty() && nullIfEmpty) ? null : r;
	}

	/**
	 * return top level field names of obj, which not equals to relevant field of
	 * extObj(compare by equals)
	 *  <p>
	 *  <strong>Notes： not</strong> satisfy collection/map/array as top obj.
	 * @param obj
	 * @param extObj
	 * @return
	 */
	public static List<String> getNotEqualFields(Object obj, Object extObj) {
		if (obj == null) {
			return Collections.emptyList();// empty
		}

		if (extObj == null) {
			// all fields
			return ReflectionUtils.findInsFields(obj.getClass()).stream().map(Field::getName)
					.collect(Collectors.toList());
		}
		if (equals(obj, extObj)) {
			return Collections.emptyList();// empty
		}

		// not equal
		Map<String, Field> extFields = ReflectionUtils.findInsFields(extObj.getClass()).stream()
				.collect(Collectors.toMap(Field::getName, a -> a));
		
		return ReflectionUtils.findInsFields(obj.getClass()).stream().filter(f -> {
			if (extFields.containsKey(f.getName())) {
				try {
					Object v = f.get(obj);
					Object valExt = extFields.get(f.getName()).get(extObj);
					return !equals(v, valExt);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					return true;
				}
			}
			return true;
		}).map(Field::getName).collect(Collectors.toList());
	}

}
