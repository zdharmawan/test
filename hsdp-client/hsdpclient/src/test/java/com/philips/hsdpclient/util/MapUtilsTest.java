/*
 * Copyright (c) 2016 Koninklijke Philips N.V.
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.hsdpclient.util;

import static org.junit.Assert.*;

import java.util.*;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class MapUtilsTest {
    private static final Map<String, Object> EMPTY_MAP = ImmutableMap.of();

    private static class Nothing {}

    public static class Something {
        public final double field;
        public DateTime datetime;

        public Something() {
            field = 0;
        }
    }

    public static class ComplexThing {
        public final Something something;

        public ComplexThing() {
            something = null;
        }
    }

    public static class WithPrivateField {
        private String field;

        public String getField() {
            return field;
        }
    }

    private static class WithList {
        private final List<WithPrivateField> list;

        @SuppressWarnings("unused")
        public WithList(List<WithPrivateField> list) {
            this.list = list;
        }
    }

    private static class WithEnum {
        public enum Enum {
            ONE,
            TWO
        }

        public Enum value;
    }

    public static class NoDefaultConstructor {
        final String name;
        private final int age;
        private final boolean awesome;
        private final double precision;
        private final Something something;

        public NoDefaultConstructor(String name, int age, boolean awesome, double precision, Something something) {
            this.name = name;
            this.age = age;
            this.awesome = awesome;
            this.precision = precision;
            this.something = something;
        }
    }

    public static class Boom {
        public Boom() {
            throw new RuntimeException("KABOOM!");
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwsOnIrrelevantMapEntries() {
        MapUtils.construct(ImmutableMap.of("irrelevant", (Object) false), Nothing.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwsOnIllegalValueType() {
        MapUtils.construct(ImmutableMap.of("field", (Object) "wrong"), Something.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwsOnUnassignablePrimitiveValue() {
        ImmutableMap<String, Object> map = ImmutableMap.of("field", (Object) true);
        MapUtils.construct(map, Something.class);
    }

    @Test
    public void constructsObjectWithoutAttributes() {
        Nothing nothing = MapUtils.construct(EMPTY_MAP, Nothing.class);
        assertNotNull(nothing);
        assertTrue(nothing instanceof Nothing);
    }

    @Test
    public void constructsSimpleObjectFromMap() {
        ImmutableMap<String, Object> map = ImmutableMap.of("field", (Object) 1.23);
        Something something = MapUtils.construct(map, Something.class);

        assertNotNull(something);
        assertEquals(1.23, something.field, 0);
    }

    @Test
    public void constructsNestedObjectFromMap() {
        ImmutableMap<String, Object> map = ImmutableMap.of("something", (Object) ImmutableMap.of("field", 3.45));
        ComplexThing result = MapUtils.construct(map, ComplexThing.class);

        assertNotNull(result);
        assertEquals(3.45, result.something.field, 0);
    }

    @Test
    public void constructsListMembers() {
        Map<String, Object> map = ImmutableMap.of("list", ImmutableList.of(ImmutableMap.of("field", "value")));
        WithList result = MapUtils.construct(map, WithList.class);

        assertTrue(result instanceof WithList);
        assertEquals(1, result.list.size());
        assertEquals("value", result.list.get(0).field);
    }

    @Test
    public void constructsEnumMembers() {
        Map<String, Object> map = ImmutableMap.of("value", "TWO");
        WithEnum result = MapUtils.construct(map, WithEnum.class);

        assertTrue(result instanceof WithEnum);
        assertEquals(WithEnum.Enum.TWO, result.value);
    }

    @Test
    public void constructsJodaDateTimeField() {
        ImmutableMap<String, Object> map = ImmutableMap.of("datetime", (Object) "2014-10-11T12:34:56");
        Something something = MapUtils.construct(map, Something.class);

        assertEquals(new DateTime(2014, 10, 11, 12, 34, 56, DateTimeZone.UTC), something.datetime);
    }

    @Test
    public void constructsJodaDateTimeFieldWithDateOnly() {
        ImmutableMap<String, Object> map = ImmutableMap.of("datetime", (Object) "2014-10-11");
        Something something = MapUtils.construct(map, Something.class);

        assertEquals(new DateTime(2014, 10, 11, 0, 0, 0, DateTimeZone.UTC), something.datetime);
    }

    @Test
    public void constructsJodaDateTimeFieldFromDate() {
        DateTime joda = DateTime.now(DateTimeZone.UTC);
        ImmutableMap<String, Object> map = ImmutableMap.of("datetime", joda.toDate());
        Something something = MapUtils.construct(map, Something.class);

        assertEquals(joda, something.datetime);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwsOnInvalidDateTimeField() {
        MapUtils.construct(ImmutableMap.of("datetime", (Object) "Not a datetime"), Something.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwsWhenObjectConstructionFails() {
        MapUtils.construct(new HashMap<String, Object>(), Boom.class);
    }

    @Test
    public void constructsObjectsWithPrivateFields() {
        WithPrivateField object = MapUtils.construct(ImmutableMap.of("field", (Object) "value"), WithPrivateField.class);
        assertEquals("value", object.getField());
    }

    @Test
    public void constructsImmutableObjectsWithoutDefaultConstructor() {
        NoDefaultConstructor object = MapUtils.construct(ImmutableMap.of(
                "name", "John Doe",
                "age", 42,
                "awesome", true,
                "precision", 123d,
                "something", new Something()), NoDefaultConstructor.class);

        assertEquals("John Doe", object.name);
        assertEquals(42, object.age);
        assertEquals(true, object.awesome);
        assertEquals(123d, object.precision, 0.01);
        assertTrue(object.something != null);
    }
}
