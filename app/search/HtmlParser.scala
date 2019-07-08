package search

import search.HtmlParser.HtmlParseResult

trait HtmlParser {

  /**
   * Parse html content, extracts title and "raw" body (removing all html tags)
   */
  def parse(html: String): HtmlParseResult
}

object HtmlParser {
  case class HtmlParseResult(title: String, body: String)
}
