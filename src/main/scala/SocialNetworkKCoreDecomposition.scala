import java.io.File

import lib.{Distribution, Export, KCoreDecomposition}
import org.apache.log4j.{Level, Logger}
import org.apache.spark.graphx.{Graph, GraphLoader}
import org.apache.spark.sql.SparkSession

import scala.reflect.io.Directory

object SocialNetworkKCoreDecomposition extends HiggsTwitter {
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

        logger.info("K Core decomposition")

        /*val kcoreGraph: Graph[Int, Int] = GraphLoader.edgeListFile(
            spark.sparkContext,
            rootPath + "/kcore.csv"
        )*/

        val kIndexes = KCoreDecomposition.run(socialNetwork)
            .cache()

        Export.vertices(
            kIndexes,
            new Directory(new File(rootPath + "/SocialNetwork/KCoreDecomposition/KIndexes"))
        )

        Export.rdd(
            Distribution.get(kIndexes),
            new Directory(new File(rootPath + "/SocialNetwork/KCoreDecomposition/Distribution"))
        )

        spark.stop()
    }
}
