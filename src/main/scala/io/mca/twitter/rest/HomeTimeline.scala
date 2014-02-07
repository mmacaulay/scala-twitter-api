package io.mca.twitter.rest

// https://dev.twitter.com/docs/api/1.1/get/statuses/home_timeline
case class HomeTimeline(token: String, tokenSecret: String, params: Seq[(String, String)] = Seq())
                        extends RESTApiRequest {

  val httpMethod = "GET"
  val resource = "https://api.twitter.com/1.1/statuses/home_timeline.json"
}
