package net.paploo.kmusical

import kotlin.math.absoluteValue
import kotlin.math.sign

/**
 * An interval on a pitch.
 *
 * For a chromatic 12-tone scale, this can be translated to/from number and quality.
 */
sealed interface PitchInterval {
    operator fun plus(that: PitchInterval): PitchInterval
    operator fun minus(that: PitchInterval): PitchInterval
    operator fun unaryMinus(): PitchInterval

    fun toSemitoneInterval(): SemitoneInterval
    fun toCompoundInterval(): CompoundInterval
}

/**
 * An interval expressed in semitones.
 *
 * The exact frequency spacing of semitones is determined by the tuning.
 */
@JvmInline
value class SemitoneInterval(val value: Int) : PitchInterval {

    override fun plus(that: PitchInterval): SemitoneInterval = SemitoneInterval(
        value + that.toSemitoneInterval().value
    )

    override fun minus(that: PitchInterval): SemitoneInterval = SemitoneInterval(
        value - that.toSemitoneInterval().value
    )

    override fun unaryMinus(): SemitoneInterval = SemitoneInterval(-value)

    override fun toSemitoneInterval(): SemitoneInterval = this

    override fun toCompoundInterval(): CompoundInterval = value.let { semitones ->
        val sign = semitones.sign
        val octaves = Math.floorDiv(semitones.absoluteValue, 12)
        val simpleInterval = Math.floorMod(semitones.absoluteValue, 12).let { SemitoneInterval(it) }

        CompoundInterval(
            simplePart = simpleInterval.let { SimpleInterval.from(it) },
            octaveSpan = octaves.toUInt(),
            direction = if (sign >=0) CompoundInterval.Direction.UP else CompoundInterval.Direction.DOWN
        )
    }
}

/**
 * Represents a simple interval of one octave or less.
 *
 * This is "unsigned", so negation works-out to be its complimentary interval.
 */
enum class SimpleInterval(
    val number: Int,
    val quality: Quality,
    private val semitoneInterval: SemitoneInterval
) : PitchInterval {
    PERFECT_UNISON(1, Quality.PERFECT, SemitoneInterval(0)),
    MINOR_SECOND(2, Quality.MINOR, SemitoneInterval(1)),
    MAJOR_SECOND(2, Quality.MAJOR, SemitoneInterval(2)),
    MINOR_THIRD(3, Quality.MINOR, SemitoneInterval(3)),
    MAJOR_THIRD(3, Quality.MAJOR, SemitoneInterval(4)),
    PERFECT_FOURTH(4, Quality.PERFECT, SemitoneInterval(5)),
    AUGMENTED_FOURTH(4, Quality.AUGMENTED, SemitoneInterval(6)),
    DIMINISHED_FIFTH(5, Quality.DIMINISHED, SemitoneInterval(6)),
    PERFECT_FIFTH(5, Quality.PERFECT, SemitoneInterval(7)),
    MINOR_SIXTH(6, Quality.MINOR, SemitoneInterval(8)),
    MAJOR_SIXTH(6, Quality.MAJOR, SemitoneInterval(9)),
    MINOR_SEVENTH(7, Quality.MINOR, SemitoneInterval(10)),
    MAJOR_SEVENTH(7, Quality.MAJOR, SemitoneInterval(11)),
    PERFECT_OCTAVE(8, Quality.PERFECT, SemitoneInterval(12));

    enum class Quality(val abbreviation: String) {
        PERFECT("P"),
        MINOR("m"),
        MAJOR("M"),
        DIMINISHED("d"),
        AUGMENTED("A")
    }

    override fun plus(that: PitchInterval): SimpleInterval =
        SimpleInterval.from(toSemitoneInterval() + that.toSemitoneInterval())

    override fun minus(that: PitchInterval): SimpleInterval =
        SimpleInterval.from(toSemitoneInterval() - that.toSemitoneInterval())

    override fun unaryMinus(): SimpleInterval = PERFECT_UNISON - this

    override fun toSemitoneInterval(): SemitoneInterval = semitoneInterval

    override fun toCompoundInterval(): CompoundInterval = CompoundInterval(this, octaveSpan = 0U, direction = CompoundInterval.Direction.UP)

    companion object {

        /**
         * Translates a given interval in to a simple interval.
         *
         * When the interval is simple (0-12 semitones), it returns the exactly correct value.
         * When the interval is compound (e.g. more than one octave up, or is down), then the
         * interval is normalized into the 0-12 range via floor-modulo arithmetic.
         */
        fun from(interval: PitchInterval): SimpleInterval = when(interval) {
            is SemitoneInterval -> fromSemitoneInterval(interval)
            is SimpleInterval -> interval
            else -> from(interval.toSemitoneInterval())
        }

        private fun fromSemitoneInterval(interval: SemitoneInterval): SimpleInterval = interval.value.let { semitones ->
            if (semitones == 12) {
                PERFECT_OCTAVE
            } else {
                when (Math.floorMod(semitones, 12)) {
                    0 -> PERFECT_UNISON
                    1 -> MINOR_SECOND
                    2 -> MAJOR_SECOND
                    3 -> MINOR_THIRD
                    4 -> MAJOR_THIRD
                    5 -> PERFECT_FOURTH
                    6 -> DIMINISHED_FIFTH //We have to choose one; In the future we can control with an argument.
                    7 -> PERFECT_FIFTH
                    8 -> MINOR_SIXTH
                    9 -> MAJOR_SIXTH
                    10 -> MINOR_SEVENTH
                    11 -> MAJOR_SEVENTH
                    else -> throw NoSuchElementException("Failed to convert $interval to a simple one.") //Math.floorMod should keep this from happening.
                }
            }
        }

    }
}

/**
 * An interval expressed as a number and quality with direction.
 *
 * There are two ways to represent this:
 * 1. The constituent parts: number, quality, and direction.
 * 2. The octave, direction, and composite number/quality.
 * The first of these leads to illegal combinations, and thus the second is the native representation.
 *
 * Note that if the SimpleInterval is directly set as a PERFECT_OCTAVE, the value
 * _is not_ normalized back to a PERFECT_UNISON
 */
data class CompoundInterval(
    val simplePart: SimpleInterval,
    val octaveSpan: UInt = 0U,
    val direction: Direction = Direction.UP
) : PitchInterval {

    enum class Direction {
        UP, DOWN;

        operator fun unaryMinus(): Direction = when(this) {
            UP -> DOWN
            DOWN -> UP
        }
    }

    override fun plus(that: PitchInterval): CompoundInterval =
        (toSemitoneInterval() + that.toSemitoneInterval()).toCompoundInterval()

    override fun minus(that: PitchInterval): CompoundInterval =
        (toSemitoneInterval() - that.toSemitoneInterval()).toCompoundInterval()

    override fun unaryMinus(): CompoundInterval = copy(direction = -direction)

    override fun toSemitoneInterval(): SemitoneInterval =
        (simplePart.toSemitoneInterval() + SemitoneInterval(12*octaveSpan.toInt())).toSemitoneInterval()

    override fun toCompoundInterval(): CompoundInterval = this
}
