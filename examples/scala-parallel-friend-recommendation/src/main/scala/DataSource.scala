package io.prediction.examples.pfriendrecommendation

import io.prediction.controller.PDataSource
import io.prediction.controller.EmptyEvaluationInfo
import io.prediction.controller.EmptyActualResult
import io.prediction.controller.Params
import io.prediction.data.storage.Event
import io.prediction.data.storage.Storage

import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.graphx._
import org.apache.spark.rdd.RDD

import grizzled.slf4j.Logger

/*
  Data Source Params : path to data file
 */
case class DataSourceParams(
  val graphEdgelistPath: String
) extends Params

case class TrainingData(
  val g:Graph[Int,Int],
  val identityMatrix:RDD[(VertexId,Double)]
) extends Serializable

class DataSource(val dsp: DataSourceParams )
  extends PDataSource[TrainingData, EmptyEvaluationInfo, Query, Double] {

  override
  def readTraining(sc:SparkContext) : TrainingData = {
    val g = GraphLoader.edgeListFile(sc, dsp.graphEdgelistPath)
    // In the interest of space (since we calculate at most n*n simrank scores),
    // each of the n vertices should have vertexID in the range 0 to n-1
    // val g2 = DeltaSimRankRDD.normalizeGraph(g)
    val identity = DeltaSimRankRDD.identityMatrix(sc, g.vertices.count())
    new TrainingData(g, identity)
  }
}
