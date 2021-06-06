package net.paploo.kmusical.core

/**
 * A concrete frequency of physical oscillation of a medium, measured in Hertz.
 */
data class Frequency(val hertz: Double) {
    init {
        assert(hertz > 0.0) { "Frequencies must be a postivie number, but $hertz was given" }
    }

    companion object {
        val standard = Frequency(440.0)
    }

}
