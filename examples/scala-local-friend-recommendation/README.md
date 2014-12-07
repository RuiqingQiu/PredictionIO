<h2>Our Engine</h2><br/>
We are creating an engine in PredictionIO for friend/item recommendation in social network settings. It learns from user profiles, item information, social interactions as well as past recommendation history and builds a model to provide suggestions on friend/item for users.<br/>

Three algorithms are implemented.

1. Random
2. Keyword Similarity KNN
3. SimRank // Note that this algorithm is recommending a uesr to a user and thus the "itemId" in the query should be userId instead

Notice about Spark settings:

As the data set is large, we recommend setting spark memories to be large. <br/>
Please set the below two lines with the two values $E_M and $D_M in the $SPARK_HOME/conf/spark-defaults.conf <br/>
spark.executor.memory $E_M <br/>
spark.driver.memory $D_M <br/>
We have tested "Random" and "Keyword Similarity KNN" algorithms with $E_M = 16g and $D_M = 16g. <br/>
"SimRank" algorithm takes up a lot of memory and thus please reduce the mini file size if memory is not enough.<br/>

To run the engine, you need to Register (Build) + Train + Deploy : 

```
$PIO_HOME/bin/pio build -v $EngineJson

$PIO_HOME/bin/pio train -v $EngineJson

$PIO_HOME/bin/pio deploy -v $EngineJson
```

$EngineJson is

1. "random_engine.json" for "Random" (default as running with full data files)
2. "keyword_similarity_engine.json" for "Keyword Similarity KNN" (default as running with full data files)
3. "simrank_similarity_engine.json" for "SimRank Similarity" (default as running with mini data files)

To query :

```
curl -H "Content-Type: application/json" -d '{ "user": $UserId , "item" : $ItemId}' http://localhost:8000/queries.json
```

$UserId and $ItemId are the user and item you want to query.

<h2>Data Preprocess</h2><br/>
We have a random script for testing a subset of original data
To use random script:<br/>
```
$ python file_random.py [number of user] [number of item] 
```
To Download the datasets:<br/>
The KDD cup page can be found <a href="https://www.kddcup2012.org/c/kddcup2012-track1">here</a><br/>
The dataset are <a href="https://www.kddcup2012.org/c/kddcup2012-track1/data">here</a><br/>
<ul>
  <li>expected files:</li> 
    <ul>
      <li>data/user_profile.txt</li>
      <li>data/item.txt</li>
      <li>data/user_key_word.txt</li>
      <li>data/user_sns.txt</li>
      <li>data/user_action.txt</li>
    </ul>
  <li>output files:</li>
    <ul>
      <li>data/mini_user_key_word.txt</li>
      <li>data/mini_item.txt</li>
      <li>data/mini_user_key_word.txt</li>
      <li>data/mini_user_sns.txt</li>
      <li>data/mini_user_action.txt</li>
    </ul>
</ul>

##### Evaluator for Friend Recommendation:
Query to our engine 
