package br.com.willianantunes.kotlinplayground

import java.time.Duration
import java.time.Instant
import java.time.temporal.ChronoUnit

fun timeIt(blockOfCode: () -> Unit): Duration {
    val start = Instant.now()

    blockOfCode()

    val end = Instant.now()
    return Duration.of(ChronoUnit.MILLIS.between(start, end), ChronoUnit.MILLIS)
}
