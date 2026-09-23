package com.example.cabpooling.util;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DistanceUtilTest {

    @Test
    void shouldCalculateDistanceBetweenTwoLocations() {

        double distance = DistanceUtil.calculateDistance(
                31.3260, 75.5762,
                31.3270, 75.5780
        );

        assertTrue(distance > 0);
    }

    @Test
    void sameLocationShouldReturnZero() {

        double distance = DistanceUtil.calculateDistance(
                31.3260, 75.5762,
                31.3260, 75.5762
        );

        assertEquals(0.0, distance, 0.001);
    }
}
