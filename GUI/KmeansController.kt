package GUI

import javafx.animation.AnimationTimer
import javafx.fxml.FXML
import javafx.scene.canvas.Canvas
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.control.Button
import java.util.*

// The controller for kmeans
class KmeansController(@FXML private var mainPane: VBox? = null,
                       @FXML private var canvas: Canvas? = null,
                       @FXML private var resetButton: Button? = null) {
    // Map each possible cluster index to a unique color
    private val clusterIndexColorMap = mapOf<Int, Color>(Pair(0, Color.BLACK), Pair(1, Color.GREEN), Pair(2, Color.RED),
            Pair(3, Color.CYAN), Pair(4, Color.FUCHSIA), Pair(5, Color.BROWN), Pair(6, Color.DARKGREEN), Pair(7, Color.DARKMAGENTA), Pair(8, Color.GOLD), Pair(9, Color.HOTPINK))

    // Kmeans
    private var kmeans = Kmeans()

    // Frame timer for running the kmeans algorithm and drawing result of current step
    private val frameTimer = object : AnimationTimer() {
        private var frameTicker = -1

        override fun handle(now: Long) {
            if (frameTicker == -1) {
                // Invalidate
                invalidate(kmeans.step())
                frameTicker++
            } else {
                frameTicker++
                if (frameTicker == 1) {
                    frameTicker = 0

                    // Invalidate
                    invalidate(kmeans.step())
                }
            }
        }
    }

    @FXML
    fun initialize() {
        // Setup the canvas to resize itself based on the parent frame
        mainPane?.heightProperty()?.addListener { _, _, newValue ->
            canvas?.let {
                val settingsHeight = resetButton?.prefHeight ?: 0.0
                it.height = newValue.toDouble() - settingsHeight
            }
        }
        mainPane?.widthProperty()?.addListener { _, _, newValue ->
            canvas?.let {
                it.width = newValue.toDouble()
            }
        }

        // Hook reset button up
        resetButton?.setOnMouseClicked {
            frameTimer.stop()
            kmeans = Kmeans()
            frameTimer.start()
        }

        // Start the frame timer
        frameTimer.start()
    }

    // Invalidate the canvas and redraw everything
    private fun invalidate(clusters: Collection<Kmeans.Cluster>) {
        canvas?.let { canvas ->
            // Get the canvas dimensions
            val canvasWidth = canvas.width
            val canvasHeight = canvas.height

            // Get the context
            val context = canvas.graphicsContext2D ?: return

            // Clear the context
            context.clearRect(0.0, 0.0, canvasWidth, canvasHeight)

            // Draw to canvas
            context.apply {
                clusters.forEachIndexed { index, cluster ->
                    val color = clusterIndexColorMap[index]
                    fill = color
                    stroke = color

                    strokeOval(cluster.centroid.x, cluster.centroid.y, 10.0, 10.0)
                    cluster.dataPoints.forEach {
                        fillOval(it.x, it.y, 10.0, 10.0)
                    }
                }
            }
        }
    }
}

data class Point(val x: Double, val y: Double)

private open class BinaryTreeNode<T>(open var data: T,
                                     open var left: BinaryTreeNode<T>? = null,
                                     open var right: BinaryTreeNode<T>? = null) where T : Comparable<T>

private abstract class BinaryTree<T>(protected open val root: BinaryTreeNode<T>) where T : Comparable<T> {
    abstract fun insert(data: T, current: BinaryTreeNode<T>? = root) : BinaryTreeNode<T>
    abstract fun find(data: T, current: BinaryTreeNode<T>? = root) : BinaryTreeNode<T>?
    abstract fun delete(data: T, current: BinaryTreeNode<T>? = root) : BinaryTreeNode<T>?
}

private open class SimpleBinaryTree<T> (override val root: BinaryTreeNode<T>) : BinaryTree<T>(root) where T : Comparable<T> {
    override fun insert(data: T, current: BinaryTreeNode<T>?) : BinaryTreeNode<T> {
        if (current === null)
            return BinaryTreeNode(data)

        if (current.data < data) {
            current.right = insert(data, current.right)
        } else {
            current.left = insert(data, current.left)
        }
        return current
    }

    override fun find(data: T, current: BinaryTreeNode<T>?) : BinaryTreeNode<T>? {
        if (current === null)
            return null

        return if (current.data == data) {
            current
        } else {
            val left = find(data, current.left)
            if (left !== null) {
                left
            } else {
                find(data, current.right)
            }
        }
    }

    override fun delete(data: T, current: BinaryTreeNode<T>?) : BinaryTreeNode<T>? {
        if (current === null)
            return current

        when {
            data < current.data -> current.left = delete(data, current.left)
            data > current.data -> current.right = delete(data, current.right)
            else -> {
                when {
                    current.left === null -> return current.right
                    current.right === null -> return current.left
                    else -> {
                        current.data = minValue(current.right!!)
                        current.right = delete(current.data, current.right)
                    }
                }
            }
        }

        return current
    }

    private fun minValue(from: BinaryTreeNode<T>) : T {
        return from.left?.let {
            minValue(it)
        } ?: from.data
    }
}

class Kmeans {
    data class Cluster(val centroid: Point, val dataPoints: MutableCollection<Point>)

    private object Randomness {
        private val rand = Random()
        fun nextDouble() = rand.nextDouble()
    }

    private val k = 10
    private val dataSet = Array(10000) { index ->
        Point(Randomness.nextDouble() * WIDTH, Randomness.nextDouble() * HEIGHT)
    }.toList()

    private var centroids = Array(k) { _ ->
        Point(Randomness.nextDouble() * WIDTH, Randomness.nextDouble() * HEIGHT)
    }.toList()

    private var clusters = clusterDataSet()

    fun step() : Collection<Cluster> {
        clusters = clusterDataSet()
        centroids = mapClustersMeansToCentroids()
        return clusters
    }

    private fun clusterDataSet() : Collection<Cluster> {
        val newClusters = mutableListOf<Cluster>()

        dataSet.forEach { dataPoint ->
            var shortestDistance = Double.MAX_VALUE
            var closestCentroidIndex = 0
            centroids.forEachIndexed { index, centroid ->
                val dx = dataPoint.x - centroid.x
                val dy = dataPoint.y - centroid.y
                val euclidianDistance = Math.sqrt(Math.pow(dx, 2.0) + Math.pow(dy, 2.0))
                if (euclidianDistance < shortestDistance) {
                    shortestDistance = euclidianDistance
                    closestCentroidIndex = index
                }
            }
            val filteredClusters = newClusters.filter { it.centroid == centroids.elementAt(closestCentroidIndex) }
            if (filteredClusters.isNotEmpty()) {
                filteredClusters.first().dataPoints.add(dataPoint)
            } else {
                newClusters.add(Cluster(centroids.elementAt(closestCentroidIndex), mutableListOf(dataPoint)))
            }
        }
        return newClusters
    }

    private fun mapClustersMeansToCentroids() = clusters.map {
        var sumX = 0.0
        var sumY = 0.0
        it.dataPoints.forEach {
            sumX += it.x
            sumY += it.y
        }
        val meanX = sumX / it.dataPoints.size
        val meanY = sumY / it.dataPoints.size
        Point(meanX, meanY)
    }
}

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
        override fun
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
                        beachline = BinaryTree(BinaryTreeNode<BeachLineEntity>(Parabola(currentEvent.origin)))
                    }.invoke()
                }
                else -> {

                }
            }
        }
    }

    private fun processDataPointEvent(event: DataPointEvent) {
        val interceptedParabola = dataPointParabolicIntercept(event)
        if (arc has generated circle event) {
            remove circle event from queue
        }
        split appart currentArc into arcLeft and arcRight
        insert new arc into wedge
        checkForCircleEvent(arcLeft)
        checkForCircleEvent(arcRight)
    }

    private fun dataPointParabolicIntercept(dataPoint: DataPointEvent) : Parabola {
        beachline?.let {
            it.find()
        }
    }
}