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

import io.mca.oauth._

case class Credentials(consumerKey: String, consumerSecret: String,
                       token: String, tokenSecret: String)
case class Tweet(id: Long, text: String, source: String, created_at: String, user: User)
case class User(id: Long, screen_name: String, name: String)
object TweetJsonProtocol extends DefaultJsonProtocol {
  implicit val userFormat = jsonFormat3(User)
  implicit val tweetFormat = jsonFormat5(Tweet)
}

class RESTClient(credentials: Credentials) extends Actor with OAuthClient {
  import context.dispatcher
  import TweetJsonProtocol._
  implicit val timeout = Timeout(5.seconds)

  val consumerKey = credentials.consumerKey
  val consumerSecret = credentials.consumerSecret
  val token = credentials.token
  val tokenSecret = credentials.tokenSecret

  def pipeline(authHeader: String): HttpRequest => Future[Seq[Tweet]] = {
    (addHeader("Authorization", authHeader)
      ~> encode(Gzip)
      ~> sendReceive
      ~> decode(Deflate)
      ~> unmarshal[Seq[Tweet]]
      )
  }

  def receive = {
    case request: HttpRequest =>
      val method = request.method.name
      val baseUri = request.uri.scheme + ":" + request.uri.authority + request.uri.path
      val authHeader = oAuthHeader(method, baseUri, Seq(), token, tokenSecret)

      pipeline(authHeader)(request) pipeTo sender

    case x => println("Unknown message: " + x)
  }
}

