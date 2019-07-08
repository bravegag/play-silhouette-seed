package search

import search.Searcher.{ SearchRequest, SearchResult }

import scala.concurrent.Future

case class Abc(a: Int)

trait Searcher {

  /**
   * Search html pages
   * @param request
   * @return
   */
  def search(request: SearchRequest): Future[SearchResult]
}

object Searcher {

  /**
   * Note: do not confuse `page` here with [[search.Page]], `page` in the given context is the set of hits)
   * @param searchQuery - search query entered by the user
   * @param lang - in which language to search
   * @param page - required page (1 based)
   * @param hitsPerPage - number of hits per page, if not set [[configuration.ApplicationConfig.search.hitsPerPage]]
   * will be used
   */
  case class SearchRequest(
    searchQuery: String,
    lang: String,
    page: Int,
    hitsPerPage: Option[Int] = None)

  /**
   * Note: do not confuse `page` here with [[search.Page]], `page` in the given context is the set of hits)
   * @param hits - set of hits within given page found by provided search query
   * @param hitsPerPage - number of hits per page
   * @param totalHits - total number of hits found by provided search query
   * @param totalPages - number of total pages. Page contains `hitsPerPage` hits
   * @param currentPage - current page, 0 if no results found
   * @param searchQuery - original search query
   */
  case class SearchResult(
    hits: List[Hit],
    hitsPerPage: Int,
    totalHits: Long,
    totalPages: Int,
    currentPage: Int,
    searchQuery: String)

  /**
   * @param page
   * @param titleHighlightedFragments - fragments of title highlighted with matches of search query
   * @param bodyHighlightedFragments - fragments of body highlighted with matches of search query
   */
  case class Hit(
    page: Page,
    titleHighlightedFragments: Seq[String],
    bodyHighlightedFragments: Seq[String])
}
