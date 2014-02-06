package io.mca.twitter

import java.util.{ UUID, Date }

import scala.concurrent.duration._
import scala.concurrent._

import akka.io.IO
import akka.util.Timeout
import akka.actor._
import akka.pattern.{ ask, pipe }

import spray.http._
import spray.json.DefaultJsonProtocol
import spray.httpx.SprayJsonSupport._
import spray.client.pipelining._
import spray.httpx.encoding.{ Gzip, Deflate }

import oauth._

case class Tweet(id: Long, text: String, source: String, created_at: String, user: User)
case class User(id: Long, screen_name: String, name: String)
object TweetJsonProtocol extends DefaultJsonProtocol {
  implicit val userFormat = jsonFormat3(User)
  implicit val tweetFormat = jsonFormat5(Tweet)
}

class TwitterAPI(timelinePath: ActorPath) extends Actor with OAuthClient {
  import context.dispatcher
  import TweetJsonProtocol._
  implicit val timeout = Timeout(5.seconds)

  val consumerKey = ""
  val consumerSecret = ""
  val token = ""
  val tokenSecret = ""
  
  val timelineUri = "https://api.twitter.com/1.1/statuses/home_timeline.json"

  def pipeline(authHeader: String): HttpRequest => Future[Seq[Tweet]] = {
    (addHeader("Authorization", authHeader)
      ~> encode(Gzip)
      ~> sendReceive
      ~> decode(Deflate)
      ~> unmarshal[Seq[Tweet]]
    )
  }

  def timeline = context.actorSelection(timelinePath)

  def receive = {
    case request: HttpRequest =>
      val method = request.method.name
      val baseUri = request.uri.scheme + ":" + request.uri.authority + request.uri.path
      val authHeader = oAuthHeader(method, baseUri, Seq(), token, tokenSecret)

      pipeline(authHeader)(request) pipeToSelection timeline

    case x => println("Unknown message: " + x)
  }
}


