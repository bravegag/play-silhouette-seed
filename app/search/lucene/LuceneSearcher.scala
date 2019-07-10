package search.lucene

import com.github.ghik.silencer.silent
import configuration.search.SearchConfig
import javax.inject.{ Inject, Singleton }
import org.apache.lucene.document.Document
import org.apache.lucene.index.{ DirectoryReader, IndexReader }
import org.apache.lucene.search.highlight.{
  Highlighter,
  QueryScorer,
  SimpleHTMLFormatter,
  SimpleSpanFragmenter
}
import org.apache.lucene.search.{
  IndexSearcher,
  Query,
  ScoreDoc,
  TopDocs,
  TopScoreDocCollector
}
import search.Searcher.{ Hit, SearchRequest, SearchResult }
import search.{ Page, Searcher }
import utils.ResourceUtil
import org.apache.lucene.search.highlight.TokenSources
import SearchQueryBuilder._
import scala.concurrent.Future

@Singleton
class LuceneSearcher @Inject() (
  luceneIndex: LuceneIndex,
  searchConfig: SearchConfig)(implicit ec: LuceneExecutionContext)
  extends Searcher {
  import LuceneSearcher._

  /**
   * Search html pages by query string
   * @param request
   * @return
   */
  override def search(request: SearchRequest): Future[SearchResult] =
    Future {
      // TODO:: validate `request.lang`
      val hitsPerPage = request.hitsPerPage.getOrElse(searchConfig.hitsPerPage)
      SearchQueryBuilder.build(request.searchQuery, luceneIndex.analyzer) match {
        case Some(query) =>
          ResourceUtil.using(DirectoryReader.open(luceneIndex.indexDirectory)) {
            indexReader =>
              val highlighter = constructHighlighter(query)
              val collector = TopScoreDocCollector.create(1000, 1000)
              val searcher = new IndexSearcher(indexReader)
              val startIndex = (request.page - 1) * hitsPerPage

              searcher.search(query.withLang(request.lang), collector)

              val topDocs: TopDocs = collector.topDocs(startIndex, hitsPerPage)

              val hits = topDocs.scoreDocs.zipWithIndex.map {
                case (topDoc: ScoreDoc, i) =>
                  val page = luceneDocToPage(searcher.doc(topDoc.doc))
                  val titleHighlightedFragments =
                    getHighlightedFragments(
                      highlighter,
                      indexReader,
                      topDoc,
                      "title",
                      page.title)
                  val bodyHighlightedFragments =
                    getHighlightedFragments(
                      highlighter,
                      indexReader,
                      topDoc,
                      "body",
                      page.body)
                  Hit(startIndex + i + 1, page, titleHighlightedFragments, bodyHighlightedFragments)
              }.to[scala.collection.immutable.List]

              val currentPage = if (hits.isEmpty) 0 else request.page

              SearchResult(
                hits,
                hitsPerPage,
                topDocs.totalHits.value,
                calculateTotalPages(
                  topDocs.totalHits.value,
                  hitsPerPage),
                currentPage,
                request.searchQuery)
          }
        case None =>
          SearchResult(Nil, hitsPerPage, 0, 0, 0, request.searchQuery)
      }
    }(ec.underlying)

  private def constructHighlighter(query: Query): Highlighter = {
    val formatter = new SimpleHTMLFormatter
    val scorer = new QueryScorer(query)
    val highlighter = new Highlighter(formatter, scorer)
    val fragmenter = new SimpleSpanFragmenter(scorer, 50)
    highlighter.setTextFragmenter(fragmenter)

    highlighter
  }

  /**
   * Construct highlighted fragments of given field and its value.
   * If no highlighted fragments found, return sequence of single element where element is original value
   * @param highlighter
   * @param indexReader
   * @param topDoc
   * @param field
   * @param value
   * @return
   */
  private def getHighlightedFragments(
    highlighter: Highlighter,
    indexReader: IndexReader,
    topDoc: ScoreDoc,
    field: String,
    value: String): Seq[String] = {
    // Suppress TokenSources.getAnyTokenStream's deprecated warning
    // TODO:: find the way to highlight without TokenSources.getAnyTokenStream
    // (@silent is Scala's equivalent of Java's @SuppressWarning)
    @silent("deprecated")
    val stream = TokenSources.getAnyTokenStream(
      indexReader,
      topDoc.doc,
      field,
      luceneIndex.analyzer)
    highlighter.getBestFragments(stream, value, 5).toSeq match {
      case Nil => Seq(value)
      case fragments => fragments
    }
  }
}

object LuceneSearcher {
  def luceneDocToPage(document: Document): Page = {
    val path = document.getField("path").stringValue()
    val title = document.getField("title").stringValue()
    val body = document.getField("body").stringValue()
    val lang = document.getField("lang").stringValue()

    Page(path, title, body, lang)
  }

  def calculateTotalPages(totalHits: Long, hitsPerPage: Int): Int =
    Math.ceil(totalHits * 1.0 / hitsPerPage).toInt
}
