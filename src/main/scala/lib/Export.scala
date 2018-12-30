package lib

import org.apache.spark.graphx.VertexRDD
import org.apache.spark.rdd.RDD

import scala.reflect.io.Directory

object Export{
    def rdd(rdd: RDD[(Int, Float)], directory: Directory): Unit = {
        directory.deleteRecursively()

        this.export(rdd
            .repartition(1)
            .map(data => formatCsv(data)), directory)
    }

    def vertices(vertices: VertexRDD[Int], directory: Directory): Unit = {
        this.export(vertices
            .repartition(1)
            .map(data => formatCsv(data)), directory)
    }

    def export(data: RDD[_], directory: Directory): Unit = {
        directory.deleteRecursively()

        data
            .saveAsTextFile(directory.path)
    }

    def formatCsv(data: Product): String = {
        data
            .productIterator
            .mkString(",")
    }
}
