package nondeterminism

import genericdatastructures.Point
import java.util.*

object Randomness {
    private val rand = Random()

    fun nextDouble() = rand.nextDouble()

    fun randomSetOfPoints(size: Int, spaceWidth: Double, spaceHeight: Double) = Array(size) { index ->
        Point(nextDouble() * spaceWidth, nextDouble() * spaceHeight)
    }.toList()
}