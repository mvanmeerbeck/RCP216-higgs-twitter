import java.io.{File, PrintWriter}

import lib.Export.formatCsv
import lib.{DegreeDistribution, DynamicGraph, Export}
import org.apache.log4j.{Level, Logger}
import org.apache.spark.graphx.{Graph, GraphOps, VertexRDD}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, SparkSession}

import scala.reflect.io.Directory

object ActivityDynamicGraphDegreeDistribution extends HiggsTwitter {

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

        var stats: List[(Int, Int, Float)] = List[(Int, Int, Float)]()
        val directory = new Directory(new File(rootPath + "/Activity/Dynamic/degree_distribution.csv"))
        directory.deleteRecursively()
        val writer = new PrintWriter(new File(directory.path))

        for (t <- activityDynamicGraph.start to activityDynamicGraph.end by activityDynamicGraph.interval) {
            logger.info(((t - activityDynamicGraph.start) / activityDynamicGraph.interval) + " / " + activityDynamicGraph.numInterval)

            val snapshot: Graph[Int, Int] = activityDynamicGraph.getSnapshotGraph(t)

            val snapshotDegrees: VertexRDD[Int] = snapshot
                .inDegrees
                .cache()

            val degreeDistribution: RDD[(Int, Float)] = DegreeDistribution.get(snapshotDegrees)
                .cache()

            degreeDistribution
                .repartition(1)
                .collect()
                .foreach(degree => {
                    writer.write(formatCsv((t, degree._1, degree._2)))
                    writer.println()

                    stats = stats :+ (t, degree._1, degree._2)
                })

            writer.println()
            writer.println()
        }

        writer.close()

        /*Export.list(
            stats,
            new Directory(new File(rootPath + "/Activity/Dynamic/degree_distribution.csv"))
        )*/

        spark.stop()
    }
}
