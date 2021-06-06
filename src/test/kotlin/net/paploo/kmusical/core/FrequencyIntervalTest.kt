package net.paploo.kmusical.core

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.doubles.ToleranceMatcher
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.beInstanceOf
import io.kotest.property.Arb
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.pair
import io.kotest.property.arbitrary.triple
import io.kotest.property.checkAll
import net.paploo.kmusical.testutil.plusOrMinus
import kotlin.math.absoluteValue

class FrequencyRatioTest : DescribeSpec({

    val frequencyRatioGen = arbitrary { rs ->
        FrequencyRatio(rs.random.nextDouble().absoluteValue)
    }

    describe("instantiation") {

        it("should not allow negative values") {
            shouldThrowAny { Frequency(-0.5) }
            shouldThrowAny { Frequency(-2.0) }
        }

        it("should not instantiate with a zero frequency") {
            shouldThrowAny { Frequency(0.0) }
        }

    }

    describe("plus") {

        it("The result should be typed as a frequency ratio") {
            val a = FrequencyRatio(1.5)
            val r: FrequencyRatio = a + a // Won't compile if typed wrong.
            r should beInstanceOf(FrequencyRatio::class)
        }

        it("should be the product of the value in hertz") {
            Arb.pair(frequencyRatioGen, frequencyRatioGen).checkAll { (a, b) ->
                val r = a + b
                r.value shouldBe (a.value * b.value).plusOrMinus()
            }
        }

        describe("it should be monoidal") {

            it("should be associative") {
                Arb.triple(frequencyRatioGen, frequencyRatioGen, frequencyRatioGen).checkAll { (a, b, c) ->
                    val left = (a + b) + c
                    val right = a + (b + c)
                    left.value shouldBe (right.value.plusOrMinus())
                }
            }

            it("should have left identity") {
                frequencyRatioGen.checkAll {
                    it + FrequencyRatio.unison shouldBe it
                }
            }

            it("should have right identity") {
                frequencyRatioGen.checkAll {
                    FrequencyRatio.unison + it shouldBe it
                }
            }

        }

    }

    describe("minus") {

        it("The result should be typed as a frequency ratio") {
            val a = FrequencyRatio(1.5)
            val r: FrequencyRatio = a - a // Won't compile if typed wrong.
            r should beInstanceOf(FrequencyRatio::class)
        }

        it("should be the div of the value in hertz") {
            Arb.pair(frequencyRatioGen, frequencyRatioGen).checkAll { (a,b) ->
                val r = a - b
                r.value shouldBe (a.value / b.value).plusOrMinus()
            }
        }

    }

    describe("unaryMinus") {

        it("should be the additive inverse") {
            frequencyRatioGen.checkAll {
                (it + (-it)).value shouldBe ((FrequencyRatio.unison).value.plusOrMinus())
            }
        }

    }

    describe("toCents") {

        it("should convert unison constant") {
            FrequencyRatio.unison.toCents() shouldBe Cent.unison
        }

        it("should convert octave constant") {
            FrequencyRatio.octave.toCents() shouldBe Cent.octave
        }

        it("should convert a just fifth to the nearest whole cent value") {
            FrequencyRatio(3.0 / 2.0).toCents() shouldBe Cent(702)
        }

        it("should convert a value down by one fourth to the nearest whole cent value") {
            FrequencyRatio(3.0/4.0).toCents() shouldBe Cent(-498)
        }

    }

    describe("toFrequencyRatio") {

        it("should return the same value") {
            frequencyRatioGen.checkAll {
                it.toFrequencyRatio() shouldBe it
            }
        }

    }

})

class CentTest : DescribeSpec({

    val centGen = arbitrary { rs ->
        Cent(rs.random.nextInt(-12000,12000).absoluteValue)
    }

    describe("plus") {

        it("The result should be typed as a cent") {
            val a = Cent(702)
            val r: Cent = a + a // Won't compile if typed wrong.
            r should beInstanceOf(Cent::class)
        }

        it("should be the sum of the raw value") {
            Arb.pair(centGen, centGen).checkAll { (a,b) ->
                val r = a + b
                r.value shouldBe a.value + b.value
            }
        }

        describe("it should be monoidal") {

            it("should be associative") {
                Arb.triple(centGen, centGen, centGen).checkAll { (a, b, c) ->
                    val left = (a + b) + c
                    val right = a + (b + c)
                    left shouldBe right
                }
            }

            it("should have left identity") {
                centGen.checkAll {
                    it + Cent.unison shouldBe it
                }
            }

            it("should have right identity") {
                centGen.checkAll {
                    Cent.unison + it shouldBe it
                }
            }

        }

    }

    describe("minus") {

        it("The result should be typed as a cent") {
            val a = Cent(702)
            val r: Cent = a - a // Won't compile if typed wrong.
            r should beInstanceOf(Cent::class)
        }

        it("should be the difference of the raw value") {
            Arb.pair(centGen, centGen).checkAll { (a,b) ->
                val r = a - b
                r.value shouldBe (a.value - b.value)
            }
        }

    }

    describe("unaryMinus") {

        it("should be the additive inverse") {
            centGen.checkAll {
                (it + (-it)) shouldBe Cent.unison
            }
        }

    }

    describe("toCents") {

        it("should return the same value") {
            centGen.checkAll {
                it.toCents() shouldBe it
            }
        }

    }

    describe("toFrequencyRatio") {

        it("should convert unison constant") {
            Cent.unison.toFrequencyRatio() shouldBe FrequencyRatio.unison
        }

        it("should convert octave constant") {
            Cent.octave.toFrequencyRatio() shouldBe FrequencyRatio.octave
        }

        //This is not very tight, since we only keep precision to one cent for cents.
        val tolerance = 1e-4

        it("should convert a just fifth (within rounding error)") {
            val converted = Cent(702).toFrequencyRatio()
            val expected = FrequencyRatio(1.5)
            converted.value shouldBe expected.value.plusOrMinus(tolerance)
        }

        it("should convert a value down by one fourth (within rounding error)") {
            val converted = Cent(-498).toFrequencyRatio()
            val expected = FrequencyRatio(3.0/4.0)
            converted.value shouldBe expected.value.plusOrMinus(tolerance)
        }

    }

})