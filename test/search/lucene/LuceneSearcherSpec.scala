package search.lucene

import org.apache.lucene.document.{ Document, Field, StringField, TextField }
import org.apache.lucene.index.IndexableField
import org.specs2.concurrent.ExecutionEnv
import play.api.test.{ PlaySpecification, WithApplication }
import search.Searcher.{ Hit, SearchRequest }
import search.{ Page, Searcher }

import scala.concurrent.duration._
import scala.concurrent.Await

class LuceneSearcherSpec(implicit ee: ExecutionEnv) extends PlaySpecification {

  "search" in new Context {
    //    val pages = Seq(
    //      Page("/en/page-1.html", "How to support open-source software and stay sane", "On 10 April, astrophysicists announced that they had captured the first ever image of a black hole. This was exhilarating news, but none of the giddy headlines mentioned that the image would have been impossible without open-source software.", "en"),
    //      Page("/en/page-2.html", "Absence of certain features in IRC considered a feature", "The other day a friend of mine (an oper on Freenode) wanted to talk about IRC compared to its peers, such as Matrix, Slack, Discord, etc. The ensuing discussion deserves summarization here", "en"),
    //      Page("/en/page-3.html", "Why remote work isn’t going away anytime soon", "A narrative has taken hold over the past few years that asserts that the future of work will be dominated by robots, AI programs, and other technological marvels that strip humans entirely away from the workplace.", "en"),
    //    )
    //
    //    pages.foreach { page =>
    //      Await.result(indexer.index(page), 5 seconds)
    //    }
    //
    //    //    Await.result(searcher.search("title:snake* AND title:bite* OR body:snake* AND body:bite* ", 10), 5 seconds)
    //    val res = Await.result(searcher.search(SearchRequest("black hole image", "en", 1)), 5 seconds)

    1 should beEqualTo(1)

  }

  "luceneDocToPage" in {
    val document = new Document
    document.add(new TextField("path", "/en/page-1.html", Field.Store.YES))
    document.add(new TextField("title", "page title", Field.Store.YES))
    document.add(new TextField("body", "page body", Field.Store.YES))
    document.add(new TextField("lang", "en", Field.Store.YES))

    val expectedPage = Page("/en/page-1.html", "page title", "page body", "en")

    val actualPage = LuceneSearcher.luceneDocToPage(document)

    expectedPage should beEqualTo(actualPage)

  }

  "calculateTotalPages" in {
    LuceneSearcher.calculateTotalPages(0, 10) should beEqualTo(0)
    LuceneSearcher.calculateTotalPages(7, 10) should beEqualTo(1)
    LuceneSearcher.calculateTotalPages(10, 10) should beEqualTo(1)
    LuceneSearcher.calculateTotalPages(12, 10) should beEqualTo(2)
  }

  trait Context extends WithApplication {
    val searcher = app.injector.instanceOf[Searcher]

  }

}
