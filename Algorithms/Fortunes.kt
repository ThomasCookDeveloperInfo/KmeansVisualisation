package Algorithms

import GenericDataStructures.BinaryTree
import GenericDataStructures.BinaryTreeNode
import GenericDataStructures.Point
import java.util.*

class Fortunes(private val dataPoints: Collection<Point>, private val width: Double, private val step: Double) {

    private open class Event(open val origin: Point)
    private class CircleEvent(override val origin: Point) : Event(origin)
    private class DataPointEvent(override val origin: Point) : Event(origin)

    private interface BeachLineEntity : Comparable<BeachLineEntity>
    private class Parabola(val origin: Point) : BeachLineEntity {
        override fun compareTo(other: BeachLineEntity): Int {
            return 0
        }
    }
    private class Edge(val pointA: Point, val pointB: Point) : BeachLineEntity {
        override fun compareTo(other: BeachLineEntity): Int {
            return 0
        }
    }

    private class FortuneBinaryTreeNode(override var data: BeachLineEntity,
                                        override var left: BinaryTreeNode<BeachLineEntity>? = null,
                                        override var right: BinaryTreeNode<BeachLineEntity>? = null,
                                        var parent: FortuneBinaryTreeNode) : BinaryTreeNode<BeachLineEntity>(data, left, right)

    private open class FortuneBinaryTree(override val root: BinaryTreeNode<BeachLineEntity>) : BinaryTree<BeachLineEntity>(root) {
        /**
         * Recursively insert the [data] which is a [BeachLineEntity] into the tree.
         * This is achieved by applying [dataPointParabolicIntercept] to search the tree for the [Parabola] leaf.
         * Once found, the leaf is replaced with a [FortuneBinaryTree] sub-tree where the root is the [Edge] between the two
         */
        override fun insert(data: BeachLineEntity, current: BinaryTreeNode<BeachLineEntity>?): BinaryTreeNode<BeachLineEntity> {

        }

        override fun delete(data: BeachLineEntity, current: BinaryTreeNode<BeachLineEntity>?): BinaryTreeNode<BeachLineEntity>? {

        }

        override fun find(data: BeachLineEntity, current: BinaryTreeNode<BeachLineEntity>?): BinaryTreeNode<BeachLineEntity>? {

        }
    }

    // Event queue
    private val eventQueue =
            PriorityQueue<Event>().also { it.addAll(dataPoints.sortedBy { it.x }.map { DataPointEvent(it) } ) }

    // Beachline
    private var beachline: BinaryTree<BeachLineEntity>? = null

    fun step() {
        if (eventQueue.peek() !== null) {
            val currentEvent = eventQueue.poll()
            when (currentEvent) {
                is DataPointEvent -> {
                    beachline?.let {

                    } ?: {
                        beachline = FortuneBinaryTree(FortuneBinaryTreeNode(Parabola(currentEvent.origin)))
                    }.invoke()
                }
                else -> {

                }
            }
        }
    }

    private fun processDataPointEvent(event: DataPointEvent) {
        // val interceptedParabola = dataPointParabolicIntercept(event)
        // if (arc has generated circle event) {
        //     remove circle event from queue
        // }
        // split appart currentArc into arcLeft and arcRight
        // insert new arc into wedge
        // checkForCircleEvent(arcLeft)
        // checkForCircleEvent(arcRight)
    }

    private fun dataPointParabolicIntercept(dataPoint: DataPointEvent) : Parabola {
        beachline?.let {
            it.find()
        }
    }
}