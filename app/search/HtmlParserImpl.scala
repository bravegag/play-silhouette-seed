package search

import javax.inject.Singleton
import org.jsoup.Jsoup
import search.HtmlParser.HtmlParseResult

@Singleton
class HtmlParserImpl extends HtmlParser {

  /**
   * Parse html content, extracts title and "raw" body (removing all html tags)
   */
  override def parse(html: String): HtmlParseResult = {
    val document = Jsoup.parse(html)
    val title = document.select("head title").text()
    val body = Jsoup.parse(html).select("body").text
    HtmlParseResult(title, body)

  }
}
