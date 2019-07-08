package configuration

import play.api.libs.json.{ Json, OFormat }

case class FileIoExecutionContextConfig(nThreads: Int)

object FileIoExecutionContextConfig {
  implicit val format: OFormat[FileIoExecutionContextConfig] =
    Json.format[FileIoExecutionContextConfig]
}
