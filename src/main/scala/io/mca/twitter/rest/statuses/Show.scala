package io.mca.twitter.rest.statuses

import io.mca.twitter.rest.RESTApiRequest

// https://dev.twitter.com/docs/api/1.1/get/statuses/show/%3Aid
case class Show(token: String, tokenSecret: String, id: Long, trimUser: Option[Boolean] = None,
                 includeMyRetweet: Option[Boolean] = None, includeEntities: Option[Boolean] = None)
  extends RESTApiRequest {

  val httpMethod = "GET"
  val resource = "https://api.twitter.com/1.1/statuses/show.json"

  val params: Seq[(String, String)] = parameterize(Seq(
    ("id", Some(id)),
    ("trim_user", trimUser),
    ("include_my_retweet", includeMyRetweet),
    ("include_entities", includeEntities)))
}