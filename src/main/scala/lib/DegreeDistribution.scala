package lib

import org.apache.spark.graphx.Graph
import org.apache.spark.rdd.RDD

object DegreeDistribution extends Distribution {
    def get(graph: Graph[Int, Int]): RDD[(Int, Float)] = {
        this.get(graph.degrees)
    }
}