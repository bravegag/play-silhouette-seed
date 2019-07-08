package search

import search.PagesLoader.LocalizedPath

import scala.concurrent.Future

trait PagesLoader {

  /**
   * Get all paths of existing html pages to index
   *
   * @return
   */
  def getHtmlFilesPaths: Future[Seq[LocalizedPath]]

  /**
   * Load given html page by path and parse it
   *
   * @param localizedPath
   * @return
   */
  def loadPage(localizedPath: LocalizedPath): Future[Page]
}

object PagesLoader {

  /**
   * @param language
   * @param path - relative to [[configuration.ApplicationConfig.search.indexer.indexableHtmlFilesBasePath]] path
   */
  case class LocalizedPath(language: String, path: String)
}
