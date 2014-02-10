package io.mca.twitter.rest.platform_objects

import spray.json.{JsonFormat, DefaultJsonProtocol}

object TwitterJsonProtocol extends DefaultJsonProtocol {
  implicit val coordinatesFormat = jsonFormat2(Coordinates)
  implicit val contributorFormat = jsonFormat3(Contributor)
  implicit val currentUserRetweetFormat = jsonFormat2(CurrentUserRetweet)
  implicit val hashTagFormat = jsonFormat2(Hashtag)
  implicit val sizeFormat = jsonFormat3(Size)
  implicit val sizesFormat = jsonFormat4(Sizes)
  implicit val mediaFormat = jsonFormat12(Media)
  implicit val urlFormat = jsonFormat4(Url)
  implicit val userMentionFormat = jsonFormat5(UserMention)
  implicit val entitiesFormat = jsonFormat4(Entities)
  implicit val placeCoordinatesFormat = jsonFormat2(PlaceCoordinates)
  implicit val placeFormat = jsonFormat9(Place)
  implicit val tweetFormat: JsonFormat[Tweet] = lazyFormat(jsonFormat22(Tweet))
  implicit val rootTweetFormat = rootFormat(tweetFormat)
  implicit val userFormat = jsonFormat22(User)
}

case class Coordinates(coordinates: Seq[Double], `type`: String)
case class Contributor(id: Long, id_str: String, screen_name: String)
case class CurrentUserRetweet(id: Long, id_str: String)

// This representation of a tweet object is missing a few fields due to
// Scala's constraint on case classes having more than 22 parameters.
case class Tweet(
                  coordinates: Option[Coordinates],
                  created_at: String,
                  current_user_retweet: Option[CurrentUserRetweet],
                  entities: Entities,
                  favorite_count: Option[Long],
                  favorited: Option[Boolean],
                  filter_level: Option[String], // https://dev.twitter.com/blog/introducing-new-metadata-for-tweets
                  id_str: String, // https://groups.google.com/forum/#!topic/twitter-development-talk/ahbvo3VTIYI
                  in_reply_to_screen_name: Option[String],
                  in_reply_to_status_id_str: Option[String],
                  in_reply_to_user_id_str: Option[String],
                  lang: Option[String],
                  place: Option[Place],
                  possibly_sensitive: Option[Boolean],
                  retweet_count: Long,
                  retweeted: Option[Boolean],
                  retweeted_status: Option[Tweet],
                  source: String,
                  text: String,
                  user: User,
                  withheld_copyright: Option[Boolean],
                  withheld_in_countries: Option[Seq[String]])

// Awaiting scala 2.11 for case classes with more than 22 parameters.
/*case*/ class TweetFull(
                  contributors: Option[Seq[Contributor]],
                  coordinates: Coordinates,
                  created_at: String,
                  current_user_retweet: Option[CurrentUserRetweet],
                  entities: Entities,
                  favorite_count: Option[Long],
                  favorited: Option[Boolean],
                  filter_level: String, // https://dev.twitter.com/blog/introducing-new-metadata-for-tweets
                  id: Long,
                  id_str: String, // https://groups.google.com/forum/#!topic/twitter-development-talk/ahbvo3VTIYI
                  in_reply_to_screen_name: Option[String],
                  in_reply_to_status_id: Option[Long],
                  in_reply_to_status_id_str: Option[String],
                  in_reply_to_user_id: Option[Long],
                  in_reply_to_user_id_str: Option[String],
                  lang: Option[String],
                  place: Place,
                  possibly_sensitive: Option[Boolean],
                  scopes: Map[String, Any],
                  retweet_count: Long,
                  retweeted: Option[Boolean],
                  retweeted_status: Option[Tweet],
                  source: String,
                  text: String,
                  truncated: Boolean,
                  user: User,
                  withheld_copyright: Option[Boolean],
                  withheld_in_countries: Option[Seq[String]],
                  withheld_scope: Option[String])