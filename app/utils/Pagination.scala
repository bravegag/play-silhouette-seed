package utils

import org.apache.http.client.utils.URIBuilder
import utils.boolean._
import scala.util.Try

/**
 * @param totalPages - Total pages
 * @param currentPage - Current page
 * @param hitsPerPage - Hits per page
 * @param pagesInRange - number of links (which lead to pages) to show in the range
 * @param url - url to add page to
 */
class Pagination private (
  val totalPages: Int,
  val currentPage: Int,
  val hitsPerPage: Int,
  val pagesInRange: Int,
  val url: String) {

  /**
   * Return first page number (1) if enabled ( if current page is > 1)
   * @return
   */
  def firstPage: Option[Int] = (currentPage > 1).toOption(1)

  /**
   * Return previous page number if enabled
   * @return
   */
  def prevPage: Option[Int] = (currentPage > 1).toOption(currentPage - 1)

  /**
   * Return next page number if enabled
   * @return
   */
  def nextPage: Option[Int] =
    (currentPage < totalPages).toOption(currentPage + 1)

  /**
   * Return lage page number if enabled
   * @return
   */
  def lastPage: Option[Int] = (currentPage < totalPages).toOption(totalPages)

  /**
   * Current range of pages
   * @return
   */
  def currentRange: Int = ((currentPage - 1) / pagesInRange) + 1

  /**
   * Total number of page ranges
   * @return
   */
  def totalRanges: Int = Math.ceil(totalPages * 1.0 / pagesInRange).toInt

  /**
   * Starting page of given range
   * @param range
   * @return
   */
  def startPageOfRange(range: Int): Int = (range - 1) * pagesInRange + 1

  /**
   * Starting page of previous range
   * @return
   */
  def startPageOfPrevRange: Option[Int] =
    (currentRange > 1).toOption(startPageOfRange(currentRange - 1))

  /**
   * Starting page of next range
   * @return
   */
  def startPageOfNextRange: Option[Int] =
    (currentRange < totalRanges).toOption(startPageOfRange(currentRange + 1))

  /**
   * Start page in the current range
   * @return
   */
  def startPageInCurrentRange: Int = startPageOfRange(currentRange)

  /**
   * End page in the current range
   * @return
   */
  def endPageInCurrentRange: Int = {
    val endPage = startPageInCurrentRange + pagesInRange - 1
    if (endPage > totalPages) totalPages else endPage
  }

  /**
   * Adds page param to the url
   * @param page
   * @return
   */
  def urlWithPage(page: Int): String = {
    new URIBuilder(url).setParameter("page", page.toString).build.toString
  }

  def copy(
    totalPages: Int = totalPages,
    currentPage: Int = currentPage,
    hitsPerPage: Int = hitsPerPage,
    pagesInRange: Int = pagesInRange,
    url: String): Try[Option[Pagination]] = {
    Pagination(totalPages, currentPage, hitsPerPage, pagesInRange, url)
  }
}

object Pagination {

  /**
   * Create pagination
   *
   * If no pages or only single page exists return None, e.g., there is no reason for pagination.
   * @param totalPages - Total pages
   * @param currentPage - Current page
   * @param hitsPerPage - Hits per page
   * @param pagesInRange - number of links (which lead to pages) to show in the range
   * @param url - url to add page to
   */
  def apply(
    totalPages: Int,
    currentPage: Int,
    hitsPerPage: Int,
    pagesInRange: Int,
    url: String): Try[Option[Pagination]] = Try {
    if (totalPages < 0) {
      throw new IllegalArgumentException(
        "totalPages must be greater or equal 0")
    }
    if (currentPage < 0) {
      throw new IllegalArgumentException(
        "currentPage must be greater or equal 0")
    }
    if (hitsPerPage < 1) {
      throw new IllegalArgumentException(
        "hitsPerPage must be greater or equal 1")
    }
    if (pagesInRange < 1) {
      throw new IllegalArgumentException(
        "pagesInRange must be greater or equal 1")
    }

    if (totalPages == 0 && currentPage != 0 || totalPages != 0 && currentPage == 0) {
      throw new IllegalArgumentException(
        "if either totalPages or currentPage is 0, both of them must equal 0")
    }
    if (currentPage > totalPages) {
      throw new IllegalArgumentException(
        "currentPage cannot be greater than totalPages")
    }
    if (url.trim.isEmpty) {
      throw new IllegalArgumentException("url must not be empty")
    }

    if (totalPages < 2) {
      None
    } else {
      Some(
        new Pagination(totalPages, currentPage, hitsPerPage, pagesInRange, url))
    }
  }
}
