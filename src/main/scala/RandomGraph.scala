import lib.MaxFlow
import org.apache.log4j.{Level, Logger}
import org.apache.spark.graphx.VertexId
import org.apache.spark.graphx.util.GraphGenerators
import org.apache.spark.sql.SparkSession

object RandomGraph extends HiggsTwitter {
    def main(args: Array[String]): Unit = {
        val logger = Logger.getLogger(getClass.getName)
        logger.setLevel(Level.INFO)

        val spark = SparkSession
            .builder
            .master("local[*]")
            .appName(appName)
            .getOrCreate()

        for (i <- 1 to 30) {
            var n: Int = 10 * i // Number of vertices

            // Create random graph with random source and target
            var graph = GraphGenerators.logNormalGraph(spark.sparkContext, n, 5)
            val r = scala.util.Random
            val sourceId: VertexId = r.nextInt(n) // The source
            val targetId: VertexId = r.nextInt(n) // The target

            // Calculate Max Flow
            val t0 = System.nanoTime()
            val flows = MaxFlow.run(spark.sparkContext, sourceId, targetId, graph)
            val t1 = System.nanoTime()

            // Print information
            val emanating = flows.filter(e => e._1._1 == sourceId).map(e => (e._1._1, e._2)).reduceByKey(_ + _).collect
            println("Number of Edges: ")
            println(flows.count)
            println("Max Flow: ")
            println(emanating(0)._2)
        }

        spark.stop()
    }
}
