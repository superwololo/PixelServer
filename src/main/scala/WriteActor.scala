package com.freemonetize.pixelserver


import akka.actor.Actor
import java.lang.{System, StringBuilder}
import java.nio.file.Files
import java.io.File
import ujson.Js
import java.util.UUID


case class PixelParams(uid: String, url: String)


class WriteActor extends Actor {


  val maxSize = 50 * 1024 * 1024 //50 MB
  val flushFreq = 10 // ???
  val stringBuilder = new StringBuilder()
  val actorIdentifier = UUID.randomUUID().toString.take(8)
  var writerSize = 0
  var fileIndex = 0


  def writeFile(compressed: Array[Byte]): Unit = {
    Files.write(new File(s"test-${actorIdentifier}-${fileIndex}.gz").toPath(), compressed);
  }

  def flush(): Unit = {
    val compressed = Gzip.compress(stringBuilder.toString.getBytes)
    stringBuilder.delete(0, writerSize)
    writerSize = 0
    writeFile(compressed)
    fileIndex = fileIndex + 1
  }

  def onMessageReceived(p : PixelParams): Unit = {
    val json = Js.Obj(
      "ts" -> System.currentTimeMillis(),
      "uid" -> p.uid.take(100),
      "url" -> p.url.take(100)
    ).toString
    stringBuilder.append(json)
    stringBuilder.append('\n')
    writerSize += json.length + 1
    if (writerSize >= maxSize) {
      flush() //We also need to flush if there has been no activity over a prolonged period of time
    }
  }

  def receive = {
    case p : PixelParams => onMessageReceived(p)
  }


}
