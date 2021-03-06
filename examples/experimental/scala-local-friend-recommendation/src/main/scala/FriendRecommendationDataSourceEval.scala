package io.prediction.examples.friendrecommendation

import io.prediction.controller._
import scala.io.Source
import scala.collection.immutable.HashMap

//dataSource and dataSourceparam only for evaluation purpose
class FriendRecommendationDataSourceEval (val dsp: FriendRecommendationDataSourceParamsEval)
  extends LDataSource[FriendRecommendationDataSourceParamsEval, EmptyDataParams, FriendRecommendationTrainingData, FriendRecommendationQuery, FriendRecommendationActual] {
 /* 
 override
  def readTraining() : FriendRecommendationTrainingData = {
    val (itemIdMap, itemKeyword) = readItem(dsp.itemFilePath)
    val (userIdMap, userKeyword) = readUser(dsp.userKeywordFilePath)
    val adjArray = readRelationship(dsp.userActionFilePath, userKeyword.size, userIdMap)
    val trainingRecord = readTrainingRecord(dsp.trainingRecordFilePath, userIdMap, itemIdMap)
    new FriendRecommendationTrainingData(userIdMap, itemIdMap, userKeyword, itemKeyword, adjArray, trainingRecord)
  }*/

 // override the read function for evaluator
 override
 def read():Seq[(FriendRecommendationTrainingData, EmptyEvaluationInfo , 
                Seq[(FriendRecommendationQuery, FriendRecommendationActual)])] = {
   // reading the rec_log_test.txt in the format user item action timestamp
   val lines = Source.fromFile(dsp.testingFileDataPath).getLines()
   val queryActualSeq = new Seq[(FriendRecommendation,FriendRecommendationActual)]
   lines.foreach{
     line => 
      val data = line.split("\\s")
      val queryUser = data(0)
      val queryItem = data(1)
      val action = data(3) 
      queryActualSeq += (new FriendRecommendationQuery(queryUser,queryItem),new FriendRecommendationActual(queryUser,action))
   }
   
   //for training
   val (itemIdMap,itemKeyword) = readItem(dsp.itemFilePath)
   val (userIdMap,userKeyword) = readUser(dsp.userKeywordFilePath)
   val adjArray = readRelationship(dsp.userActionFilePath, userKeyword.size, userIdMap)
   val trainingRecord = readTrainingRecord(dsp.trainingRecordFilePath, userIdMap, itemIdMap)
   val (queryUserId, queryitemKeyword) = readUser(dsp.userKeywordFilePath)
   val (actualUserId,actualAction) = readItem(dsp.itemFilePath)
   val q = new FriendRecommendationQuery(queryUserId, queryitemKeyword) 
   val a = new FriendRecommendationActual(actualUserId,actualAction)
   val tmp = (
              new FriendRecommendationTrainingData(userIdMap, itemIdMap, userKeyword, itemKeyword, adjArray, trainingRecord),
              EmptyParams(),
              queryActualSeq
            )
   return Seq(tmp)
  }

  def readItem(file: String) : (HashMap[Int, Int], Array[HashMap[Int, Double]]) = {
    val itemSize = Source.fromFile(file).getLines().size
    val lines = Source.fromFile(file).getLines()
    // An array on Map[keywordId -> weight] values with internal item id index
    val itemKeyword = new Array[HashMap[Int, Double]](itemSize)
    // A map from external id to internal id
    var itemIdMap = new HashMap[Int, Int]()
    var internalId = 0
    lines.foreach{
      line =>
      val data = line.split("\\s")
      itemIdMap += (data(0).toInt -> internalId)
      var keywordMap = new HashMap[Int, Double]()
      data(2).split(";").foreach{
        term =>
        keywordMap += (term.toInt -> 1.0)
      }
      itemKeyword(internalId) = keywordMap
      internalId += 1
    }
    (itemIdMap, itemKeyword)
  }

  def readUser(file: String) : (HashMap[Int, Int], Array[HashMap[Int, Double]]) = {
    val userSize = Source.fromFile(file).getLines().size
    val lines = Source.fromFile(file).getLines()
    // An array on Map[keywordId -> weight] values with internal item id index
    val userKeyword = new Array[HashMap[Int, Double]](userSize)
    // A map from external id to internal id
    var userIdMap = new HashMap[Int, Int]()
    var internalId = 0
    lines.foreach{
      line =>
      val data = line.split("\\s")
      userIdMap += (data(0).toInt -> internalId)
      var keywordMap = new HashMap[Int, Double]()
      data(1).split(";").foreach{
        termWeight =>
        val termWeightPair = termWeight.split(":")
        keywordMap += (termWeightPair(0).toInt -> termWeightPair(1).toDouble)
      }
      userKeyword(internalId) = keywordMap
      internalId += 1
    }
    (userIdMap, userKeyword)
  }

  def readRelationship(file: String, userSize: Int, userIdMap: HashMap[Int, Int]) : Array[List[(Int, Int)]] = {
    val adjArray = new Array[List[(Int, Int)]](userSize)
    val lines = Source.fromFile(file).getLines()
    lines.foreach{
      line =>
      val data = line.split("\\s").map(s => s.toInt)
      if (userIdMap.contains(data(0)) && userIdMap.contains(data(1))) {
        val srcInternalId = userIdMap(data(0))
        val destInternalId = userIdMap(data(1))
        if (adjArray(srcInternalId) == null) {
          adjArray(srcInternalId) = (destInternalId, data.slice(2,5).sum)::List()
        } else {
          adjArray(srcInternalId) = (destInternalId, data.slice(2,5).sum)::adjArray(srcInternalId)
        }
      }
    }
    adjArray
  }

  def readTrainingRecord(file: String, userIdMap: HashMap[Int, Int], itemIdMap: HashMap[Int, Int]) : Iterator[(Int, Int, Boolean)] = {
    val lines = Source.fromFile(file).getLines()
    val trainingRecord = lines.map{
      line =>
      val data = line.split("\\s")
      val userId = userIdMap(data(0).toInt)
      val itemId = itemIdMap(data(1).toInt)
      val result = (data(2).toInt == 1)
      (userId, itemId, result)
    }
    trainingRecord
  }
}
