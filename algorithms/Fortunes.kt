package algorithms

import genericdatastructures.BinaryTree
import genericdatastructures.BinaryTreeNode
import genericdatastructures.Point
import java.util.*

class Fortunes(private val dataPoints: Collection<Point>) {

    private open class Event(open val origin: Point)
    private class SiteEvent(override val origin: Point, val parabola: Parabola? = null) : Event(origin)
    private class CircleEvent(override val origin: Point) : Event(origin)

    private class Edge(val pointA: Point, val pointB: Point, val left: Point, val right: Point, val f: Double, val g: Double, val neighbour: Edge)

    private class Parabola(var focus: Point? = null,
                           var isLeaf: Boolean = false,
                           var edge: Edge? = null,
                           var event: CircleEvent? = null,
                           var parent: Parabola? = null) : Comparable<Parabola> {

        override fun compareTo(other: Parabola): Int {
            return 0
        }
    }

    private class FortuneBinaryTreeNode(override var data: Parabola,
                                        override var left: BinaryTreeNode<Parabola>? = null,
                                        override var right: BinaryTreeNode<Parabola>? = null,
                                        var parent: FortuneBinaryTreeNode? = null) : BinaryTreeNode<Parabola>(data, left, right)

    private open class FortuneBinaryTree(override val root: BinaryTreeNode<Parabola>) : BinaryTree<Parabola>(root) {
        override fun insert(data: Parabola, current: BinaryTreeNode<Parabola>?): BinaryTreeNode<Parabola> {
            return current!!
        }

        override fun delete(data: Parabola, current: BinaryTreeNode<Parabola>?): BinaryTreeNode<Parabola>? {
            return current
        }

        override fun find(data: Parabola, current: BinaryTreeNode<Parabola>?): BinaryTreeNode<Parabola>? {
            return current
        }
    }

    // Event queue
    private val eventQueue =
            PriorityQueue<Event>().also { it.addAll(dataPoints.sortedBy { it.y }.map { SiteEvent(it) } ) }

    // Beachline
    private var beachline: BinaryTree<Parabola>? = null

    fun step() {
        if (eventQueue.peek() !== null) {
            val currentEvent = eventQueue.poll()
            when (currentEvent) {
                is SiteEvent -> {
                    beachline?.insert(Parabola(currentEvent.origin)) ?: {
                        beachline = FortuneBinaryTree(FortuneBinaryTreeNode(Parabola(currentEvent.origin)))
                    }.invoke()
                }
                else -> {
                    beachline?.delete(Parabola(currentEvent.origin))
                }
            }
        }
    }
}