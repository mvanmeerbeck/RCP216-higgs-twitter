import java.io.File

import lib.{ClusteringCoefficient, Export}
import org.apache.log4j.{Level, Logger}
import org.apache.spark.graphx._
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, SparkSession}

import scala.reflect.io.Directory

object ActivityClusteringCoefficient extends HiggsTwitter {
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

        val clusteringCoefficients: Graph[Double, Int] = ClusteringCoefficient.byVertices(activityGraph)
        logger.info(clusteringCoefficients.vertices.map(vertex => vertex._2).sum() / activityGraph.numVertices)

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
            new Directory(new File(rootPath + "/Activity/ClusteringCoefficientDistribution"))
        )

        spark.stop()
    }
}
