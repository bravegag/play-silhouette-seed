package search.lucene

import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.store.Directory

/**
 * Lucene index directory and analyzer
 * @param indexDirectory
 * @param analyzer
 */
case class LuceneIndex(indexDirectory: Directory, analyzer: Analyzer)
