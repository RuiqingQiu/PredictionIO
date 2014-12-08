package io.prediction.examples.pfriendrecommendation

import io.prediction.controller.Engine
import io.prediction.controller.IEngineFactory
import io.prediction.controller.IPersistentModel
import io.prediction.controller.IPersistentModelLoader
import io.prediction.controller.PDataSource
import io.prediction.controller.Params
import io.prediction.controller.P2LAlgorithm
import io.prediction.controller.PAlgorithm
import io.prediction.controller.IdentityPreparator
import io.prediction.controller.FirstServing
import io.prediction.controller.Utils
import io.prediction.controller.Workflow
import io.prediction.controller.WorkflowParams

import org.apache.commons.io.FileUtils
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
// For stubbing batchpredict
import org.apache.spark.rdd.EmptyRDD
import org.apache.spark.rdd.RDD
import org.apache.spark.graphx._
import org.json4s._

import scala.io.Source

import java.io.File
/*
  Data Source produces loads graph from file specified in dsp and generates
  GraphX object

  About the tuple query class: this is how you format the query : 
  `curl -H "Content-Type: application/json" -d '[1,4]'
  http://localhost:8000/queries.json`
  */
object Run {
  def main(args: Array[String]) {
    Workflow.run(
      dataSourceClassOpt = Some(classOf[DataSource]),
      dataSourceParams = dsp,
      preparatorClassOpt = Some(IdentityPreparator(classOf[DataSource])),
      algorithmClassMapOpt = Some(Map("" -> classOf[SimRankAlgorithm])),
      algorithmParamsList = Seq(("", ap)),
      servingClassOpt = Some(FirstServing(classOf[SimRankAlgorithm])),
      params = WorkflowParams(
	      batch = "Imagine: P Recommendations",
        verbose = 1
      )
    )
  }
}

object PSimRankEngineFactory extends IEngineFactory {
  def apply() = {
    new Engine(
      classOf[DataSource],
      IdentityPreparator(classOf[DataSource]),
      Map("" -> classOf[SimRankAlgorithm]),
      FirstServing(classOf[SimRankAlgorithm]))
  }
}


class Tuple2IntSerializer extends CustomSerializer[(Int, Int)](format => (
  {
    case JArray(List(JInt(x), JInt(y))) => (x.intValue, y.intValue)
  },
  {
    case x: (Int, Int) => JArray(List(JInt(x._1), JInt(x._2)))
  }
))
