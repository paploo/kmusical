package net.paploo.kmusical.core

import kotlin.math.log2
import kotlin.math.roundToInt

/**
 * A concrete frequency of oscillation, measured in Hertz.
 */
data class Frequency(val hertz: Double) {

    companion object {
        val standard = Frequency(440.0)
    }

}

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
@JvmInline
value class FrequencyRatio(val value: Double) : FrequencyInterval {
    override fun plus(that: FrequencyInterval): FrequencyInterval = when (that) {
        is FrequencyRatio -> FrequencyRatio(value * that.value)
        is Cent -> toCents() + that
    }

    override fun minus(that: FrequencyInterval): FrequencyInterval = when (that) {
        is FrequencyRatio -> FrequencyRatio(value / that.value)
        is Cent -> toCents() - that
    }

    override fun unaryMinus(): FrequencyInterval = FrequencyRatio(1.0 / value)

    override fun toCents(): Cent = Cent(
        (Cent.octave.value.toDouble() * log2(value)).roundToInt()
    )

    override fun toFrequencyRatio(): FrequencyRatio = this
}

/**
 * A frequency interval defined as exactly 1/1200 of an octave.
 */
@JvmInline
value class Cent(val value: Int) : FrequencyInterval {
    override fun plus(that: FrequencyInterval): FrequencyInterval = Cent(value + that.toCents().value)

    override fun minus(that: FrequencyInterval): FrequencyInterval = Cent(value - that.toCents().value)

    override fun unaryMinus(): FrequencyInterval = Cent(-value)

    override fun toCents(): Cent = this

    override fun toFrequencyRatio(): FrequencyRatio = FrequencyRatio(
        Math.pow(2.0, value.toDouble() / octave.value.toDouble())
    )

    companion object {
        val zero = Cent(0)
        val octave = Cent(1200)
    }
}
