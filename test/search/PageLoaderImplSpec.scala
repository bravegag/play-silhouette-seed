package search

import configuration.search.SearchConfig
import play.api.test.{ PlaySpecification, WithApplication }
import search.PagesLoader.LocalizedPath
import utils.FileIoExecutionContext
import org.specs2.concurrent.ExecutionEnv

class PageLoaderImplSpec(implicit ee: ExecutionEnv) extends PlaySpecification {

  sequential

  "getHtmlFilesPaths" should {
    "fail if indexable html base path does not exist" in new Context {
      val indexerConfig = app.injector
        .instanceOf[SearchConfig]
        .indexer
        .copy(indexableHtmlFilesBasePath = "/some/non-existing-path")
      val searchConfig =
        app.injector.instanceOf[SearchConfig].copy(indexer = indexerConfig)

      override val pagesLoader: PageLoaderImpl = new PageLoaderImpl(
        searchConfig,
        app.injector.instanceOf[HtmlParser]
      )(app.injector.instanceOf[FileIoExecutionContext])

      val result = pagesLoader.getHtmlFilesPaths

      result must throwA[IllegalArgumentException](
        "Failed to get html file paths to index"
      ).await
    }
    "return html file paths, ignoring other file types and only paths which start with supported languages" in new Context {

      val expectedPaths = Seq(
        LocalizedPath("en", "/en/page1.html"),
        LocalizedPath("en", "/en/page2.html"),
        LocalizedPath("es", "/es/page1.html")
      )

      val actualPaths = pagesLoader.getHtmlFilesPaths

      actualPaths should containTheSameElementsAs(expectedPaths).await
    }
  }

  "getLocalizedPaths" should {
    "return only paths which start with supported languages" in new Context {

      val actualPaths = pagesLoader.getLocalizedPaths(
        Seq(
          "/en/page1.html",
          "/en/page2.html",
          // this path does not start with language, should be ignored
          "/somepath",
          "/es/page1.html",
          "/es/page2.html",
          // fr language is not supported, so these paths should be ignored
          "/fr/page1.html",
          "/fr/page2.html"
        )
      )

      val expectedPaths = Seq(
        LocalizedPath("en", "/en/page1.html"),
        LocalizedPath("en", "/en/page2.html"),
        LocalizedPath("es", "/es/page1.html"),
        LocalizedPath("es", "/es/page2.html")
      )

      actualPaths should containTheSameElementsAs(expectedPaths)
    }
  }

  "loadPage" should {
    "load html page and parse it" in new Context {
      val actualPage =
        pagesLoader.loadPage(LocalizedPath("en", "/en/page1.html"))

      val expectedPage = Page(
        "/en/page1.html",
        "This is a super page",
        "Hello, World! This is a content of html page to test",
        "en"
      )

      actualPage must beEqualTo(expectedPage).await
    }
  }

  /**
   * Context reused by all tests
   */
  trait Context extends WithApplication {
    val pagesLoader = app.injector.instanceOf[PageLoaderImpl]
  }
}
