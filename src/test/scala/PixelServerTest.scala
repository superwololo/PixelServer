package com.freemonetize.pixelserver


import org.scalatest.{Matchers, WordSpec}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.http.scaladsl.model.{StatusCodes, MediaTypes}


class PixelServerTest extends WordSpec with Matchers with ScalatestRouteTest {

  "The PixelServer" should {

    "successfully handle properly formatted /pv endpoint" in {
      Get("/pv?uid=123&url=testurl") ~> PixelServer.route ~> check {
        status shouldEqual StatusCodes.OK
        mediaType shouldEqual MediaTypes.`image/gif`
      }
    }

    "reject the /pv endpoint if it is missing information" in {
      Get("/pv?uid=123") ~> PixelServer.route ~> check {
        handled shouldBe false
      }
    }

    "return a 200 OK response for the /live endpoint" in {
      Get("/live") ~> PixelServer.route ~> check {
        responseAs[String] shouldEqual "OK"
      }
    }

    "not handle unknown endpoints" in {
      Get("/unknown") ~> PixelServer.route ~> check {
        handled shouldBe false
      }
    }
  }

}
