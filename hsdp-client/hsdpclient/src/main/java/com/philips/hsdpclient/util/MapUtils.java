/*
 * Copyright (c) 2016 Koninklijke Philips N.V.
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.hsdpclient.util;

import java.lang.reflect.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.google.common.base.Defaults;

public abstract class MapUtils {
    private MapUtils() {
    }

    @SuppressWarnings("unchecked")
    public static <T> T extract(Map<String, Object> map, String path) {
        Object value = map;

        try {
            for (String p : path.split("\\."))
                value = ((Map<String, Object>) value).get(p);

            return (T) value;
        } catch (Exception e) {
            return null;
        }
    }

    public static <T> T construct(Map<String, Object> map, Class<T> clazz) {
        T object = createInstance(clazz);
        setAttributes(object, map);
        return object;
    }

    private static <T> T createInstance(Class<T> clazz) throws IllegalArgumentException {
        Constructor<?> constructor = getFirstAccesibleConstructor(clazz);
        return invokeConstructorWithDefaultValues(constructor);
    }

    @SuppressWarnings("unchecked")
    /**
     * Invokes the given constructor, filling all parameters with default values suitable
     * for the parameter types.
     */
    private static <T> T invokeConstructorWithDefaultValues(Constructor<?> constructor) {
        Class<?>[] ctorParameterTypes = constructor.getParameterTypes();
        Object[] parameters = new Object[ctorParameterTypes.length];

        for (int i = 0; i < ctorParameterTypes.length; i++)
            parameters[i] = Defaults.defaultValue(ctorParameterTypes[i]);

        try {
            constructor.setAccessible(true);
            return (T) constructor.newInstance(parameters);
        } catch (InstantiationException | IllegalArgumentException | InvocationTargetException | IllegalAccessException e) {
            throw new IllegalArgumentException("Problem instantiating " + constructor.getDeclaringClass(), e);
        }
    }

    private static Constructor<?> getFirstAccesibleConstructor(Class<?> clazz) {
        Constructor<?>[] constructors = clazz.getDeclaredConstructors();

        if (constructors.length == 0)
            throw new IllegalArgumentException("Could not find a suitable constructor to create an instance of " + clazz);

        return constructors[0];
    }

    private static <T> void setAttributes(T object, Map<String, Object> map) {
        Class<?> clazz = object.getClass();
        for (Entry<String, Object> entry : map.entrySet())
            try {
                Field f = clazz.getDeclaredField(entry.getKey());
                f.setAccessible(true);
                Type genericType = f.getGenericType();
                Class<?> elementClass = null;
                if (genericType instanceof ParameterizedType)
                    elementClass = (Class<?>) ((ParameterizedType) genericType).getActualTypeArguments()[0];
                f.set(object, convertToType(map.get(f.getName()), f.getType(), elementClass));
            } catch (IllegalArgumentException | IllegalAccessException | SecurityException e) {
                throw new IllegalArgumentException(String.format("Cannot map value for field '%s' of %s", entry.getKey(), clazz), e);
            } catch (NoSuchFieldException e) {
                throw new IllegalArgumentException(String.format("%s does not support a field named '%s'", clazz, entry.getKey()), e);
            }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static Object convertToType(Object value, Class<?> type, Class<?> elementType) {
        if (value instanceof List)
            return ((List) value).stream()
                    .map(e -> convertToType(e, elementType, null))
                    .collect(Collectors.toList());
        if (type.isEnum())
            return Enum.valueOf((Class) type, (String) value);
        if (value instanceof Date)
            return new DateTime(value, DateTimeZone.UTC);
        if (type.isPrimitive() || type.isAssignableFrom(value.getClass()))
            return value;
        if (value instanceof Map) {
            Map<String, Object> subMap = (Map<String, Object>) value;
            return construct(subMap, type);
        } else if (DateTime.class.equals(type)) {
            return DateTimeUtils.toDateTime((String) value);
        }

        throw new IllegalArgumentException(String.format("Value '%s' cannot be mapped to a field of %s", value, type));
    }
}
