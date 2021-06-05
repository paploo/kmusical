package net.paploo.kmusical

sealed interface Pitch {
    operator fun plus(that: PitchInterval): Pitch
    operator fun minus(that: PitchInterval): Pitch
    operator fun minus(that: Pitch): PitchInterval

    fun toStandardPitch(): StandardPitch
}

/**
 * A pitch represented as an interval to the reference pitch of A4.
 *
 * Pitch can be seen as the anchor for an interval, giving it a value relative
 * to the real world. A4 is chosen since this is the modern concert
 * reference pitch, and so is slightly more meaningful than other values.
 */
@JvmInline
value class StandardPitch(val intervalFromA4: PitchInterval) : Pitch {

    override fun plus(that: PitchInterval): StandardPitch =
        StandardPitch(intervalFromA4 + that.toSemitoneInterval())

    override fun minus(that: PitchInterval): StandardPitch =
        StandardPitch(intervalFromA4 - that.toSemitoneInterval())

    override fun minus(that: Pitch): PitchInterval =
        intervalFromA4 - that.toStandardPitch().intervalFromA4

    override fun toStandardPitch(): StandardPitch = this

    companion object {
        val concertA: StandardPitch = StandardPitch(SemitoneInterval(0))
        val middleC: StandardPitch = StandardPitch(SemitoneInterval(-9))
    }
}

/**
 * A pitch by name, using Scientific Pitch Notation as its reference (thus C4 is middle C).
 *
 * A pitch that is the same realtive pitch from a refernce pitch may have multiple names, depending on key.
 * Therefore transformations from a `NamedPitch` to a `StandardPitch` require additional parameters to
 * correctly choose.
 */
data class NamedPitch(val name: PitchName, val accidental: Accidental, val octave: Int) : Pitch {

    enum class PitchName(val sciPitchInterval: PitchInterval) {
        C(SemitoneInterval(0)),
        D(SemitoneInterval(2)),
        E(SemitoneInterval(4)),
        F(SemitoneInterval(5)),
        G(SemitoneInterval(7)),
        A(SemitoneInterval(9)),
        B(SemitoneInterval(11));
    }

    enum class Accidental(val modifyingInterval: PitchInterval) {
        DOUBLE_FLAT(SemitoneInterval(-2)),
        FLAT(SemitoneInterval(-1)),
        NATURAL(SemitoneInterval(0)),
        SHARP(SemitoneInterval(1)),
        DOUBLE_SHARP(SemitoneInterval(2));
    }

    /**
     * Adds the given pitch interval, returning a StandardPitch.
     *
     * Adding to produce a NamedPitch requires context such as the key.
     */
    override fun plus(that: PitchInterval): StandardPitch =
        toStandardPitch() + that

    /**
     * Subtracts the given pitch interval, returning a StandardPitch.
     *
     * Adding to produce a NamedPitch requires context such as the key.
     */
    override fun minus(that: PitchInterval): StandardPitch =
        toStandardPitch() - that

    /**
     * Finds the interval between the pitches.
     */
    override fun minus(that: Pitch): CompoundInterval =
        (toStandardPitch() - that).toCompoundInterval()

    /**
     * TODO: Consider adding a compound interval to a named pitch but having to provide a key.
     * Adding an interval, especially without knowing the scale, is non-trivial.
     * The general rule used here is to attempt to have the pitch be a natural
     * note, followed by a sharp and then a flat.
     *
     * TODO: What about starting with a flat and then flatting one step to make a double-flat?
     *       Is that a different method (like flat() and sharp())? Does that require a key?
     */
//    fun plus(that: CompoundInterval, key: Key): NamedPitch = TODO()

//    fun plus(that: SimpleInterval, key: Key): NamedPitch = this.plus(that.toCompoundInterval(), key = key)

//    fun minus(that: CompoundInterval, key: Key): NamedPitch = TODO()

//    fun minus(that: SimpleInterval, key: Key): NamedPitch = this.minus(that.toCompoundInterval(), key = key)

    override fun toStandardPitch(): StandardPitch =
        StandardPitch.middleC +
                (name.sciPitchInterval + SemitoneInterval(SemitoneInterval.octave.value * octave) +
                accidental.modifyingInterval)

    companion object {
        val concertA: NamedPitch = NamedPitch(PitchName.A, Accidental.NATURAL, 4)
        val middleC: NamedPitch = NamedPitch(PitchName.C, Accidental.NATURAL, 4)
    }
}
