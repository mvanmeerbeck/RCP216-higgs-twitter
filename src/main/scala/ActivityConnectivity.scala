import lib.MaxFlow
import org.apache.log4j.{Level, Logger}
import org.apache.spark.graphx.{Edge, Graph, GraphLoader, VertexId}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, SparkSession}

object ActivityConnectivity extends HiggsTwitter {
    def main(args: Array[String]) {
        val logger = Logger.getLogger(getClass.getName)
        logger.setLevel(Level.INFO)

        val spark = SparkSession
            .builder
            .master("local[*]")
            .appName(appName)
            .getOrCreate()

        // Social Network Graph
        logger.info("Loading activity graph")

        val activityDataFrame: DataFrame = spark.read
            .option("sep", " ")
            .option("header", true)
            .option("inferSchema", true)
            .csv(args(0))

        val edges: RDD[Edge[Int]] = activityDataFrame
            .rdd
            .map(row => Edge(row.getInt(0), row.getInt(1)))

        val activityGraph: Graph[VertexId, Int] = Graph.fromEdges(edges, 0)
            .mapVertices((vid, attr) => attr.toLong)
            .cache()

        val vertices: Array[(VertexId, VertexId)] = activityGraph.vertices.takeSample(false, 2)
        val sourceId: VertexId = vertices.head._1
        val targetId: VertexId = vertices.last._1
        println(sourceId)
        println(targetId)
        val flows: RDD[((VertexId, VertexId), Int)] = MaxFlow.run(spark.sparkContext, vertices.head._1, vertices.last._1, activityGraph, 0)

        val emanating = flows.filter(e => e._1._1 == sourceId).map(e => (e._1._1,e._2)).reduceByKey(_ + _).collect
        println("Max Flow: ")
        println(emanating(0)._2)

        spark.stop()
    }
}
