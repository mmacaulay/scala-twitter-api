package io.mca.twitter.examples

import scala.concurrent.duration._

import akka.pattern.{ ask, pipe }
import akka.actor.{Props, Actor, ActorSystem}
import io.mca.twitter.rest.{HomeTimeline, RESTClient}
import akka.util.Timeout

object HomeTimelineApp extends App {
  val system = ActorSystem("HomeTimelineApp")

  val consumerKey = args(0)
  val consumerSecret = args(1)
  val token = args(2)
  val tokenSecret = args(3)

  val worker = system.actorOf(Props(new Worker(consumerKey, consumerSecret, token, tokenSecret)))
  Thread.sleep(2000)
  worker ! Work
}

case object Work

class Worker(consumerKey: String, consumerSecret: String, token: String, tokenSecret: String)
  extends Actor {
  import context.dispatcher
  implicit val timeout = Timeout(5.seconds)

  val client = context.actorOf(RESTClient(consumerKey, consumerSecret), name = "restclient")

  def receive = {
    case Work =>
      client ? HomeTimeline(token, tokenSecret) pipeTo self

    case x => println("Received " + x)
  }
}
