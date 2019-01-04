import java.io.File

import lib._
import org.apache.log4j.{Level, Logger}
import org.apache.spark.graphx.{Edge, Graph}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, SparkSession}

import scala.reflect.io.Directory

object ActivityKCoreComponents extends HiggsTwitter {

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

        val edges: RDD[Edge[Int]] = activityDataFrame
            .rdd
            .map(row => Edge(row.getInt(0), row.getInt(1)))

        val activityGraph: Graph[Int, Int] = Graph.fromEdges(edges, 0)
                .cache()

        // K-core Decomposition
        val kIndexes = KCoreComponents.run(activityGraph)
            .cache()

        Export.vertices(
            kIndexes,
            new Directory(new File(rootPath + "/Activity/KCoreComponents/KIndexes"))
        )

        Export.rdd(
            Distribution.get(kIndexes),
            new Directory(new File(rootPath + "/Activity/KCoreComponents/Distribution"))
        )

        spark.stop()
    }
}
