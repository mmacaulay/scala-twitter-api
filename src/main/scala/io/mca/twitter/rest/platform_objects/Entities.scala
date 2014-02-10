package io.mca.twitter.rest.platform_objects

case class Hashtag(
                  indices: Seq[Int],
                  text: String)
case class Media(
                  display_url: String,
                  expanded_url: String,
                  id: Long,
                  id_str: String,
                  indices: Seq[Int],
                  media_url: String,
                  media_url_https: String,
                  sizes: Sizes,
                  source_status_id: Option[Long],
                  source_status_id_str: Option[String],
                  `type`: String,
                  url: String)
case class Size(
                 h: Int,
                 resize: String,
                 w: Int)
case class Sizes(
                  thumb: Size,
                  large: Size,
                  medium: Size,
                  small: Size)
case class Url(
                display_url: String,
                expanded_url: String,
                indices: Seq[Int],
                url: String)

case class UserMention(
                id: Long,
                id_str: String,
                indices: Seq[Int],
                name: String,
                screen_name: String)

case class Entities(hashtags: Option[Seq[Hashtag]],
                  media: Option[Seq[Media]],
                  urls: Option[Seq[Url]],
                  user_mentions: Option[Seq[UserMention]])