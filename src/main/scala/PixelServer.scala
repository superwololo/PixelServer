package com.freemonetize.pixelserver


import akka.actor.{Props, ActorSystem}
import akka.stream.ActorMaterializer
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.model.{HttpEntity, MediaTypes}
import akka.http.scaladsl.server.Directives._
import scala.concurrent.Future
import org.slf4j.LoggerFactory


object PixelServer {

  val log = LoggerFactory.getLogger("pixelserver")
  implicit val system = ActorSystem("pixelserver")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  val trackingGif: Array[Byte] = Array(0x47, 0x49, 0x46, 0x38, 0x39, 0x61, 0x1, 0x0, 0x1, 0x0,
    0x80.toByte, 0x0, 0x0, 0xff.toByte, 0xff.toByte,  0xff.toByte, 0x0, 0x0, 0x0, 0x2c, 0x0,
    0x0, 0x0, 0x0, 0x1, 0x0, 0x1, 0x0, 0x0, 0x2, 0x2, 0x44, 0x1, 0x0, 0x3b
  )

  val writer = system.actorOf(Props[WriteActor], "writer")


  def shutdown(bindingFuture: Future[ServerBinding]): Unit = {
    sys.ShutdownHookThread {
      log.info("Starting shutdown hook for HTTP server")
      bindingFuture
        .flatMap(_.unbind()) // trigger unbinding from the port
        .onComplete { _ => 
          system.terminate()
          log.info("HTTP server shut down successfully")
        }
    }
  }


  val route =
    path("pv") {
      get {
        parameters('uid, 'url) { (uid, url) =>
          writer ! PixelParams(uid, url)
          complete(HttpEntity(MediaTypes.`image/gif`, trackingGif))
        }
      }
    } ~
    path("live") {
      get {
        complete("OK")
      }
    }


  def main(args: Array[String]): Unit = {

    val port = 8080
    log.info(s"Starting HTTP server on port ${port}")
    val bindingFuture = Http().bindAndHandle(route, "0.0.0.0", port)

    shutdown(bindingFuture)
  }


}
