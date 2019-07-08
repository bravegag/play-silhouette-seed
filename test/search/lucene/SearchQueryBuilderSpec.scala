package search.lucene

import play.api.test.{ PlaySpecification, WithApplication }
import SearchQueryBuilder._

class SearchQueryBuilderSpec extends PlaySpecification {

  "build" in new Context {
    val query1 = SearchQueryBuilder.build("john snow", luceneIndex.analyzer).get
    val expectedQuery1 = "+(title:john* body:john*) +(title:snow* body:snow*)"
    query1.toString must beEqualTo(expectedQuery1)

    val query2 = SearchQueryBuilder.build("john", luceneIndex.analyzer).get
    val expectedQuery2 = "title:john* body:john*"
    query2.toString must beEqualTo(expectedQuery2)

    val query3 = SearchQueryBuilder.build("   ", luceneIndex.analyzer)
    query3.isDefined must beEqualTo(false)
  }

  "build with lang" in new Context {
    val query1 = SearchQueryBuilder
      .build("john snow", luceneIndex.analyzer)
      .get
      .withLang("en")
    val expectedQuery1 =
      "+(+(title:john* body:john*) +(title:snow* body:snow*)) +lang:en"
    query1.toString must beEqualTo(expectedQuery1)

    val query2 =
      SearchQueryBuilder.build("john", luceneIndex.analyzer).get.withLang("es")
    val expectedQuery2 = "+(title:john* body:john*) +lang:es"
    query2.toString must beEqualTo(expectedQuery2)

  }

  trait Context extends WithApplication {
    val luceneIndex = app.injector.instanceOf[LuceneIndex]
  }
}
