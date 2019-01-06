import lib.{AverageShortestPaths, ClusteringCoefficient}
import org.apache.log4j.{Level, Logger}
import org.apache.spark.graphx.{Edge, Graph}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{DataFrame, Dataset, Row, SparkSession}

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

        val interval = 60 * 60

        var Row(start: Int, end: Int) = activityDataFrame
            .agg(min("Timestamp"), max("Timestamp"))
            .head

        start = start - (start % interval)
        end = end - (end % interval)

        for (t <- start to end by interval) {
            logger.info(t + " / " + (end - start) / interval)

            val actions: Dataset[Row] = activityDataFrame
                .withColumn("Range", col("Timestamp") - (col("Timestamp") % interval))
                .filter("Range <= " + t)

            val edges: RDD[Edge[Int]] = actions
                .rdd
                .map(row => Edge(row.getInt(0), row.getInt(1)))

            val snapshotActivityGraph: Graph[Int, Int] = Graph.fromEdges(edges, 0)
                .cache()

            /** Many simple parameters exist to describe a static network (number of nodes, edges, path length, connected components),
              * or to describe specific nodes in the graph such as the number of links or the clustering coefficient.
              * These properties can then individually be studied as a time series using signal processing notions
              * */
            println("vertices " + snapshotActivityGraph.numVertices)
            println("activated users " + snapshotActivityGraph.numVertices)
            println("edges " + snapshotActivityGraph.numEdges)
            println("cc ")
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

            println("avg shortest paths " + AverageShortestPaths.max(snapshotActivityGraph, 5))
        }

        spark.stop()
    }
}
