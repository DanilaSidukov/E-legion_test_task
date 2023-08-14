package com.test.geotask

import kotlin.random.Random

fun generateRandomLatitude(): Float {
    return -90.000f + Random.nextFloat() * (90.000f + 90.000f)
}

fun generateRandomLongitude(): Float {
    return -180.000f + Random.nextFloat() * (180.000f + 180.000f)
}