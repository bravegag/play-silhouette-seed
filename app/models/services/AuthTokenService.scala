package models.services

import java.util.UUID
import models.generated.Tables.AuthTokenRow

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.language.postfixOps

/**
 * Handles actions to auth tokens.
 */
trait AuthTokenService {

  /**
   * Creates a new auth token and saves it in the backing store.
   *
   * @param userId The user ID for which the token should be created.
   * @param expiry The duration a token expires.
   * @return The saved auth token.
   */
  def create(userId: Long, expiry: FiniteDuration = 5 minutes): Future[AuthTokenRow]

  /**
   * Validates a token ID.
   *
   * @param id The token ID to validate.
   * @return The token if it's valid, None otherwise.
   */
  def validate(id: UUID): Future[Option[AuthTokenRow]]

  /**
   * Cleans expired tokens.
   *
   * @return The list of deleted tokens.
   */
  def clean: Future[Seq[AuthTokenRow]]
}
