package GUI

import Algorithms.Kmeans
import javafx.animation.AnimationTimer
import javafx.fxml.FXML
import javafx.scene.canvas.Canvas
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.control.Button

// The controller for kmeans
class KmeansController(@FXML private var mainPane: VBox? = null,
                       @FXML private var canvas: Canvas? = null,
                       @FXML private var resetButton: Button? = null) {

    // Map each possible cluster index to a unique color
    private val clusterIndexColorMap = mapOf(
            Pair(0, Color.BLACK),
            Pair(1, Color.GREEN),
            Pair(2, Color.RED),
            Pair(3, Color.CYAN),
            Pair(4, Color.FUCHSIA),
            Pair(5, Color.BROWN),
            Pair(6, Color.DARKGREEN),
            Pair(7, Color.DARKMAGENTA),
            Pair(8, Color.GOLD),
            Pair(9, Color.HOTPINK))

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