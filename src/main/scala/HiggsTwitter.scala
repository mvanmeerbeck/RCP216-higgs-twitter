import org.apache.spark.graphx.{Graph, GraphLoader, VertexId}
import org.apache.spark.sql.SparkSession

object HiggsTwitter {
    def main(args: Array[String]) {
        val spark = SparkSession
            .builder
            .master("local[*]")
            .appName("HiggsTwitter")
            .getOrCreate()

        /*        val socialNetworkSchema = new StructType()
                  .add("userA", IntegerType)
                  .add("userB", IntegerType)

                var socialNetworkData = spark.read
                  .option("delimiter", " ")
                  .schema(socialNetworkSchema)
                  .csv(args(0))*/

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

        val degreeDistribution = socialNetworkDegrees
            .map(degree => (degree._2, 1L))
            .reduceByKey(_ + _)
            .sortBy(_._1, false)

        degreeDistribution.repartition(1).saveAsTextFile("test.csv")

        //socialNetworkDegrees.foreach(println)
        println(socialNetworkDegrees.map(_._2).stats())

        spark.stop()
    }
}
