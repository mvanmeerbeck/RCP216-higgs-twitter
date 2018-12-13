import org.apache.spark.sql.{DataFrame, SparkSession}

class File(spark: SparkSession, filePath: String) {
    var data: DataFrame = _

    def load() {
        data = spark.read.csv(filePath)
    }
}
