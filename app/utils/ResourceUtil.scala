package utils

import scala.concurrent.{ ExecutionContext, Future }
import scala.language.reflectiveCalls

object ResourceUtil {

  /**
   * Automatically closes the resource
   * @param resource
   * @param f
   * @tparam A
   * @tparam B
   * @return
   */
  def using[A <: { def close(): Unit }, B](resource: A)(f: A => B): B =
    try {
      f(resource)
    } finally {
      resource.close()
    }

  /**
   * Automatically closes the resource for async operation
   * @param resource
   * @param f
   * @param ec
   * @tparam A
   * @tparam B
   * @return
   */
  def usingAsync[A <: { def close(): Unit }, B](resource: A)(f: A => Future[B])(
    implicit
    ec: ExecutionContext): Future[B] =
    try {
      f(resource).andThen {
        case result =>
          resource.close()
          result
      }
    } catch {
      case e: Throwable =>
        resource.close()
        throw e
    }
}
