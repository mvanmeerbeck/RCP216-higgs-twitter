import java.io.File

import org.apache.spark.graphx.{Graph, GraphLoader, VertexId}
import org.apache.spark.sql.SparkSession

import scala.reflect.io.Directory

object HiggsTwitter {
    private val appName = "HiggsTwitter"
    private val rootPath = scala.util.Properties.envOrElse("HOME", "~/" + appName)

    def main(args: Array[String]) {
        val spark = SparkSession
            .builder
            .master("local[*]")
            .appName(appName)
            .getOrCreate()

        // Social Network Graph
        val socialNetwork: Graph[Int, Int] = GraphLoader
            .edgeListFile(spark.sparkContext, args(0))
            .cache()

        // Connected components
        val connectedComponents: Graph[VertexId, Int] = socialNetwork
            .connectedComponents()
            .cache()

        val componentCounts: Seq[(VertexId, Long)] = connectedComponents
            .vertices
            .map(_._2)
            .countByValue
            .toSeq
            .sortBy(_._2)
            .reverse

        println(componentCounts.size)
        //componentCounts.take(10).foreach(println)

        // Degrees
        exportDegreeDistribution(
            socialNetwork,
            new Directory(new File(rootPath + "/SocialNetwork/DegreeDistribution"))
        )

        spark.stop()
    }

    def exportDegreeDistribution(graph: Graph[Int, Int], directory: Directory): Unit = {
        val graphDegrees = graph
            .degrees
            .cache()

        val verticesCount = graph.vertices.count()

        val graphDegreeDistribution = graphDegrees
            .map(degree => (degree._2, 1F))
            .reduceByKey(_ + _)
            .map(degree => (degree._1, degree._2 / verticesCount))

        directory.deleteRecursively()

        graphDegreeDistribution
            .repartition(1)
            .map(data => formatCsv(data))
            .saveAsTextFile(directory.path)
    }

    def formatCsv(data: Product): String = {
        data
            .productIterator
            .mkString(",")
    }
}
