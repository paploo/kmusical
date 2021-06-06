package net.paploo.kmusical.core

import kotlin.math.log2
import kotlin.math.pow
import kotlin.math.roundToInt

/**
 * Common interface for measuring intervals between frequencies.
 *
 * This interval is used for relating intervals to real frequencies, as opposed
 * to pitches on a scale (which are affected by tuning).
 */
sealed interface FrequencyInterval {
    operator fun plus(that: FrequencyInterval): FrequencyInterval
    operator fun minus(that: FrequencyInterval): FrequencyInterval
    operator fun unaryMinus(): FrequencyInterval

    fun toCents(): Cent
    fun toFrequencyRatio(): FrequencyRatio
}

/**
 * An interval defined in terms of the frequency ratio.
 */
data class FrequencyRatio(val value: Double) : FrequencyInterval {

    override fun plus(that: FrequencyInterval): FrequencyRatio =
        FrequencyRatio(value * that.toFrequencyRatio().value)

    override fun minus(that: FrequencyInterval): FrequencyRatio =
        FrequencyRatio(value / that.toFrequencyRatio().value)

    override fun unaryMinus(): FrequencyRatio =
        FrequencyRatio(1.0 / value)

    override fun toCents(): Cent = Cent(
        (Cent.octave.value.toDouble() * log2(value)).roundToInt()
    )

    override fun toFrequencyRatio(): FrequencyRatio = this

    companion object {
        val unison = FrequencyRatio(1.0)
        val octave = FrequencyRatio(2.0)
    }
}

/**
 * A frequency interval defined as exactly 1/1200 of an octave.
 */
@JvmInline
value class Cent(val value: Int) : FrequencyInterval {

    override fun plus(that: FrequencyInterval): Cent =
        Cent(value + that.toCents().value)

    override fun minus(that: FrequencyInterval): Cent =
        Cent(value - that.toCents().value)

    override fun unaryMinus(): Cent =
        Cent(-value)

    override fun toCents(): Cent = this

    override fun toFrequencyRatio(): FrequencyRatio = FrequencyRatio(
        2.0.pow(value.toDouble() / octave.value.toDouble())
    )

    companion object {
        val unison = Cent(0)
        val octave = Cent(1200)
    }
}