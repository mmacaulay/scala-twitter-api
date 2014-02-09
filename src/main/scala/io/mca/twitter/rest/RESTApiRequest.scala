package io.mca.twitter.rest

import spray.http.{Uri, HttpMethods, HttpRequest}

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
