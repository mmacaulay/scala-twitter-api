package io.mca.twitter.rest

import scala.concurrent.duration._
import scala.concurrent._

import akka.io.IO
import akka.util.Timeout
import akka.actor._
import akka.pattern.{ ask, pipe }

import spray.http._
import spray.httpx.SprayJsonSupport._
import spray.httpx.encoding.{ Gzip, Deflate }
import spray.httpx.unmarshalling._

import io.mca.oauth._
import spray.can.Http
import io.mca.twitter.rest.statuses.{HomeTimeline, Show}
import spray.http.HttpHeaders.{`Content-Encoding`, `Accept-Encoding`, RawHeader}
import spray.http.HttpHeaders.RawHeader
import akka.actor.Status.Failure
import spray.http.HttpResponse
import io.mca.twitter.rest.statuses.HomeTimeline
import spray.httpx.unmarshalling.UnsupportedContentType
import io.mca.twitter.rest.statuses.Show


object RESTClient {
  def apply(consumerKey: String, consumerSecret: String): Props = {
    Props(classOf[RESTClient], consumerKey, consumerSecret)
  }
}

class RESTClient(val consumerKey: String, val consumerSecret: String) extends Actor with OAuthClient {
  import TweetJsonProtocol._
  import context.dispatcher

  implicit val timeout = Timeout(5.seconds)

  def receive = {
    case request: RESTApiRequest =>
      makeRequest(request).map(response => unmarshal(request, response)).pipeTo(sender)

    case x => println("Unknown message: " + x)
  }

  def makeRequest(apiRequest: RESTApiRequest): Future[HttpResponse] = {
    val authHeader = RawHeader("Authorization", oAuthHeader(apiRequest.httpMethod, apiRequest.resource,
      apiRequest.params, apiRequest.token, apiRequest.tokenSecret))

    val acceptHeader = `Accept-Encoding`(HttpEncodings.gzip, HttpEncodings.deflate)
    val request = apiRequest.httpRequest.withHeaders(List(authHeader, acceptHeader))

    (IO(Http)(context.system) ? request).mapTo[HttpResponse].map(decode)
  }

  def decode(response: HttpResponse): HttpResponse = {
    response.headers.find(_.lowercaseName == `Content-Encoding`.lowercaseName).map(_.value) match {
      case Some(HttpEncodings.gzip.value) =>
        Gzip.decode(response)
      case Some(HttpEncodings.deflate.value) =>
        Deflate.decode(response)
      case x =>
        println("Didn't match content encoding: " + x)
        response
    }
  }

  def unmarshal(request: RESTApiRequest, response: HttpResponse) = {
    val result = request match {
      case homeTimeline: HomeTimeline =>
        response.as[Seq[Tweet]]
      case show: Show =>
        response.as[Tweet]
    }

    result match {
      case Right(success) =>
        success
      case Left(error) =>
        error match {
          case ContentExpected =>
            Failure(new Exception("Content expected"))
          case UnsupportedContentType(message) =>
            Failure(new Exception(message))
          case MalformedContent(message, cause) =>
            Failure(new Exception(message, cause.getOrElse(null)))
        }
    }
  }
}

