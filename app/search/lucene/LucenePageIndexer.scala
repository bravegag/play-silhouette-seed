package search.lucene

import javax.inject.{ Inject, Singleton }
import org.apache.lucene.document.{ Document, Field, TextField }
import org.apache.lucene.index.IndexWriter
import search.Page

import scala.concurrent.Future

@Singleton
class LucenePageIndexer @Inject() (implicit ec: LuceneExecutionContext) {

  /**
   * Index single page
   *
   * @param page
   * @return
   */
  def index(page: Page, indexWriter: IndexWriter): Future[Page] =
    Future {

      // TODO:: set `path` as primary key
      val document = new Document
      document.add(new TextField("path", page.path, Field.Store.YES))
      document.add(new TextField("lang", page.lang, Field.Store.YES))
      document.add(new TextField("title", page.title, Field.Store.YES))
      document.add(new TextField("body", page.body, Field.Store.YES))

      indexWriter.addDocument(document)
      page
    }(ec.underlying)
}
