package org.perf.logging;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SimplePojoTest {

    private static final List<String> LIST = List.of("A");

    @Test void testConstructor() {
        // given

        String value = null;
        if (LIST.size() == 1) {
            value = LIST.get(0);
        }

        // when

        // then
        assertNotNull(value);
    }

    @Test void testConstructor2() {
        // given

        String value = null;
        if (LIST.size() == 1) {
            value = LIST.stream().findFirst().orElseThrow();
        }

        // when

        // then
        assertNotNull(value);
    }

}
