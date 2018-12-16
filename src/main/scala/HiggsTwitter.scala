import org.apache.spark.graphx.{Graph, VertexRDD}

import scala.reflect.io.Directory

abstract class HiggsTwitter extends Serializable {
    protected val appName = "HiggsTwitter"
    protected val rootPath = scala.util.Properties.envOrElse("HOME", "~/" + appName)

    /**
      * Clustering coefficient
      * C(V)=2*t / k(k−1)
      */
    def clusteringCoefficient(graph: Graph[Int, Int]) {
        val triangleGraph: Graph[Int, Int] = graph.triangleCount()

        val totalTriangleGraph: VertexRDD[Double] = graph.degrees.mapValues(d => d * (d - 1) / 2.0)

        val coef: VertexRDD[Double] = triangleGraph.vertices.innerJoin(totalTriangleGraph) {
            (vertexId, triangleCount, totalTriangle) => {
                if (totalTriangle == 0) 0
                else triangleCount / totalTriangle
            }
        }
    }

    def exportDegreeDistribution(graph: Graph[Int, Int], degrees: VertexRDD[Int], directory: Directory): Unit = {
        val verticesCount = graph.vertices.count()

        val graphDegreeDistribution = degrees
            .map(degree => (degree._2, 1F))
            .reduceByKey(_ + _)
            .map(degree => (degree._1, degree._2 / verticesCount))

        directory.deleteRecursively()

        graphDegreeDistribution
            .repartition(1)
            .map(data => formatCsv(data))
            .saveAsTextFile(directory.path)
    }

    def formatCsv(data: Product): String = {
        data
            .productIterator
            .mkString(",")
    }
}
