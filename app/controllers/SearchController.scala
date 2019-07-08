package controllers

import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.util.Clock
import configuration.ApplicationConfig
import javax.inject.Inject
import models.services.{ LoginInfoService, UserService }
import org.webjars.play.WebJarsUtil
import play.api.Configuration
import play.api.i18n.I18nSupport
import search.Searcher
import search.Searcher.SearchRequest
import utils.Pagination
import utils.auth.DefaultEnv

import scala.concurrent.ExecutionContext
import scala.util.{ Failure, Success }

class SearchController @Inject() (
  silhouette: Silhouette[DefaultEnv],
  searcher: Searcher,
  configuration: Configuration,
  clock: Clock,
  loginInfoService: LoginInfoService
)(
  implicit
  webJarsUtil: WebJarsUtil,
  assets: AssetsFinder,
  userService: UserService,
  ec: ExecutionContext,
  authInfoRepository: AuthInfoRepository,
  applicationConfig: ApplicationConfig
) extends AbstractAuthController(silhouette, configuration, clock)
  with I18nSupport {

  def search(
    searchQuery: String,
    lang: String,
    page: Int = 1,
    hitsPerPage: Option[Int] = None) = Action.async {
    implicit request =>
      val searchRequest = SearchRequest(searchQuery, lang, page, hitsPerPage)
      searcher.search(searchRequest).map { searchResult =>
        val url = routes.SearchController.search(searchQuery, lang, 1).url
        Pagination(
          searchResult.totalPages,
          searchResult.currentPage,
          searchResult.hitsPerPage,
          applicationConfig.pagination.pagesInRange,
          url) match {
            case Success(paginationOpt) =>
              Ok(views.html.search(searchResult, paginationOpt))
            case Failure(ex) => throw ex
          }
      }
  }

}
