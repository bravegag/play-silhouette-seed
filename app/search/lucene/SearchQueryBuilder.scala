package search.lucene

import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.index.Term
import org.apache.lucene.queryparser.classic.{
  MultiFieldQueryParser,
  QueryParser
}
import org.apache.lucene.search.{
  BooleanClause,
  BooleanQuery,
  Query,
  TermQuery,
  WildcardQuery
}

object SearchQueryBuilder {

  /**
   * Build lucene query from "free" search query provided by user
   *
   * Example:
   * Given that search query is `john snow`, generated query will look like:
   *
   * (title:john* AND title:snow*) OR (body:john* AND body:snow*)
   * (title:john* OR body:john*) AND (title:snow* OR body:snow*)
   *
   * @param rawSearchQuery - search query entered by the user
   * @param analyzer
   * @return
   */
  def build(rawSearchQuery: String, analyzer: Analyzer): Option[Query] = {
    rawSearchQuery
      .trim()
      .split("\\s+")
      .to[scala.collection.immutable.List] match {
        case head :: Nil if head.isEmpty => None
        case Nil => None
        case keywords =>
          // Split searchQuery into key words, add * to each key word and construct back search query with * after each word
          val wildCardedKeyWords = keywords.map(keyWord => s"$keyWord*")
          val wildCardedSearchQuery = wildCardedKeyWords.mkString(" ")

          val queryParser =
            new MultiFieldQueryParser(Array("title", "body"), analyzer)
          queryParser.setDefaultOperator(QueryParser.Operator.AND)

          val query = queryParser.parse(wildCardedSearchQuery)

          Some(query)
      }
  }

  implicit class QueryOps(query: Query) {

    /**
     * Add lang condition to query
     * @param lang
     * @return
     */
    def withLang(lang: String): Query = {
      val langQuery = new TermQuery(new Term("lang", lang))
      val queryWithLangBuilder = new BooleanQuery.Builder()
        .add(query, BooleanClause.Occur.MUST)
        .add(langQuery, BooleanClause.Occur.MUST)
      queryWithLangBuilder.build()
    }
  }
}
