package search

import play.api.test.{ PlaySpecification, WithApplication }

class HtmlParserImplSpec extends PlaySpecification {

  sequential

  "parse" should {
    "extract title and raw body from the html document" in new Context {
      val html =
        """
          <html>
            <head>
              <title>This is a super page</title>
            </head>
            <body>
              <h1>Hello, World!</h1>
              This is a content of html page to test
            </body>
          </html>
        """.stripMargin

      val result = parser.parse(html)
      result.title must beEqualTo("This is a super page")
      result.body must beEqualTo(
        "Hello, World! This is a content of html page to test")
    }
    "ignore javascript and css" in new Context {
      val html =
        """
          <html>
            <head>
              <title>This is a super page</title>
              <style>
                body {
                    font-size: 12px;
                }
              </style>
            </head>
            <body>
              This is a content of html page to test
              <script>
                const a = 10;
              </script>
            </body>
          </html>
        """.stripMargin

      val result = parser.parse(html)
      result.body must beEqualTo("This is a content of html page to test")
    }
    "parse incorrect html file" in new Context {
      val html =
        """
          <html>
            <head>
              <title>This is a super page</title>
              <style>
                body {
                    font-size: 12px;
                }
              </style>
            </head>
            <body>
              This is a content of html page to test
              <script>
                const a = 10;
              </script>
          </html>
        """.stripMargin

      val result = parser.parse(html)
      result.body must beEqualTo("This is a content of html page to test")
    }
  }

  trait Context extends WithApplication {
    val parser = app.injector.instanceOf[HtmlParserImpl]
  }
}
