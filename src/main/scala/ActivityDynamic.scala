import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.functions.{max, min}
import org.apache.spark.sql.{DataFrame, Row, SparkSession}
import org.graphstream.graph.implementations.SingleGraph
import org.graphstream.stream.file.FileSinkDGS
import org.graphstream.stream.file.FileSinkImages.OutputPolicy

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

        val activityGraph = new SingleGraph("Activity")
        activityGraph.setStrict(false)
        activityGraph.setAutoCreate(true)

        val fs = new FileSinkDGS()
        activityGraph.addSink(fs)

        val outputPolicy = OutputPolicy.BY_STEP
        //val fsi = new FileSinkImages("prefix", OutputType.PNG, Resolutions.HD720, outputPolicy)

        //fsi.setLayoutPolicy(LayoutPolicy.COMPUTED_FULLY_AT_NEW_IMAGE)

        //activityGraph.addSink(fsi)

        var Row(start: Int, end: Int) = activityDataFrame
            .agg(min("Timestamp"), max("Timestamp"))
            .head

        var t = start
        var l = 0
        val step = 100000

        fs.begin(rootPath + "/test.dgs")
        //fsi.begin(rootPath + "/prefix")

        for (t <- start to end by step) {
            val rows = activityDataFrame
                .filter("Timestamp > " + (t - step) + " AND Timestamp <= " + t)

            rows
                .collect()
                .foreach(row => {
                    activityGraph.addEdge(l.toString, row.getInt(0).toString, row.getInt(1).toString, true)
                    l += 1
                })

            println(t)
            activityGraph.stepBegins(t)
        }

        activityGraph.stepBegins(end)

        fs.end()
        //fsi.end()

        spark.stop()
    }
}
