package configuration.search

import play.api.libs.json.{ Json, OFormat }

case class SearchConfig(
  indexer: IndexerConfig,
  supportedLanguages: Seq[String],
  hitsPerPage: Int)

object SearchConfig {
  implicit val format: OFormat[SearchConfig] = Json.format[SearchConfig]
}
