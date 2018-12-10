import org.apache.spark.sql.SparkSession

class File(spark: SparkSession, filePath: String) {
    def load() {
        val csv = spark.read.csv(filePath)

        csv.show(5)
    }
}
