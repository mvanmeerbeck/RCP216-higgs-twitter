package lib

import org.apache.spark.graphx.{Graph, VertexRDD}

object ClusteringCoefficient {

    /**
      * Clustering coefficient
      * C(V)=2*t / k(k−1)
      */
    def byVertices(graph: Graph[Int, Int]): Graph[Double, Int] = {
        val triangleGraph: Graph[Int, Int] = graph.triangleCount()
        val totalTriangleGraph: VertexRDD[Double] = {
            graph.degrees.mapValues(d => (d * (d - 1)) / 2.0)
        }

        triangleGraph.outerJoinVertices(totalTriangleGraph)((vertexId, triangleCount, totalTriangle) => {
            totalTriangle match {
                case Some(totalTriangle) => {
                    if (totalTriangle == 0) 0 else triangleCount / totalTriangle
                }
                case None => triangleCount
            }
        })
    }

    def avg(graph: Graph[Int, Int]): Double = {
        byVertices(graph).vertices.map(vertex => vertex._2).sum() / graph.numVertices
    }
}