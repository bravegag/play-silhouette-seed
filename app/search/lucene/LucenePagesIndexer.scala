package search.lucene

import javax.inject.{ Inject, Singleton }
import org.apache.lucene.index.IndexWriterConfig.OpenMode
import org.apache.lucene.index.{ IndexWriter, IndexWriterConfig }
import play.api.Logger
import search.{ Page, PagesIndexer, PagesLoader }
import search.PagesLoader.LocalizedPath
import utils.ResourceUtil

import scala.concurrent.{ ExecutionContext, Future }

/**
 * Index html pages
 */
@Singleton
class LucenePagesIndexer @Inject() (
  pagesLoader: PagesLoader,
  lucenePageIndexer: LucenePageIndexer,
  luceneIndex: LuceneIndex)(implicit ec: ExecutionContext)
  extends PagesIndexer {

  private val logger = Logger(this.getClass)

  /**
   * Index all html pages located under
   * [[configuration.ApplicationConfig.search.indexer.indexableHtmlFilesBasePath]]/[any-of-supported-language] folders.
   * supported languages are configured in [[configuration.ApplicationConfig.search.supportedLanguages]]
   * @return
   */
  def index(): Future[Seq[Page]] = {
    logger.info("Indexing pages")
    for {
      paths <- pagesLoader.getHtmlFilesPaths
      pages <- indexPages(paths)
    } yield {
      logger.info(s"Successfully indexed ${pages.size} page(s)")
      pages
    }
  }

  /**
   * Load pages from paths and index, sequentially (can be done in a batch, but there is a chance (less likely
   * though, but anyway) to load too many data into memory and thus overflow it in case of many files to index.
   * @param paths
   * @return
   */
  private def indexPages(paths: Seq[LocalizedPath]): Future[Seq[Page]] = {
    // Clear index before writing
    val indexWriterConfig =
      new IndexWriterConfig(luceneIndex.analyzer).setOpenMode(OpenMode.CREATE)
    ResourceUtil.usingAsync(
      new IndexWriter(luceneIndex.indexDirectory, indexWriterConfig)) {
        writer =>
          paths.foldLeft(Future.successful(Nil: Seq[Page])) {
            (indexedPagesFuture, path) =>
              logger.info(s"Indexing page $path")
              for {
                indexedPages <- indexedPagesFuture
                page <- pagesLoader.loadPage(path)
                _ <- lucenePageIndexer.index(page, writer)
              } yield {
                indexedPages :+ page
              }
          }
      }
  }
}
