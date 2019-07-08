package configuration.lucene

import play.api.libs.json.{ Json, OFormat }

case class LuceneConfig(
  indexDirectoryPath: String,
  executionContext: ExecutionContextConfig)

object LuceneConfig {
  implicit val format: OFormat[LuceneConfig] = Json.format[LuceneConfig]
}
