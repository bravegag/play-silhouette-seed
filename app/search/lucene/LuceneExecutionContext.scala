package search.lucene

import scala.concurrent.ExecutionContext

/**
 * Since lucene library is blocking, we need to execute lucene operations in dedicated thread pool.
 * We need this wrapper in order to prevent accident injection of non-blocking play's execution context
 * where blocking lucene execution context is expected.
 *
 * @param underlying
 */
case class LuceneExecutionContext(underlying: ExecutionContext)
