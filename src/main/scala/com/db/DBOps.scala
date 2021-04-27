package com.db

import com.model.ModelClasses.SimpleValue

// DB Layer
class DBOps[F[_] : cats.effect.Sync] {
  def fetchFromDB(k: String): F[SimpleValue] = {
    cats.effect.Sync[F].pure {
      println(s"Fetching data for key $k")
      SimpleValue(k, k)
    }
  }
  def fetchAllFromDB(): F[List[SimpleValue]] = {
    cats.effect.Sync[F].pure {
      println("Loading From DB, bunch of items")
      (1 to 100).map(e => SimpleValue(e.toString, e.toString)).toList
    }
  }
}