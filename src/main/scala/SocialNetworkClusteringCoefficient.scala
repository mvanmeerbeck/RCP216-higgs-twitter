import java.io.File

import SocialNetworkDegreeDistribution.rootPath
import SocialNetworkKCoreComponents.rootPath
import lib.ClusteringCoefficient.byVertices
import lib.{ClusteringCoefficient, DegreeDistribution, Distribution, Export}
import org.apache.log4j.{Level, Logger}
import org.apache.spark.graphx.{Graph, GraphLoader, VertexRDD}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

import scala.reflect.io.Directory

object SocialNetworkClusteringCoefficient extends HiggsTwitter {
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
                args(0)/*"G:\\projets\\HiggsTwitter\\kcore.csv"*/
            )
            .cache()

        val clusteringCoefficients: Graph[Double, Int] = ClusteringCoefficient.byVertices(socialNetwork)
        logger.info(clusteringCoefficients.vertices.map(vertex => vertex._2).sum() / socialNetwork.numVertices)

        clusteringCoefficients
            .vertices
            .sortBy(_._2, ascending = false)
            .take(5)
            .foreach(println)

        val degrees: VertexRDD[Int] = clusteringCoefficients.degrees

        val coeffDegree: Graph[(Double, Int), Int] = clusteringCoefficients.outerJoinVertices(degrees)((vertexId, coeff, degree) => {
            degree match {
                case Some(degree) => {
                    (coeff, degree)
                }
                case None => (coeff, 0)
            }
        })

        val coeffs = coeffDegree.vertices
            .map(v => (v._2._2, v._2._1))
            .aggregateByKey((0, 0.0))(
                (acc, coeff) => (acc._1 + 1, acc._2 + coeff),
                (coeff1, coeff2) => (coeff1._1 + coeff2._1, coeff1._2 + coeff2._2)
            )
            .mapValues(coeff => coeff._2 / coeff._1)

        Export.rdd(
            coeffs,
            new Directory(new File(rootPath + "/SocialNetwork/ClusteringCoefficientDistribution"))
        )

        spark.stop()
    }
}
