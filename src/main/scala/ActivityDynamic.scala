import org.apache.log4j.{Level, Logger}
import org.apache.spark.graphx.Edge
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.graphstream.graph.implementations.SingleGraph
import org.graphstream.stream.file.{FileSinkDGS, FileSinkImages}
import org.graphstream.stream.file.FileSinkImages.{LayoutPolicy, OutputPolicy, OutputType, Resolutions}


object ActivityDynamic extends HiggsTwitter {

    def main(args: Array[String]) {
        val logger = Logger.getLogger(getClass.getName)
        logger.setLevel(Level.INFO)

        val spark = SparkSession
            .builder
            .master("local[*]")
            .appName(appName)
            .getOrCreate()

        val activityDataFrame: DataFrame = spark.read
            .option("sep", " ")
            .option("header", true)
            .option("inferSchema", true)
            .csv(args(0))

        val edges: RDD[Edge[Int]] = activityDataFrame
            .rdd
            .map(row => Edge(row.getInt(0), row.getInt(1)))

        val fs = new FileSinkDGS()
        val graph = new SingleGraph("Social network")
        val outputPolicy = OutputPolicy.BY_STEP
        val fsi = new FileSinkImages("prefix", OutputType.PNG, Resolutions.HD720, outputPolicy)

        fsi.setLayoutPolicy(LayoutPolicy.COMPUTED_FULLY_AT_NEW_IMAGE)

        graph.setStrict(false)
        graph.setAutoCreate(true)

        graph.addSink(fsi)
        graph.addSink(fs)

        fsi.begin(rootPath + "/prefix")
        fs.begin(rootPath + "/test.dgs")
        graph.stepBegins(0)
        graph.addEdge("AB", "A", "B")

        graph.stepBegins(1)
        graph.stepBegins(2)
        graph.stepBegins(3)
        graph.addEdge("BC", "B", "C")
        graph.stepBegins(4)
        graph.addEdge("CA", "C", "A")
        graph.stepBegins(5)

        fs.end()
        fsi.end()

        spark.stop()
    }
}
