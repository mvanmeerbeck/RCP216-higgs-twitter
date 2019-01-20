import java.io.File

import lib.{DynamicGraph, Export}
import org.apache.log4j.{Level, Logger}
import org.apache.spark.graphx.Graph
import org.apache.spark.sql.{DataFrame, SparkSession}

import scala.reflect.io.Directory

object ActivityDynamicGraphDegree extends HiggsTwitter {

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

        var stats: List[(Int, Int, Int)] = List[(Int, Int, Int)]()

        for (t <- activityDynamicGraph.start to activityDynamicGraph.end by activityDynamicGraph.interval) {
            logger.info(((t - activityDynamicGraph.start) / activityDynamicGraph.interval) + " / " + activityDynamicGraph.numInterval)

            val snapshot: Graph[Int, Int] = activityDynamicGraph.getSnapshotGraph(t)

            stats = stats :+ (t, snapshot.inDegrees.map(_._2).max(), snapshot.outDegrees.map(_._2).max())
        }

        Export.list(
            stats,
            new Directory(new File(rootPath + "/Activity/Dynamic/degrees.csv"))
        )

        spark.stop()
    }
}
