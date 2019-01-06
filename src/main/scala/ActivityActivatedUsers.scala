import java.io.File

import ActivityDegreeDistribution.rootPath
import lib.Export.formatCsv
import lib._
import org.apache.log4j.{Level, Logger}
import org.apache.spark.graphx.{Edge, Graph}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{DataFrame, Dataset, Row, SparkSession}

import scala.reflect.io.Directory

object ActivityActivatedUsers extends HiggsTwitter {

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
        var activatedUsers: List[(Int, Long)] = List[(Int, Long)]()

        for (t <- activityDynamicGraph.start to activityDynamicGraph.end by activityDynamicGraph.interval) {
            logger.info(((t - activityDynamicGraph.start) / activityDynamicGraph.interval) + " / " + activityDynamicGraph.numInterval)

            val snapshot: Graph[Int, Int] = activityDynamicGraph.getSnapshotGraph(t)

            activatedUsers = activatedUsers :+ (t, snapshot.outDegrees.count())
        }

        Export.list(
            activatedUsers,
            new Directory(new File(rootPath + "/Activity/ActivatedUsers/data.csv"))
        )

        spark.stop()
    }
}
