import lib.AverageShortestPaths
import org.apache.log4j.{Level, Logger}
import org.apache.spark.graphx.{Graph, GraphLoader}
import org.apache.spark.sql.SparkSession

object SocialNetworkShortestPath extends HiggsTwitter {
    def main(args: Array[String]) {
        val logger = Logger.getLogger(getClass.getName)
        logger.setLevel(Level.INFO)

        val spark = SparkSession
            .builder
            .master("local[*]")
            .appName(appName)
            .getOrCreate()

        // Social Network Graph
        logger.info("Loading social network graph")

        val socialNetwork: Graph[Int, Int] = GraphLoader
            .edgeListFile(
                spark.sparkContext,
                args(0)
            )
            .cache()

        // Average Shortest Paths
        val averageShortestPaths = AverageShortestPaths.run(socialNetwork, 2)
        averageShortestPaths.foreach(println)

        spark.stop()
    }
}
