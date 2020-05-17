package com.hojeda.kafka.consumer.rx

import com.hojeda.kafka.consumer.rx.vertx.MainVerticle
import io.reactivex.Completable
import io.vertx.core.logging.SLF4JLogDelegateFactory
import io.vertx.reactivex.core.Vertx
import org.slf4j.LoggerFactory

fun main() {

    val topic = "numbers-topic"
    val host = "localhost"
    val port = "9092"

    val logger = LoggerFactory.getLogger("REPORTING-API-APPLICATION")
    System.setProperty("vertx.logger-delegate-factory-class-name", SLF4JLogDelegateFactory::class.java.name)

    val vertx = Vertx.vertx()
    val verticle = MainVerticle()

    vertx.deployVerticle(verticle) { result ->
        if (result.succeeded()) {
            logger.info("Verticle started")

            KafkaRxListener<String, String>(vertx)
                .listen(host, port, topic, 10, 1) { record ->
                    println("Value: ${record.value()}")
                    Completable.complete()
                }
                .subscribe()

        } else {
            logger.error("Could not start application", result.cause())
        }
    }

    Runtime.getRuntime().addShutdownHook(Thread {
        logger.info("Closing application")
        vertx.close()
    })

}