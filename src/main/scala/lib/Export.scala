package lib

import java.io.{File, PrintWriter}

import org.apache.spark.graphx.VertexRDD
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{Dataset, Row}

import scala.reflect.ClassTag
import scala.reflect.io.Directory

object Export{
    def list(list: List[(Int, Long)], directory: Directory): Unit = {
        directory.deleteRecursively()

        val writer = new PrintWriter(new File(directory.path))

        list.foreach(data => {
            writer.write(formatCsv(data))
            writer.println()
        })

        writer.close()
    }

    def dataset(dataset: Dataset[Row], directory: Directory): Unit = {
        directory.deleteRecursively()

        dataset
            .repartition(1)
            .write
            .format("com.databricks.spark.csv")
            .save(directory.path)
    }

    def rdd[A: ClassTag, B: ClassTag](rdd: RDD[(A, B)], directory: Directory): Unit = {
        directory.deleteRecursively()

        this.export(rdd
            .repartition(1)
            .map(data => formatCsv(data)), directory)
    }

    def rdd[A: ClassTag, B: ClassTag, C: ClassTag](rdd: RDD[(A, B, C)], directory: Directory): Unit = {
        directory.deleteRecursively()

        this.export(rdd
            .repartition(1)
            .map(data => formatCsv(data)), directory)
    }

    def rdd(rdd: RDD[(Int, Float)], directory: Directory): Unit = {
        directory.deleteRecursively()

        this.export(rdd
            .repartition(1)
            .map(data => formatCsv(data)), directory)
    }

    def vertices[T: ClassTag](vertices: VertexRDD[T], directory: Directory): Unit = {
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
