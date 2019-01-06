import java.io.File

import lib.Export
import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.{DataFrame, Dataset, Row, SparkSession}
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types.IntegerType

import scala.reflect.io.Directory

object ActivityActionsRate extends HiggsTwitter {

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

        val interval = 60 * 60

        val actions: Dataset[Row] = activityDataFrame
            .withColumn("Range", col("Timestamp") - (col("Timestamp") % interval))
            .groupBy("Range")
            .count()
            .sort("Range")

        Export.dataset(
            actions,
            new Directory(new File(rootPath + "/Activity/ActionsRate"))
        )

        spark.stop()
    }
}
