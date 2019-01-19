import org.apache.log4j.{Level, Logger}
import org.apache.spark.graphx.{Edge, Graph}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, SparkSession}

object ActivityPageRank extends HiggsTwitter {

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

        // PageRank
        val ranks = activityGraph.pageRank(0.0001).vertices
        ranks
            .sortBy(_._2, ascending = false)
            .take(5)
            .foreach(println)

        spark.stop()
    }
}
