import org.apache.spark.sql.SparkSession

object HiggsTwitter {
    def main(args: Array[String]) {
        val spark = SparkSession
          .builder
          .master("local[*]")
          .appName("HiggsTwitter")
        .getOrCreate()

        val file = new File(spark, args(0))
        file.load()
    }
}
