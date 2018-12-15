import java.io.File
import org.apache.spark.graphx.{Graph, GraphLoader, VertexId}
import org.apache.spark.sql.SparkSession
import scala.reflect.io.Directory

object HiggsTwitter {
    private val appName = "HiggsTwitter"
    private val rootPath = scala.util.Properties.envOrElse("HOME", "~/" + appName)
    private val socialNetworkDegreeDistributionDirectory = new Directory(new File(rootPath + "/SocialNetwork/DegreeDistribution"))

    def main(args: Array[String]) {
        val spark = SparkSession
            .builder
            .master("local[*]")
            .appName(appName)
            .getOrCreate()
        println(this.getClass.getName)
        socialNetworkDegreeDistributionDirectory.deleteRecursively()

        // Social Network Graph
        val socialNetwork: Graph[Int, Int] = GraphLoader
            .edgeListFile(spark.sparkContext, args(0))
            .cache()

        println(socialNetwork.edges.count())
        println(socialNetwork.vertices.count())

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
        val socialNetworkDegrees = socialNetwork
            .degrees
            .cache()

        val userCount = socialNetwork.vertices.count()

        val socialNetworkDegreeDistribution = socialNetworkDegrees
            .map(degree => (degree._2, 1F))
            .reduceByKey(_ + _)
            .map(degree => (degree._1, degree._2 / userCount))
            .sortBy(_._1, false)

        socialNetworkDegreeDistribution
            .repartition(1)
            .map(data => formatCsv(data))
            .saveAsTextFile(socialNetworkDegreeDistributionDirectory.path)

        println(socialNetworkDegrees.map(_._2).stats())

        spark.stop()
    }

    def formatCsv(data: Product): String = {
        data
            .productIterator
            .mkString(",")
    }
}
