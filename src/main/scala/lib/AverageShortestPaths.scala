package lib

import org.apache.spark.graphx.lib.ShortestPaths
import org.apache.spark.graphx.lib.ShortestPaths.SPMap
import org.apache.spark.graphx.{Graph, VertexId}
import org.apache.spark.internal.Logging

import scala.reflect.ClassTag

object AverageShortestPaths extends Logging {

    def run[VD: ClassTag, ED: ClassTag](graph: Graph[VD, ED], numIter: Int): Seq[Double] = {
        val vertices: Seq[VertexId] = graph
            .vertices
            .map(_._1)
            .takeSample(false, numIter)
            .toSeq
        val verticesIterator: Iterator[VertexId] = vertices
            .iterator
        var avgShortestPaths: Seq[Double] = Seq()

        while (verticesIterator.hasNext) {
            val landmarks = Seq(verticesIterator.next())
            val shortestPaths: Graph[SPMap, ED] = ShortestPaths.run(graph, landmarks)
                .cache()

            avgShortestPaths = avgShortestPaths :+ shortestPaths
                .vertices
                .map(vertex => {
                    vertex._2.values.sum.toDouble / landmarks.size.toDouble
                })
                .sum / shortestPaths.numVertices
        }

        avgShortestPaths
    }

    def avg[VD: ClassTag, ED: ClassTag](graph: Graph[VD, ED], numIter: Int): Double = {
        run(graph, numIter).sum / numIter
    }
}
