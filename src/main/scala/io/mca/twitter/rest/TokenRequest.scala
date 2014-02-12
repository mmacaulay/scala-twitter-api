package io.mca.twitter.rest

import spray.http.{HttpResponse, Uri, HttpMethods, HttpRequest}

case class TokenRequest(callback: Option[String] = None) {
  val httpMethod: String = "POST"
  val baseUri: String = "https://api.twitter.com/oauth/request_token"

  def httpRequest: HttpRequest = {
    val method = httpMethod match {
      case "GET" => HttpMethods.GET
      case "POST" => HttpMethods.POST
      case _ => throw new Exception("Twitter API only supports GET and POST")
    }
    val uri = Uri(baseUri)
    HttpRequest(method, uri)
  }
}

object TokenResponse {
  type Result = Either[Success, Fail]

  case class Success(token: String, tokenSecret: String,
                                   fullResponse: HttpResponse)

  case class Fail(fullResponse: HttpResponse)
}