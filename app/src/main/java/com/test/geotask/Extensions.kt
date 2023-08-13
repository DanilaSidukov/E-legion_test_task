package com.test.geotask

import kotlin.random.Random

fun generateRandomLatitude(random: Random): Float {
    return -90.000f + random.nextFloat() * (90.000f + 90.000f)
}

fun generateRandomLongitude(random: Random): Float {
    return -180.000f + random.nextFloat() * (180.000f + 180.000f)
}