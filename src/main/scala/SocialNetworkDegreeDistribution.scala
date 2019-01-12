import java.io.File

import lib.{Distribution, DegreeDistribution, Export}
import org.apache.log4j.{Level, Logger}
import org.apache.spark.graphx.{Graph, GraphLoader, VertexId}
import org.apache.spark.sql.SparkSession

import scala.reflect.io.Directory

object SocialNetworkDegreeDistribution extends HiggsTwitter {
    def main(args: Array[String]) {
        val logger = Logger.getLogger(getClass.getName)
        logger.setLevel(Level.INFO)

        val spark: SparkSession = SparkSession
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

        // Degrees
        logger.info("Exporting social network degree distribution")
        Export.rdd(
            DegreeDistribution.get(socialNetwork),
            new Directory(new File(rootPath + "/SocialNetwork/DegreeDistribution"))
        )

        spark.stop()
    }
}
