package configuration

import play.api.libs.json.{ Json, OFormat }

case class PaginationConfig(pagesInRange: Int)

object PaginationConfig {
  implicit val format: OFormat[PaginationConfig] = Json.format[PaginationConfig]
}
