package search

import java.io.File

import configuration.search.SearchConfig
import javax.inject.{ Inject, Singleton }
import search.PagesLoader.LocalizedPath
import utils.FileIoExecutionContext

import scala.concurrent.Future
import scala.io.Source
import utils.ResourceUtil._

/**
 * Loads pages from the directory provided in configuration
 * @param searchConfig
 * @param ec
 */
@Singleton
class PageLoaderImpl @Inject() (
  searchConfig: SearchConfig,
  htmlParser: HtmlParser)(implicit ec: FileIoExecutionContext)
  extends PagesLoader {

  /**
   * Get all paths of existing html pages to index
   *
   * @return
   */
  override def getHtmlFilesPaths: Future[Seq[LocalizedPath]] =
    Future {
      val file = new File(searchConfig.indexer.indexableHtmlFilesBasePath)
      if (!file.exists()) {
        throw new IllegalArgumentException(
          s"Failed to get html file paths to index, directory ${searchConfig.indexer.indexableHtmlFilesBasePath}" +
            s" does not exist")
      }
      val allFiles = recursiveListFiles(file)
      val htmlFiles =
        allFiles.filter(f => """.*\.html$""".r.findFirstIn(f).isDefined)
      val localizedFiles = getLocalizedPaths(htmlFiles)

      localizedFiles
    }(ec.underlying)

  /**
   * Walk directory recursively and get all paths relative
   * to [[configuration.ApplicationConfig.search.indexer.indexableHtmlFilesBasePath]]
   * @param f
   * @return
   */
  private def recursiveListFiles(f: File): Seq[String] = {
    def doRecursiveListFiles(f: File): Array[File] = {
      val these = f.listFiles
      these ++ these.filter(_.isDirectory).flatMap(doRecursiveListFiles)
    }

    doRecursiveListFiles(f)
      .map { file =>
        val path = file.getPath
        val pathWithoutIndexableHtmlFilesBasePath =
          path.substring(searchConfig.indexer.indexableHtmlFilesBasePath.length)
        pathWithoutIndexableHtmlFilesBasePath
      }
      .to[collection.immutable.Seq]
  }

  /**
   * Get only paths which start with any of supported language
   * (like `/en/some/file.html`, `/es/some/file.html`)
   * @param paths
   * @return
   */
  private[search] def getLocalizedPaths(
    paths: Seq[String]): Seq[LocalizedPath] = {
    val supportedLanguages = searchConfig.supportedLanguages.mkString("|")
    val regex = s"^/($supportedLanguages)/.*".r

    paths.foldLeft(Nil: Seq[LocalizedPath]) { (localizedPaths, rawPath) =>
      regex.findFirstMatchIn(rawPath) match {
        case Some(result) =>
          val language = result.group(1)
          val localizedPath = LocalizedPath(language, rawPath)
          localizedPaths :+ localizedPath
        case None => localizedPaths
      }
    }
  }

  /**
   * Load given html page by path and parse it
   *
   * @param localizedPath
   * @return
   */
  override def loadPage(localizedPath: LocalizedPath): Future[Page] =
    Future {
      val absolutePath =
        s"${searchConfig.indexer.indexableHtmlFilesBasePath}${localizedPath.path}"
      using(Source.fromFile(absolutePath)) { source =>
        val content = source.getLines.mkString("\n")
        val parseHtmlResult = htmlParser.parse(content)

        Page(
          localizedPath.path,
          parseHtmlResult.title,
          parseHtmlResult.body,
          localizedPath.language)
      }

    }(ec.underlying)
}
