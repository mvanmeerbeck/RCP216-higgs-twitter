import org.apache.spark.graphx.{Graph, VertexRDD}

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
}
