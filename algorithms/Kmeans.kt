package algorithms

import genericdatastructures.Point
import nondeterminism.Randomness

class Kmeans(private val dataSet: Collection<Point>,
             private val dataSpaceWidth: Double = {
                 val sortedByX = dataSet.sortedBy { it.x }
                 sortedByX.last().x - sortedByX.first().x
             }.invoke(),
             private val dataSpaceHeight: Double = {
                 val sortedByY = dataSet.sortedBy { it.y }
                 sortedByY.last().y - sortedByY.first().y
             }.invoke()) {

    data class Cluster(val centroid: Point, val dataPoints: MutableCollection<Point>)

    private val k = 10

    private var centroids = Array(k) { _ ->
        Point(Randomness.nextDouble() * dataSpaceWidth, Randomness.nextDouble() * dataSpaceHeight)
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