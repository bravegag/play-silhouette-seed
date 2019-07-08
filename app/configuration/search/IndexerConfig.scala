package configuration.search

import play.api.libs.json.{ Json, OFormat }

case class IndexerConfig(indexableHtmlFilesBasePath: String)

object IndexerConfig {
  implicit val format: OFormat[IndexerConfig] = Json.format[IndexerConfig]
}
