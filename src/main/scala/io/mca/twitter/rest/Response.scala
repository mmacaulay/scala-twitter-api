package io.mca.twitter.rest

import spray.json.DefaultJsonProtocol

case class Tweet(id: Long, text: String, source: String, created_at: String, user: User)
case class User(id: Long, screen_name: String, name: String)
object TweetJsonProtocol extends DefaultJsonProtocol {
  implicit val userFormat = jsonFormat3(User)
  implicit val tweetFormat = jsonFormat5(Tweet)
}