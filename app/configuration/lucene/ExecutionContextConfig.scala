package configuration.lucene

import play.api.libs.json.{ Json, OFormat }

case class ExecutionContextConfig(nThreads: Int)

object ExecutionContextConfig {
  implicit val format: OFormat[ExecutionContextConfig] =
    Json.format[ExecutionContextConfig]
}
