import java.io.File

import lib.{ClusteringCoefficient, DegreeDistribution, Export}
import org.apache.log4j.{Level, Logger}
import org.apache.spark.graphx.{Edge, Graph}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, SparkSession}

import scala.reflect.io.Directory

object ActivityDegreeDistribution extends HiggsTwitter {

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

        // Degree
        val activityDegrees = activityGraph
                .inDegrees
                .cache()

        logger.info(activityGraph.numVertices)
        logger.info(activityGraph.numEdges)
        logger.info(activityGraph.inDegrees.map(_._2).stats())
        logger.info(activityGraph.outDegrees.map(_._2).stats())

        logger.info("density : " + (activityGraph.numEdges.toDouble / (activityGraph.numVertices.toDouble * (activityGraph.numVertices.toDouble - 1))))

        println(activityDegrees
            .sortBy(_._2, ascending = false)
            .map(_._2)
            .stats())

        println(activityDegrees
            .sortBy(_._2, ascending = false)
            .take(5)
            .foreach(println))

        Export.rdd(
            DegreeDistribution.get(activityGraph),
            new Directory(new File(rootPath + "/Activity/DegreeDistribution"))
        )

        // Clustering coefficient
        val clusteringCoefficient: Double = ClusteringCoefficient.avg(activityGraph)
        println(clusteringCoefficient)

        spark.stop()
    }
}
