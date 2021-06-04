package net.paploo.kmusical

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.*

class FrequencyTest : DescribeSpec({

    describe("constants") {

        it("should have concert pitch") {
            Frequency.standard.hertz shouldBe 440.0
        }

    }

    describe("properties") {
        val h = 183.92
        val f = Frequency(h)
        f.hertz shouldBe h
    }

})