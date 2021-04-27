package com.cache

import cats.Parallel
import cats.syntax.all._
import com.db.DBOps
import com.model.ModelClasses.SimpleValue
import scalacache.Mode
import scalacache.caffeine.CaffeineCache

// DBCache Layer
// Read About Parallel
// TODO We can still make it generic by taking key and value as part of both DBOps and DBCache Layer
class DBCache[F[_]: cats.effect.Sync : Parallel](db: DBOps[F])(implicit val mode: Mode[F]) extends BaseCache[F, String, SimpleValue] {
  override val cache: scalacache.Cache[SimpleValue] = CaffeineCache[SimpleValue]

  override def get(k: String): F[SimpleValue] = {
    cache.cachingF(k)(None)(db.fetchFromDB(k))
  }

  override def invalidate(): F[Unit] = {
    cats.effect.Sync[F].map(cache.removeAll())(_ => ())
  }

  override def removeK(k: String): F[Unit] = {
    cats.effect.Sync[F].map(cache.remove(k))(_ => ())
  }

  override def load(): F[Unit] = {
    cats.effect.Sync[F].flatMap(db.fetchAllFromDB())(e =>  {
      e.map(e => (e.k, e)).map(k => cache.put(k._1)(k._2)).sequence.map(_ => ())
    })
  }
}