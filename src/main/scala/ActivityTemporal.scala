import java.io.File

import lib.Export
import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.{DataFrame, Dataset, Row, SparkSession}

import scala.reflect.io.Directory

object ActivityTemporal extends HiggsTwitter {

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

        val actions: Dataset[Row] = activityDataFrame
            .groupBy("Timestamp")
            .count()
            .sort("Timestamp")

        // calculer le rate sur un delta de temps

        Export.dataset(
            actions,
            new Directory(new File(rootPath + "/Activity/Temporal/Actions"))
        )

        spark.stop()
    }
}
