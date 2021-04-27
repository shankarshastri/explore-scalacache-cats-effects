package com

import cats.effect._
import cats.implicits._
import com.cache.DBCache
import com.db.DBOps
import scalacache.Mode

object MainIOCache extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {
    implicit val mode: Mode[IO] = scalacache.CatsEffect.modes.async // Async CatsEffects Mode
    val dbOp = new DBOps[IO]() // Database Layer
    val dbCache = new DBCache[IO](dbOp) // Cache Layer
    for {
      _ <- dbCache.load() // List Loaded
//      _ <- dbCache.load().attempt // This will lead to don't care failures.
      res <- (1 to 100).map(e => dbCache.get(e.toString)).toList.sequence // Already Loaded
      res1 <- dbCache.get("100") // Already Loaded
      res2 <- dbCache.get("200") // Loading On-Demand
      res3 <- dbCache.get("201") // Loading On-Demand
    } yield {
      println(res) // List Loaded
      println(res1) // Already Loaded
      println(res2) // Loading On-Demand
      println(res3)
      ExitCode.Success
    }
  }
}
