package io.mca.twitter.examples

import scala.concurrent.duration._

import akka.pattern.{ ask, pipe }
import akka.actor.{Props, Actor, ActorSystem}
import io.mca.twitter.rest.{Tweet, RESTClient}
import akka.util.Timeout
import io.mca.twitter.rest.statuses.{Show, HomeTimeline}
import akka.actor.Status.Failure

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

sealed trait WorkerMessage
case object Start extends WorkerMessage
case class Timeline(tweets: Seq[Tweet]) extends WorkerMessage

class Worker(consumerKey: String, consumerSecret: String, token: String, tokenSecret: String)
  extends Actor {
  import context.dispatcher
  implicit val timeout = Timeout(5.seconds)

  val client = context.actorOf(RESTClient(consumerKey, consumerSecret), name = "restclient")

  def receive = {
    case w: WorkerMessage =>
      w match {
        case Start =>
          client.ask(HomeTimeline(token, tokenSecret)).map {
            case Right(tweets: Seq[Tweet]) =>
              Timeline(tweets)
            case Left(fail: Failure) =>
              fail
          }.pipeTo(self)

        case Timeline(tweets) =>
          tweets.collectFirst { case t: Tweet =>
            client.ask(Show(token, tokenSecret, t.id)).map {
              case Right(tweet: Tweet) =>
                tweet
              case Left(fail: Failure) =>
                fail
            }.pipeTo(self)
          }
      }

    case t: Tweet =>
      println("Latest tweet:")
      println(t)

    case Failure(cause) =>
      println(cause.getMessage)
      println(cause.getStackTraceString)

    case x => println("Received unexpected " + x)
  }
}
