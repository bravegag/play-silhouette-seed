package modules

import javax.inject.{ Inject, Singleton }
import play.api.Logger
import search.PagesIndexer
import scala.concurrent.Await
import scala.concurrent.duration._

@Singleton
class Bootstrap @Inject() (pagesIndexer: PagesIndexer) {

  private val logger = Logger(this.getClass)

  logger.info("Invoking pages indexer")
  Await.result(pagesIndexer.index(), 5.seconds)

}
