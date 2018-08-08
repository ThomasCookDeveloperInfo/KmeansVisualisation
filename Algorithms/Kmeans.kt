package Algorithms

import GUI.HEIGHT
import GUI.WIDTH
import GenericDataStructures.Point
import java.util.*

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