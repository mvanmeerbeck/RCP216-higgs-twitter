package lib

import org.apache.spark.graphx.{Edge, Graph}
import org.apache.spark.internal.Logging
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, Dataset, Row}
import org.apache.spark.sql.functions.{col, max, min}

class DynamicGraph(var dataframe: DataFrame, var timeColumnName: String, var interval: Int) extends Logging {

    def start: Int = getStart

    def end: Int = getEnd

    def numInterval: Int = getNumInterval

    def getSnapshotGraph(t: Int): Graph[Int, Int] = {
        val actions: Dataset[Row] = dataframe
            .withColumn("Range", col(timeColumnName) - (col(timeColumnName) % interval))
            .filter("Range <= " + t)

        val edges: RDD[Edge[Int]] = actions
            .rdd
            .map(row => Edge(row.getInt(0), row.getInt(1)))

        val snapshot: Graph[Int, Int] = Graph.fromEdges(edges, 0)
            .cache()

        snapshot
    }

    private def getStart: Int = {
        val Row(start: Int) = dataframe
            .agg(min(timeColumnName))
            .head

        start - (start % interval)
    }

    private def getEnd: Int = {
        val Row(end: Int) = dataframe
            .agg(max(timeColumnName))
            .head

        end - (end % interval)
    }

    private def getNumInterval: Int = {
        (end - start) / interval
    }
}
