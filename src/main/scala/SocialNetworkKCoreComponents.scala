import java.io.File

import lib.{Distribution, Export, KCoreComponents}
import org.apache.log4j.{Level, Logger}
import org.apache.spark.graphx.{Graph, GraphLoader, VertexRDD}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

import scala.reflect.io.Directory

object SocialNetworkKCoreComponents extends HiggsTwitter {
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
                args(0)/*"G:\\projets\\HiggsTwitter\\kcore.csv"*/
            )
            .cache()

        logger.info("K Core Components")
        val kIndexes: VertexRDD[Int] = KCoreComponents.run(socialNetwork)
            .cache()

        val degreeKShell: RDD[(Int, Int, Int)] = socialNetwork.degrees
            .leftJoin(kIndexes) {
                (vid, degree, kShell) => {
                    kShell match {
                        case Some(kShell) => {
                            (degree, kShell)
                        }
                        case None => (degree, 0)
                    }
                }
            }
            .map(v => (v._2._1, v._2._2))
            .map(x => (x, 1)).reduceByKey(_ + _)
            .map(v => (v._1._1, v._1._2, v._2))

        Export.vertices(
            kIndexes,
            new Directory(new File(rootPath + "/SocialNetwork/KCoreComponents/KShell"))
        )

        Export.rdd(
            Distribution.get(kIndexes),
            new Directory(new File(rootPath + "/SocialNetwork/KCoreComponents/Distribution"))
        )

        Export.rdd(
            degreeKShell,
            new Directory(new File(rootPath + "/SocialNetwork/KCoreComponents/DegreeKShell"))
        )

        spark.stop()
    }
}
