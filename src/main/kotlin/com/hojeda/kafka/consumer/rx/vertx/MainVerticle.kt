package com.hojeda.kafka.consumer.rx.vertx

import com.hojeda.util.LoggerDelegate
import io.reactivex.Completable
import io.vertx.core.Future
import io.vertx.reactivex.CompletableHelper
import io.vertx.reactivex.core.AbstractVerticle

class MainVerticle : AbstractVerticle() {

    private val logger by LoggerDelegate()

    override fun start(startFuture: Future<Void>) {
        startServer().subscribe(CompletableHelper.toObserver(startFuture))
    }

    private fun startServer(): Completable {
        return Completable.complete()
    }

}
