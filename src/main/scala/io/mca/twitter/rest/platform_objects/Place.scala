package io.mca.twitter.rest.platform_objects

case class PlaceCoordinates(
                            coordinates: Seq[Seq[Seq[Float]]],
                            `type`: String)

case class Place(
                  attributes: Map[String, String],
                  bounding_box: PlaceCoordinates,
                  country: String,
                  country_code: String,
                  full_name: String,
                  id: String,
                  name: String,
                  place_type: String,
                  url: String)