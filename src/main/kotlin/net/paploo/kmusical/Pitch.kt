package net.paploo.kmusical

/**
 * The smallest "uniform" interval of the chromatic scale.
 *
 * The exact frequency spacing of semitones is determined by the tuning.
 */
data class Semitone(val value: Int)

/**
 * An interval on a pitch.
 *
 * For a chromatic 12-tone scale, this can be translated to/from number and quality.
 */
sealed interface PitchInterval {
    operator fun plus(that: PitchInterval): PitchInterval
    operator fun minus(that: PitchInterval): PitchInterval

    fun toSemitoneInterval(): SemitoneInterval
}

/**
 * An interval in pitch, expressed in semitones.
 */
data class SemitoneInterval(val semitone: Semitone): PitchInterval {

    override fun plus(that: PitchInterval): PitchInterval {
        TODO("Not yet implemented")
    }

    override fun minus(that: PitchInterval): PitchInterval {
        TODO("Not yet implemented")
    }

    override fun toSemitoneInterval(): SemitoneInterval {
        TODO("Not yet implemented")
    }
}

/**
 * An interval expressed as a number and quality.
 *
 * TODO: Figure out how to best express this, as
 *       * Not all combinations are legal
 *       * How do octaves work out—need to use compound intervals.
 *       * How do we descend? Are these only positive quantities and we have to subtract?
 *
 * TODO: It may be that we make Interval be SemitoneInterval, and make ChromaticInterval its own thing, and keep typing separated to use each theri own way.
 * TODO: Make a direction, up vs. down (up being the default), to match colloquial terminology "down a fourth"
 */
data class ChromaticInterval private constructor (val number: Int, val quality: Quality) {

    enum class Quality(val abbreviation: String) {
        //TODO: Add inversion of each.
        Perfect("P"), Minor("m"), Major("M"), Diminished("d"), Augmented("A")
    }

}

/**
 * A pitch represented as an interval to the reference pitch of A4.
 *
 * Pitch can be seen as the anchor for an interval, giving it a value relative
 * to the real world. A4 is chosen since this is the modern concert
 * reference pitch, and so is slightly more meaningful than other values.
 */
data class Pitch(val interval: PitchInterval)