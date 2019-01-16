import java.io.{File, PrintWriter}

import lib.AverageShortestPaths
import org.apache.log4j.{Level, Logger}
import org.apache.spark.graphx.lib.ShortestPaths
import org.apache.spark.graphx.lib.ShortestPaths.SPMap
import org.apache.spark.graphx.{Edge, Graph, GraphLoader, VertexId}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, SparkSession}

object ActivityShortestPaths extends HiggsTwitter {
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

        val activityGraph: Graph[Int, Int] = Graph.fromEdges(edges, 0)
            .cache()

        val vertices: Seq[VertexId] = activityGraph
            .vertices
            .map(_._1)
            .collect()
            .toSeq

        val shortestPaths: Graph[SPMap, Int] = ShortestPaths.run(activityGraph, vertices)
            .cache()

        shortestPaths.vertices.take(1000).foreach(println)

/*
        // Average Shortest Paths
        val averageShortestPaths = AverageShortestPaths.run(activityGraph, 1000)

        val writer = new PrintWriter(new File(rootPath + "/Activity/ShortestPaths/data.csv"))

        averageShortestPaths.foreach(avg => {
            writer.write(avg
                .productIterator
                .mkString(","))
            writer.println()
        })

        writer.close()*/

        spark.stop()
    }
}
