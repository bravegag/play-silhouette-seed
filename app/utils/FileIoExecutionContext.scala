package utils

import scala.concurrent.ExecutionContext

/**
 * Since file io is blocking, we need to execute file operations in dedicated thread pool.
 * We need this wrapper in order to prevent accident injection of non-blocking play's execution when blocking execution
 * context is expected.
 * @param underlying
 */
case class FileIoExecutionContext(underlying: ExecutionContext)
