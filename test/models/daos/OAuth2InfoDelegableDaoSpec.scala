package models.daos

import com.mohiva.play.silhouette.impl.providers.OAuth2Info
import com.mohiva.play.silhouette.persistence.daos.DelegableAuthInfoDAO

import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Test suite for the [[OAuth2InfoDelegableDao]]
 */
class OAuth2InfoDelegableDaoSpec extends BaseDaoSpec {
  sequential

  "The oauth info delegable dao" should {
    "should add and find oAuth2Info" in new Context {
      val oAuth2InfoOpt: Option[OAuth2Info] = for {
        _ <- userDao.create(testUser, testLoginInfo)
        _ <- authInfoDelegableDao.add(testLoginInfo, testOAuth2Info)
        oAuth2Info <- authInfoDelegableDao.find(testLoginInfo)
      } yield oAuth2Info

      oAuth2InfoOpt should not be None
      val oAuth2Info: OAuth2Info = oAuth2InfoOpt.get
      oAuth2Info.accessToken should beEqualTo(testOAuth2Info.accessToken)
      oAuth2Info.tokenType should beEqualTo(testOAuth2Info.tokenType)
      oAuth2Info.expiresIn should beEqualTo(testOAuth2Info.expiresIn)
      oAuth2Info.refreshToken should beEqualTo(testOAuth2Info.refreshToken)
    }

    "should save (insert or update) and find oAuth2Info" in new Context {
      val oAuth2InfoOpt: Option[OAuth2Info] = for {
        _ <- userDao.create(testUser, testLoginInfo)
        _ <- authInfoDelegableDao.save(testLoginInfo, testOAuth2Info)
        oAuth2Info <- authInfoDelegableDao.find(testLoginInfo)
      } yield oAuth2Info

      oAuth2InfoOpt should not be None
      val oAuth2Info: OAuth2Info = oAuth2InfoOpt.get
      oAuth2Info.accessToken should beEqualTo(testOAuth2Info.accessToken)
      oAuth2Info.tokenType should beEqualTo(testOAuth2Info.tokenType)
      oAuth2Info.expiresIn should beEqualTo(testOAuth2Info.expiresIn)
      oAuth2Info.refreshToken should beEqualTo(testOAuth2Info.refreshToken)
    }

    "should update oAuth2Info" in new Context {
      val oAuth2InfoOpt: Option[OAuth2Info] = for {
        _ <- userDao.create(testUser, testLoginInfo)
        _ <- authInfoDelegableDao.add(testLoginInfo, testOAuth2Info)
        _ <- authInfoDelegableDao.update(testLoginInfo, testOAuth2Info2)
        oAuth2Info <- authInfoDelegableDao.find(testLoginInfo)
      } yield oAuth2Info

      oAuth2InfoOpt should not be None
      val oAuth2Info: OAuth2Info = oAuth2InfoOpt.get
      oAuth2Info.accessToken should beEqualTo(testOAuth2Info2.accessToken)
      oAuth2Info.tokenType should beEqualTo(testOAuth2Info2.tokenType)
      oAuth2Info.expiresIn should beEqualTo(testOAuth2Info2.expiresIn)
      oAuth2Info.refreshToken should beEqualTo(testOAuth2Info2.refreshToken)
    }

    "should remove oAuth2Info" in new Context {
      val oAuth2InfoOpt: Option[OAuth2Info] = for {
        _ <- userDao.create(testUser, testLoginInfo)
        _ <- authInfoDelegableDao.save(testLoginInfo, testOAuth2Info)
        oAuth2Info <- authInfoDelegableDao.find(testLoginInfo)
      } yield oAuth2Info

      oAuth2InfoOpt should not be None

      val emptyOAuth2InfoOpt: Option[OAuth2Info] = for {
        _ <- authInfoDelegableDao.remove(testLoginInfo)
        oAuth2Info <- authInfoDelegableDao.find(testLoginInfo)
      } yield oAuth2Info

      emptyOAuth2InfoOpt should be(None)
    }
  }

  /**
   * Context reused by all tests
   */
  trait Context extends BaseContext {

    val authInfoDelegableDao: DelegableAuthInfoDAO[OAuth2Info] = daoContext.oAuth2InfoDelegableDao

    // ensure repeatability of the test
    await(userDao.deleteAll)
  }
}