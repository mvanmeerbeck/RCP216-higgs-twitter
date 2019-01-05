import java.io.{File, PrintWriter}

import lib.AverageShortestPaths
import org.apache.log4j.{Level, Logger}
import org.apache.spark.graphx.{Graph, GraphLoader}
import org.apache.spark.sql.SparkSession

object SocialNetworkShortestPaths extends HiggsTwitter {
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
        val averageShortestPaths = AverageShortestPaths.run(socialNetwork, 100)

        val writer = new PrintWriter(new File(rootPath + "/SocialNetwork/ShortestPaths/data.csv"))

        averageShortestPaths.foreach(avg => {
            writer.write(avg
                .productIterator
                .mkString(","))
            writer.println()
        })

        writer.close()

        spark.stop()
    }
}
