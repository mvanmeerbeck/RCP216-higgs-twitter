import lib.MaxFlow
import org.apache.log4j.{Level, Logger}
import org.apache.spark.graphx.{Graph, GraphLoader, VertexId, VertexRDD}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

object SocialNetworkConnectivity extends HiggsTwitter {
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

        val socialNetwork: Graph[VertexId, Int] = GraphLoader
            .edgeListFile(
                spark.sparkContext,
                args(0)/*"G:\\projets\\HiggsTwitter\\kcore.csv"*/
            )
            .mapVertices((vid, attr) => attr.toLong)
            .cache()

        val r = scala.util.Random
        val sourceId: VertexId = r.nextInt(socialNetwork.numVertices.toInt) // The source
        val targetId: VertexId = r.nextInt(socialNetwork.numVertices.toInt) // The target
        println(sourceId)
        println(targetId)
        val flows: RDD[((VertexId, VertexId), Int)] = MaxFlow.run(spark.sparkContext, sourceId, targetId, socialNetwork, 5)

        val emanating = flows.filter(e => e._1._1 == sourceId).map(e => (e._1._1,e._2)).reduceByKey(_ + _).collect
        println("Max Flow: ")
        println(emanating(0)._2)

        spark.stop()
    }
}
