package configuration

import com.typesafe.config.{ Config, ConfigRenderOptions }
import configuration.lucene.LuceneConfig
import configuration.search.SearchConfig
import play.api.libs.json.{ JsError, JsSuccess, Json, OFormat }
import play.api.{ ConfigLoader, Logger }

/**
 * Type safe application config
 * Here goes everything under `application` key of application[.env].conf file
 */
case class ApplicationConfig(
  search: SearchConfig,
  pagination: PaginationConfig,
  lucene: LuceneConfig,
  fileIoExecutionContext: FileIoExecutionContextConfig)

object ApplicationConfig {
  private val logger = Logger(this.getClass)

  implicit val format: OFormat[ApplicationConfig] =
    Json.format[ApplicationConfig]

  implicit val configLoader: ConfigLoader[ApplicationConfig] =
    (rootConfig: Config, path: String) => {
      val configJsonString =
        rootConfig
          .resolve()
          .root()
          .get(path)
          .render(ConfigRenderOptions.concise())
          // Make numeric string numbers, otherwise ApplicationConfig int properties wan't be parsed
          .replaceAll(":\"(\\d+)\"", ":$1")

      val configJson = Json.parse(configJsonString)

      configJson.validate[ApplicationConfig] match {
        case s: JsSuccess[ApplicationConfig] => s.get
        case e: JsError =>
          val message = s"Cannot load config. Error: ${e.errors}"
          logger.error(message)
          throw new ConfigurationException(message)
      }
    }
}
