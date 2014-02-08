package io.mca.twitter.rest

import java.util.{ UUID, Date }

import scala.Any
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

trait RESTApiRequest {
  val token: String
  val tokenSecret: String
  val httpMethod: String
  val resource: String
  val params: Seq[(String, String)]

  def httpRequest: HttpRequest = {
    val method = httpMethod match {
      case "GET" => HttpMethods.GET
      case "POST" => HttpMethods.POST
      case _ => throw new Exception("Twitter API only supports GET and POST")
    }
    val uri = Uri(resource).withQuery(params: _*)
    HttpRequest(method, uri)
  }

  def parameterize(p: Seq[(String, Option[Any])]): Seq[(String, String)] = {
    p.flatMap { case(x, y) =>
      y.map(yp => (x, yp.toString))
    }
  }
}

case class Tweet(id: Long, text: String, source: String, created_at: String, user: User)
case class User(id: Long, screen_name: String, name: String)
object TweetJsonProtocol extends DefaultJsonProtocol {
  implicit val userFormat = jsonFormat3(User)
  implicit val tweetFormat = jsonFormat5(Tweet)
}

object RESTClient {
  def apply(consumerKey: String, consumerSecret: String): Props = {
    Props(classOf[RESTClient], consumerKey, consumerSecret)
  }
}

class RESTClient(val consumerKey: String, val consumerSecret: String) extends Actor with OAuthClient {
  import TweetJsonProtocol._
  import context.dispatcher
  implicit val timeout = Timeout(5.seconds)

  def pipeline(authHeader: String): HttpRequest => Future[Seq[Tweet]] = {
    (addHeader("Authorization", authHeader)
      ~> encode(Gzip)
      ~> sendReceive
      ~> decode(Deflate)
      ~> unmarshal[Seq[Tweet]]
      )
  }

  def receive = {
    case request: RESTApiRequest =>
      val authHeader = oAuthHeader(request.httpMethod, request.resource, request.params, request.token, request.tokenSecret)
      pipeline(authHeader)(request.httpRequest) pipeTo sender

    case x => println("Unknown message: " + x)
  }
}

