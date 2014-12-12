package io.prediction.examples.friendrecommendation

import io.prediction.controller._

class FriendRecommendationDataSourceParamsEval(
  val itemFilePath: String,
  val userKeywordFilePath: String,
  val userActionFilePath: String,
  val trainingRecordFilePath: String,
  val testingDataFilePath: String
) extends Params
