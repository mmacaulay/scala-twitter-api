package io.mca.twitter.examples

import scala.concurrent.duration._

import akka.pattern.{ ask, pipe }
import akka.actor.{Props, Actor, ActorSystem}
import io.mca.twitter.rest.{TokenResponse, TokenRequest, RESTClient}
import akka.util.Timeout
import io.mca.twitter.rest.statuses.{Show, HomeTimeline}
import akka.actor.Status.Failure
import io.mca.twitter.rest.platform_objects.Tweet
import spray.http.HttpResponse

object RESTClientExampleApp extends App {
  val system = ActorSystem("RESTClientExampleApp")

  val consumerKey = args(0)
  val consumerSecret = args(1)
  val token = args(2)
  val tokenSecret = args(3)

  val worker = system.actorOf(Props(new Worker(consumerKey, consumerSecret, token, tokenSecret)))
  Thread.sleep(2000)
  worker ! Start
}

case object Start

class Worker(consumerKey: String, consumerSecret: String, token: String, tokenSecret: String)
  extends Actor {
  import context.dispatcher
  implicit val timeout = Timeout(10.seconds)

  val client = context.actorOf(RESTClient(consumerKey, consumerSecret), name = "restclient")

  def receive = {
    case Start =>
      client.ask(TokenRequest()).mapTo[TokenResponse.Result].pipeTo(self)

    case tweets: Seq[Tweet] =>
      println(s"Got ${tweets.size} tweets")
      tweets.headOption.map { t =>
        client.ask(Show(token, tokenSecret, t.id_str)).mapTo[Tweet].pipeTo(self)
      }

    case t: Tweet =>
      println(s"Latest tweet: $t")

    case TokenResponse.Success(newToken, newTokenSecret, _) =>
      // new token has not been approved yet...
      client.ask(HomeTimeline(token, tokenSecret)).mapTo[Seq[Tweet]].pipeTo(self)

    case TokenResponse.Fail(response) =>
      println(s"Token request failed: $response")
    case response: HttpResponse =>
      println(s"Received HTTP Response $response")

    case Failure(cause) =>
      println(cause.getMessage)
      println(cause.getStackTraceString)

    case x => println("Received unexpected " + x)
  }
}
