import java.io.File

import ActivityActivatedUsers.rootPath
import lib.{AverageShortestPaths, ClusteringCoefficient, DynamicGraph, Export}
import org.apache.log4j.{Level, Logger}
import org.apache.spark.graphx.{Edge, Graph}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{DataFrame, Dataset, Row, SparkSession}

import scala.reflect.io.Directory

object ActivityDynamicGraph extends HiggsTwitter {

    def main(args: Array[String]) {
        val logger = Logger.getLogger(getClass.getName)
        logger.setLevel(Level.INFO)

        val spark = SparkSession
            .builder
            .master("local[*]")
            .appName(appName)
            .getOrCreate()

        val activityDataFrame: DataFrame = spark.read
            .option("sep", " ")
            .option("header", true)
            .option("inferSchema", true)
            .csv(args(0))
            .cache()

        val activityDynamicGraph: DynamicGraph = new DynamicGraph(
            activityDataFrame,
            "Timestamp",
            60 * 60
        )

        var stats: List[(Int, Long, Long)] = List[(Int, Long, Long)]()

        for (t <- activityDynamicGraph.start to activityDynamicGraph.end by activityDynamicGraph.interval) {
            logger.info(((t - activityDynamicGraph.start) / activityDynamicGraph.interval) + " / " + activityDynamicGraph.numInterval)

            val snapshot: Graph[Int, Int] = activityDynamicGraph.getSnapshotGraph(t)

            stats = stats :+ (t, snapshot.numVertices, snapshot.numEdges)

            /** Many simple parameters exist to describe a static network (number of nodes, edges, path length, connected components),
              * or to describe specific nodes in the graph such as the number of links or the clustering coefficient.
              * These properties can then individually be studied as a time series using signal processing notions
              * */

            /*println("cc ")
            snapshotActivityGraph.connectedComponents()
                .vertices
                .map(_._2)
                .countByValue
                .toSeq
                .sortBy(_._2)
                .reverse
                .take(5)
                .foreach(println)

            val clusteringCoefficient: Double = ClusteringCoefficient.avg(snapshotActivityGraph)
            println("clustering coefficient " + clusteringCoefficient)

            println(snapshotActivityGraph
                .inDegrees
                .sortBy(_._2, ascending = false)
                .map(_._2)
                .stats())

            println("avg shortest paths " + AverageShortestPaths.max(snapshotActivityGraph, 5))*/
        }

        Export.list(
            stats,
            new Directory(new File(rootPath + "/Activity/Dynamic/stats.csv"))
        )

        spark.stop()
    }
}
