package nondeterminism

import genericdatastructures.Point
import java.util.*

object Randomness {
    private val rand = Random()

    fun nextDouble() = rand.nextDouble()

    fun randomSetOfPoints(spaceWidth: Double, spaceHeight: Double) = Array(10000) { index ->
        Point(nextDouble() * spaceWidth, nextDouble() * spaceHeight)
    }.toList()
}