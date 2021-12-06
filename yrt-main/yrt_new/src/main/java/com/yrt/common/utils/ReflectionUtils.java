package com.yrt.common.utils;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.stream.Stream;

public class ReflectionUtils {

	private static final List<Class<?>> PRIMARY_WRAPPERS = Arrays.asList(Boolean.class, Byte.class, Short.class,
			Integer.class, Long.class, Float.class, Double.class, Character.class, Void.class);

	private static final List<Class<?>> JAVA_SIMPLE_TYPES = Arrays.asList(//
			Number.class, // BigDecimal/BigInteger...
			String.class, //
			// Enum.class, // Class.isEnum
			Date.class
	// java.time.xxx
	);

	public static boolean isSimpleType(Class<?> c) {
		return c.isPrimitive() || c.isEnum() || isPrimaryWrapper(c) || isJavaSimpleType(c);

	}

	private static boolean isPrimaryWrapper(Class<?> c) {
		return PRIMARY_WRAPPERS.contains(c);
	}

	private static boolean isJavaSimpleType(Class<?> c) {
		return JAVA_SIMPLE_TYPES.stream().anyMatch(e -> e.isAssignableFrom(c));
	}

	private static boolean isCollection(Class<?> c) {
		return Collection.class.isAssignableFrom(c);
	}

	private static boolean isArray(Class<?> c) {
		return c.isArray();
	}

	private static boolean isMap(Class<?> c) {
		return Map.class.isAssignableFrom(c);
	}

	public static List<Field> findInsFields(Class<?> c) {
		List<Field> fields = new ArrayList<>();
		Field[] fs = c.getDeclaredFields();
		if (fs != null) {
			Stream.of(fs).filter(f -> !Modifier.isStatic(f.getModifiers())).forEach(f -> {
				if (!f.isAccessible()) {
					f.setAccessible(true);
					fields.add(f);
				}
			});
		}
		if (c.getSuperclass() != null) {
			fields.addAll(findInsFields(c.getSuperclass()));
		}
		return fields;
	}

	public List<String> findNullFields(Object obj) {
		List<String> fields = new ArrayList<>();
		findInsFields(obj.getClass()).forEach(f -> {
			try {
				if (f.get(obj) == null) {
					fields.add(f.getName());
				}
			} catch (IllegalArgumentException | IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		});
		return fields;
	}

	public List<String> findNotNullFields(Object obj) {
		List<String> fields = new ArrayList<>();
		findInsFields(obj.getClass()).forEach(f -> {
			try {
				if (f.get(obj) != null) {
					fields.add(f.getName());
				}
			} catch (IllegalArgumentException | IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		});
		return fields;
	}

	private static <T> void traverseObject(Object obj, BiFunction<Field, T, T> bc, Set<Integer> ids) {
		if (obj == null) {
			return;
		}

		if (isSimpleType(obj.getClass())) {
			return;
		}

		if (!ids.add(System.identityHashCode(obj))) {
			return;
		}

		if (isCollection(obj.getClass())) {
			Collection<?> c = (Collection<?>) obj;
			c.forEach(e -> traverseObject(e, bc, ids));
			return;
		}

		if (isArray(obj.getClass())) {
			int len = Array.getLength(obj);
			for (int i = 0; i < len; i++) {
				traverseObject(Array.get(obj, i), bc, ids);
			}
			return;
		}

		if (isMap(obj.getClass())) {
			Map<?, ?> m = (Map<?, ?>) obj;
			m.forEach((k, v) -> {
				traverseObject(k, bc, ids);
				traverseObject(v, bc, ids);
			});
		}

		findInsFields(obj.getClass()).forEach(f -> {
			try {
				@SuppressWarnings("unchecked")
				T value = (T) f.get(obj);
				T retValue = bc.apply(f, value);
				if (retValue != value) {
					f.set(obj, retValue);
				}
				traverseObject(retValue, bc, ids);
			} catch (IllegalArgumentException | IllegalAccessException ex) {
				throw new RuntimeException(ex);
			}
		});
	}

	/**
	 * traverse field value (for validation etc.)
	 * 
	 * @param obj
	 * @param bc
	 */
	public static void traverseFields(Object obj, BiConsumer<Field, Object> bc) {
		traverseObject(obj, (f, v) -> {
			bc.accept(f, v);
			return v;
		}, new HashSet<>());
	}

	/**
	 * process field value and set change back
	 * 
	 * @param <T>
	 * @param obj
	 * @param bf
	 */
	public static <T> void processFields(Object obj, BiFunction<Field, T, T> bf) {
		traverseObject(obj, bf, new HashSet<>());
	}

	/**
	 * process specified type of field value and set change back
	 * 
	 * @param <T>
	 * @param obj
	 * @param fieldType
	 * @param bf
	 */
	public static <T> void processFields(Object obj, Class<T> fieldType, BiFunction<Field, T, T> bf) {
		processFields(obj, (f, v) -> {
			if (fieldType.isAssignableFrom(f.getType())) {
				@SuppressWarnings("unchecked")
				T value = (T) v;
				return bf.apply(f, value);
			} else {
				return v;
			}
		});
	}

}
