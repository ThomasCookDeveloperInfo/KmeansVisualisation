package GUI

import javafx.animation.AnimationTimer
import javafx.fxml.FXML
import javafx.scene.canvas.Canvas
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import java.util.*

// The controller for kmeans
class KmeansController(@FXML private var mainPane: VBox? = null,
                       @FXML private var canvas: Canvas? = null) {

    private val clusterIndexColorMap = mapOf<Int, Color>(Pair(0, Color.BLACK), Pair(1, Color.GREEN), Pair(2, Color.RED),
            Pair(3, Color.CYAN), Pair(4, Color.FUCHSIA), Pair(5, Color.BROWN), Pair(6, Color.DARKGREEN), Pair(7, Color.DARKMAGENTA), Pair(8, Color.GOLD), Pair(9, Color.HOTPINK))

    @FXML
    fun initialize() {
        // Setup the canvas to resize itself based on the parent frame
        mainPane?.heightProperty()?.addListener { _, _, newValue ->
            canvas?.let {
                it.height = newValue.toDouble()
            }
        }
        mainPane?.widthProperty()?.addListener { _, _, newValue ->
            canvas?.let {
                it.width = newValue.toDouble()
            }
        }

        // Frame timer for running the kmeans algorithm and drawing result of current step
        object : AnimationTimer() {
            // Create instance of kmeans
            private val kmeans = Algorithms.kmeans()
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
        }.start()
    }

    // Invalidate the canvas and redraw everything
    private fun invalidate(clusters: Collection<Cluster>) {
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

data class Vector(val x: Double, val y: Double)
data class Cluster(val centroid: Vector, val dataPoints: MutableCollection<Vector>)

object Algorithms {
    class kmeans {
        private val k = 10
        private val dataSet = Array(100000) { index ->
            Vector(Randomness.nextDouble() * WIDTH, Randomness.nextDouble() * HEIGHT)
        }.toList()

        private var centroids = Array(k) { _ ->
            Vector(Randomness.nextDouble() * WIDTH / 4, Randomness.nextDouble() * HEIGHT / 4)
        }.toList()

        private var clusters = clusterDataSet()

        fun step() : Collection<Cluster> {
            clusters = clusterDataSet()
            centroids = calculateCentroids()
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

        private fun calculateCentroids() = clusters.map {
            var sumX = 0.0
            var sumY = 0.0
            it.dataPoints.forEach {
                sumX += it.x
                sumY += it.y
            }
            val meanX = sumX / it.dataPoints.size
            val meanY = sumY / it.dataPoints.size
            Vector(meanX, meanY)
        }
    }
}

object Randomness {
    private val rand = Random()
    fun nextDouble() = rand.nextDouble()
}