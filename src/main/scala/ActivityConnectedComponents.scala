import org.apache.log4j.{Level, Logger}
import org.apache.spark.graphx.{Edge, Graph, GraphLoader, VertexId}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, SparkSession}

object ActivityConnectedComponents extends HiggsTwitter {
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

        // Connected components
        logger.info("Building activity connected components")

        val connectedComponents: Graph[VertexId, Int] = activityGraph
            .connectedComponents()
            .cache()

        val componentCounts: Seq[(VertexId, Long)] = connectedComponents
            .vertices
            .map(_._2)
            .countByValue
            .toSeq
            .sortBy(_._2)
            .reverse

        logger.info("Number of users : " + activityGraph.numVertices)
        logger.info("Number of connected components : " + componentCounts.size)
        logger.info("Ratio connected components / user : " + componentCounts.size.toDouble / activityGraph.numVertices.toDouble)
        componentCounts.take(1).foreach(
            component => {
                logger.info("Largest connected components : " + component._2)
                logger.info("Ratio largest connected components / users : " + component._2.toDouble / activityGraph.numVertices.toDouble)
            }
        )

        spark.stop()
    }
}
