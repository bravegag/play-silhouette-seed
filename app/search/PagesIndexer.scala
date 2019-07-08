package search

import scala.concurrent.Future

/**
 * Index html pages
 */
trait PagesIndexer {

  /**
   * Index all html pages located under
   * [[configuration.ApplicationConfig.search.indexer.indexableHtmlFilesBasePath]]/[any-of-supported-language] folders.
   * supported languages are configured in [[configuration.ApplicationConfig.search.supportedLanguages]]
   * @return
   */
  def index(): Future[Seq[Page]]
}
