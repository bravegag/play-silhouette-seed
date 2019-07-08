package utils

import org.specs2.specification.Scope
import play.api.test.PlaySpecification

import scala.util.Success

class PaginationSpec extends PlaySpecification {

  "apply" >> {
    "when totalPages < 0" >> {
      "should fail" >> {
        Pagination(-1, 0, 0, 0, "https://example.com/search") must beFailedTry
          .like {
            case e: IllegalArgumentException =>
              e.getMessage must startWith(
                "totalPages must be greater or equal 0")
          }
      }
    }
    "when currentPage < 0" >> {
      "should fail" >> {
        Pagination(1, -1, 0, 0, "https://example.com/search") must beFailedTry
          .like {
            case e: IllegalArgumentException =>
              e.getMessage must startWith(
                "currentPage must be greater or equal 0")
          }
      }
    }
    "when hitsPerPage < 1" >> {
      "should fail" >> {
        Pagination(1, 1, 0, 0, "https://example.com/search") must beFailedTry
          .like {
            case e: IllegalArgumentException =>
              e.getMessage must startWith(
                "hitsPerPage must be greater or equal 1")
          }
      }
    }
    "when pagesInRange < 1" >> {
      "should fail" >> {
        Pagination(1, 1, 1, 0, "https://example.com/search") must beFailedTry
          .like {
            case e: IllegalArgumentException =>
              e.getMessage must startWith(
                "pagesInRange must be greater or equal 1")
          }
      }
    }
    "when totalPages == 0 and currentPage != 0" >> {
      "should fail" >> {
        Pagination(0, 1, 1, 1, "https://example.com/search") must beFailedTry
          .like {
            case e: IllegalArgumentException =>
              e.getMessage must startWith(
                "if either totalPages or currentPage is 0, both of them must equal 0")
          }
      }
    }
    "when totalPages != 0 and currentPage == 0" >> {
      "should fail" >> {
        Pagination(1, 0, 1, 1, "https://example.com/search") must beFailedTry
          .like {
            case e: IllegalArgumentException =>
              e.getMessage must startWith(
                "if either totalPages or currentPage is 0, both of them must equal 0")
          }
      }
    }
    "when currentPage > totalPages" >> {
      "should fail" >> {
        Pagination(1, 2, 1, 1, "https://example.com/search") must beFailedTry
          .like {
            case e: IllegalArgumentException =>
              e.getMessage must startWith(
                "currentPage cannot be greater than totalPages")
          }
      }
    }
    "when url is empty" >> {
      "should fail" >> {
        Pagination(1, 1, 1, 1, "") must beFailedTry.like {
          case e: IllegalArgumentException =>
            e.getMessage must startWith("url must not be empty")
        }
        Pagination(1, 1, 1, 1, "   ") must beFailedTry.like {
          case e: IllegalArgumentException =>
            e.getMessage must startWith("url must not be empty")
        }
      }
    }
    "when totalPages == 0 and currentPage == 0" >> {
      "should return None" >> {
        Pagination(0, 0, 1, 1, "https://example.com/search") should beEqualTo(
          Success(None))
      }
    }
    "when totalPages == 1" >> {
      "should return None" >> {
        Pagination(1, 1, 1, 1, "https://example.com/search") should beEqualTo(
          Success(None))
      }
    }
    "when all check passed" >> {
      "should create Pagination" >> new Context {
        val pagination = createPagination(99, 2, 20, 10)
        pagination.totalPages should beEqualTo(99)
        pagination.currentPage should beEqualTo(2)
        pagination.hitsPerPage should beEqualTo(20)
        pagination.pagesInRange should beEqualTo(10)
      }
    }
  }

  "firstPage" >> {
    "when current page is 1" >> {
      "should return None" >> new Context {
        val pagination = createPagination(currentPage = 1)
        pagination.firstPage should beNone
      }
    }
    "when current page > 1" >> {
      "should return 1" >> new Context {
        val pagination = createPagination(currentPage = 2)
        pagination.firstPage should beSome(1)
      }
    }
  }

  "prevPage" >> {
    "when current page is 1" >> {
      "should return None" >> new Context {
        val pagination = createPagination(currentPage = 1)
        pagination.prevPage should beNone
      }
    }
    "when current page > 1" >> {
      "should return previous pagfe" >> new Context {
        val pagination = createPagination(currentPage = 2)
        pagination.prevPage should beSome(1)
      }
    }
  }

  "nextPage" >> {
    "when current page equals to last page" >> {
      "should return None" >> new Context {
        val pagination = createPagination(totalPages = 9, currentPage = 9)
        pagination.nextPage should beNone
      }
    }
    "when current page is less than last page" >> {
      "should return next page" >> new Context {
        val pagination = createPagination(totalPages = 9, currentPage = 8)
        pagination.nextPage should beSome(9)
      }
    }
  }

  "lastPage" >> {
    "when current page equals to last page" >> {
      "should return None" >> new Context {
        val pagination = createPagination(totalPages = 9, currentPage = 9)
        pagination.lastPage should beNone
      }
    }
    "when current page is less than last page" >> {
      "should return last page" >> new Context {
        val pagination = createPagination(totalPages = 9, currentPage = 8)
        pagination.lastPage should beSome(9)
      }
    }
  }

  "currentRange" >> new Context {
    createPagination(totalPages = 99, currentPage = 2, pagesInRange = 10).currentRange should beEqualTo(
      1)
    createPagination(totalPages = 99, currentPage = 11, pagesInRange = 10).currentRange should beEqualTo(
      2)
    createPagination(totalPages = 99, currentPage = 10, pagesInRange = 10).currentRange should beEqualTo(
      1)
    createPagination(totalPages = 2, currentPage = 1, pagesInRange = 10).currentRange should beEqualTo(
      1)
    createPagination(totalPages = 100, currentPage = 100, pagesInRange = 10).currentRange should beEqualTo(
      10)
    createPagination(totalPages = 15, currentPage = 14, pagesInRange = 7).currentRange should beEqualTo(
      2)
  }

  "totalRanges" >> new Context {
    createPagination(totalPages = 99, currentPage = 2, pagesInRange = 10).totalRanges should beEqualTo(
      10)
    createPagination(totalPages = 99, currentPage = 1, pagesInRange = 20).totalRanges should beEqualTo(
      5)
    createPagination(totalPages = 2, currentPage = 1, pagesInRange = 10).totalRanges should beEqualTo(
      1)
    createPagination(totalPages = 100, currentPage = 1, pagesInRange = 10).totalRanges should beEqualTo(
      10)
  }

  "startPageOfRange" >> new Context {
    createPagination(pagesInRange = 10).startPageOfRange(1) should beEqualTo(1)
    createPagination(pagesInRange = 10).startPageOfRange(2) should beEqualTo(11)
    createPagination(pagesInRange = 7).startPageOfRange(2) should beEqualTo(8)
  }

  "startPageOfPrevRange" >> {
    "when current range is first" >> {
      "should return None" >> new Context {
        createPagination(totalPages = 99, currentPage = 3, pagesInRange = 10).startPageOfPrevRange should beNone
      }
    }
    "when current range is not first one" >> {
      "should return start page of previous range" >> new Context {
        createPagination(totalPages = 99, currentPage = 21, pagesInRange = 10).startPageOfPrevRange should beSome(
          11)
      }
    }
  }

  "startPageOfNextRange" >> {
    "when current range is last one" >> {
      "should return None" >> new Context {
        createPagination(totalPages = 99, currentPage = 98, pagesInRange = 10).startPageOfNextRange should beNone
      }
    }
    "when current range is less than total ranges" >> {
      "should return start page of the next range" >> new Context {
        createPagination(totalPages = 99, currentPage = 21, pagesInRange = 10).startPageOfNextRange should beSome(
          31)
      }
    }
  }

  "startPageInCurrentRange" >> new Context {
    createPagination(99, 1, 20, 10).startPageInCurrentRange should beEqualTo(1)
    createPagination(99, 10, 20, 10).startPageInCurrentRange should beEqualTo(1)
    createPagination(99, 2, 20, 10).startPageInCurrentRange should beEqualTo(1)
    createPagination(100, 100, 20, 10).startPageInCurrentRange should beEqualTo(
      91)
    createPagination(2, 1, 20, 10).startPageInCurrentRange should beEqualTo(1)
    createPagination(15, 6, 20, 7).startPageInCurrentRange should beEqualTo(1)
    createPagination(15, 9, 20, 7).startPageInCurrentRange should beEqualTo(8)
  }

  "endPageInCurrentRange" >> new Context {
    createPagination(99, 1, 20, 10).endPageInCurrentRange should beEqualTo(10)
    createPagination(99, 10, 20, 10).endPageInCurrentRange should beEqualTo(10)
    createPagination(99, 2, 20, 10).endPageInCurrentRange should beEqualTo(10)
    createPagination(99, 12, 20, 10).endPageInCurrentRange should beEqualTo(20)
    createPagination(100, 100, 20, 10).endPageInCurrentRange should beEqualTo(
      100)
    createPagination(99, 97, 20, 10).endPageInCurrentRange should beEqualTo(99)
    createPagination(102, 101, 20, 10).endPageInCurrentRange should beEqualTo(
      102)
    createPagination(15, 6, 20, 7).endPageInCurrentRange should beEqualTo(7)
    createPagination(15, 9, 20, 7).endPageInCurrentRange should beEqualTo(14)
    createPagination(15, 15, 20, 7).endPageInCurrentRange should beEqualTo(15)
  }

  "urlWithPage" >> {
    "should add page to the url" >> new Context {
      val pagination = createPagination(url = "https://example.com/search")
      pagination.urlWithPage(3) must beEqualTo(
        "https://example.com/search?page=3")
    }
    "should replace `page` param with new value" >> new Context {
      val pagination =
        createPagination(url = "https://example.com/search?page=10&lang=en")
      pagination.urlWithPage(3) must beEqualTo(
        "https://example.com/search?lang=en&page=3")
    }
    "should handle relative urls" >> new Context {
      val pagination = createPagination(url = "/search?page=10")
      pagination.urlWithPage(3) must beEqualTo("/search?page=3")
    }
  }

  trait Context extends Scope {
    def createPagination(
      totalPages: Int = 93,
      currentPage: Int = 1,
      hitsPerPage: Int = 10,
      pagesInRange: Int = 10,
      url: String = "https://example.com/search"): Pagination =
      Pagination(totalPages, currentPage, hitsPerPage, pagesInRange, url).get.get
  }

}
