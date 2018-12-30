package lib

import org.apache.spark.graphx.VertexRDD
import org.apache.spark.rdd.RDD

class Distribution {
    def get(vertices: VertexRDD[Int]): RDD[(Int, Float)] = {
        val verticesCount = vertices.count()

        vertices
            .map(value => (value._2, 1F))
            .reduceByKey(_ + _)
            .map(value => (value._1, value._2 / verticesCount))
    }
}

object Distribution extends Distribution {

}