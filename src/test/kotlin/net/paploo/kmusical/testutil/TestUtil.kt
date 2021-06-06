package net.paploo.kmusical.testutil

import io.kotest.matchers.doubles.ToleranceMatcher
import io.kotest.matchers.doubles.plusOrMinus

fun Double.plusOrMinus(): ToleranceMatcher = plusOrMinus(1e-15)