package io.prediction.examples.friendrecommendation

import io.prediction.controller._

object EvalEngineFactory extends IEngineFactory {
  override
  def apply() = {
    new Engine(
      classOf[FriendRecommendationDataSourceEval],
      IdentityPreparator(classOf[FriendRecommendationDataSourceEval]),
      Map("RandomAlgorithm" -> classOf[RandomAlgorithm]),
      FirstServing(classOf[RandomAlgorithm])
    )
  }
}
