package com.cache

trait BaseCache[F[_], K, V] {
  protected def cache: scalacache.Cache[V]
  def invalidate(): F[Unit]
  def get(k: K): F[V]
  def removeK(k: K): F[Unit]
  def load(): F[Unit]
}