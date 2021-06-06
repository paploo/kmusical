package net.paploo.kmusical.core

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.shouldBe
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.checkAll
import kotlin.math.absoluteValue

class FrequencyRatioTest : DescribeSpec({

    val epsilon = 1e-15

    val frequencyRatioGen = arbitrary { rs ->
        FrequencyRatio(rs.random.nextDouble().absoluteValue)
    }

    describe("intantiation") {

        it("should not allow negative values") {
            shouldThrowAny { Frequency(-0.5) }
            shouldThrowAny { Frequency(-2.0) }
        }

        it("should not instantiate with a zero frequency") {
            shouldThrowAny { Frequency(0.0) }
        }

    }

    describe("plus") {

        it("should return a FrequencyRatio (covariance)") {
            val a = FrequencyRatio(1.5)
            val b = FrequencyRatio(0.6667)
            val r: FrequencyRatio = a + b
            r.value shouldBe ((a.value * b.value) plusOrMinus epsilon)
        }

        describe("it should be monoidal") {

            it("should be associative") {
                val iterations = 10
                checkAll(iterations, frequencyRatioGen) { a ->
                    checkAll(iterations, frequencyRatioGen) { b ->
                        checkAll(iterations, frequencyRatioGen) { c ->
                            val left = (a + b) + c
                            val right = a + (b + c)
                            left.value shouldBe (right.value.plusOrMinus(epsilon))
                        }
                    }
                }
            }

            it("should have left identity") {
                checkAll(frequencyRatioGen) {
                    it + FrequencyRatio.unison shouldBe it
                }
            }

            it("should have right identity") {
                checkAll(frequencyRatioGen) {
                    FrequencyRatio.unison + it shouldBe it
                }
            }

        }

    }

    describe("minus") {
        //TODO: Need to implement.
    }

    describe("unaryMinus") {

        it("should be the additive inverse") {
            checkAll(frequencyRatioGen) {
                (it + (-it)).value shouldBe ((FrequencyRatio.unison).value.plusOrMinus(epsilon))
            }
        }

    }

    describe("toCents") {

    }

    describe("toFrequencyRatio") {

    }

})

class CentTest : DescribeSpec({

})