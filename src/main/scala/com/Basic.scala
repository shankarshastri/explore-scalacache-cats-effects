package com

import cats.effect.{ExitCode, IO, IOApp}
import scalacache.caffeine.CaffeineCache
import scalacache.{Cache, Mode}

object Basic extends IOApp {
  def getFromDB(k: String): IO[String] = {
    IO.pure {
      println("Computing")
      k
    }
  }

  override def run(args: List[String]): IO[ExitCode] = {
    implicit val mode: Mode[IO] = scalacache.CatsEffect.modes.async
    implicit val caffeineCache: Cache[String] = CaffeineCache[String]
    val k = "Shankar"
    val sK = "Shankar1"
    val r = for {
      value1 <- caffeineCache.cachingF(k)(ttl = None)(getFromDB(k))
      _ <- caffeineCache.remove(k)
      value2 <- caffeineCache.cachingF(k)(ttl = None)(getFromDB(k))
    } yield (value1, value2)
    r.as(ExitCode.Success)
  }

}
