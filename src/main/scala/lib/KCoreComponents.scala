package lib

import org.apache.spark.graphx.{Graph, PartitionID}
import org.apache.spark.internal.Logging

import scala.reflect.ClassTag

object KCoreComponents extends Logging {

    def run[VD: ClassTag, ED: ClassTag](graph: Graph[VD, ED]) = {
        var degreeGraph: Graph[PartitionID, ED] = graph.outerJoinVertices(graph.degrees) {
            (vid, vd, degree) => degree.getOrElse(0)
        }
            .cache()
        var corenessGraph = graph.mapVertices((vid, _) => 0).vertices
        var k = 1

        while (degreeGraph.numVertices > 0) {
            degreeGraph = degreeGraph.outerJoinVertices(degreeGraph.degrees) {
                (vid, vd, degree) => degree.getOrElse(0)
            }.subgraph(
                vpred = (vid, degree) => degree >= k
            ).cache()

            corenessGraph = corenessGraph.leftJoin(degreeGraph.vertices) {
                (vid, coreness, degree) => {
                    if (degree.isEmpty) coreness else k
                }
            }.cache()

            println("k = " + k)
            println("vertices = " + degreeGraph.numVertices)

            corenessGraph.count()

            k += 1
        }

        corenessGraph
    }
}
