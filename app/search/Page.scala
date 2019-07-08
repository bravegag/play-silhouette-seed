package search

/**
 * @param path -  relative to [[configuration.ApplicationConfig.search.indexer.indexableHtmlFilesBasePath]] path to
 *                original html file
 * @param title - html page's title
 * @param body - cleaned up html page's body (all tags removed)
 * @param lang - page language
 */
case class Page(path: String, title: String, body: String, lang: String)
