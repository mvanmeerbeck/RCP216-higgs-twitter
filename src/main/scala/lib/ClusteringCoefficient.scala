package lib

import org.apache.spark.graphx.{Graph, VertexRDD}

object ClusteringCoefficient {

    /**
      * Clustering coefficient
      * C(V)=2*t / k(k−1)
      */
    def byVertices(graph: Graph[Int, Int]): VertexRDD[Double] = {
        val triangleGraph: Graph[Int, Int] = graph.triangleCount()

        val totalTriangleGraph: VertexRDD[Double] = graph.degrees.mapValues(d => d * (d - 1) / 2.0)

        triangleGraph.vertices.innerJoin(totalTriangleGraph) {
            (vertexId, triangleCount, totalTriangle) => {
                if (totalTriangle == 0) 0
                else triangleCount / totalTriangle
            }
        }
    }

    def avg(graph: Graph[Int, Int]): Double = {
        byVertices(graph).map(_._2).sum() / graph.vertices.count()
    }
}