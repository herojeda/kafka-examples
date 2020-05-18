package com.hojeda.kafka.consumer.rx

import com.hojeda.kafka.consumer.rx.operator.ThrottlingFlowableOperator.Companion.allowRate
import com.hojeda.util.LoggerDelegate
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.vertx.reactivex.core.Vertx
import io.vertx.reactivex.kafka.client.consumer.KafkaConsumer
import io.vertx.reactivex.kafka.client.consumer.KafkaConsumerRecord
import org.apache.kafka.clients.consumer.ConsumerConfig.*
import org.apache.kafka.common.serialization.StringDeserializer
import java.util.concurrent.TimeUnit

class KafkaRxListener<K, V>(
    private val vertx: Vertx
) {

    private val logger by LoggerDelegate()

    fun listen(
        host: String,
        port: String,
        topic: String,
        rate: Long,
        interval: Long,
        handler: (KafkaConsumerRecord<K, V>) -> Completable
    ): Flowable<KafkaConsumerRecord<K, V>> =
        createConsumer(host, port, topic).let { consumer ->
            consumer
                .subscribe(topic) { result ->
                    if (result.succeeded()) {
                        logger.info("KAFKA_SUCCESSFULLY_SUBSCRIBE_TO ${topic}")
                    } else {
                        logger.error(
                            "KAFKA_SUBSCRIBE_ERROR ${topic} - ${result.cause().message}",
                            result.cause()
                        )
                    }
                }
                .toFlowable()
                .lift(
                    allowRate(
                        intervalUnit = TimeUnit.SECONDS,
                        rate = rate,
                        interval = interval
                    )
                )
                .flatMapMaybe {
                    handler(it)
                        .andThen(Maybe.just(it))
                        .onErrorResumeNext(Maybe.empty())
                }
                .doAfterNext { consumer.commit() }
        }

    private fun createConsumer(
        host: String,
        port: String,
        topic: String
    ): KafkaConsumer<K, V> =
        KafkaConsumer.create(
            vertx,
            mapOf(
                BOOTSTRAP_SERVERS_CONFIG to "$host:$port",
                KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java.name,
                VALUE_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java.name,
                GROUP_ID_CONFIG to "consumer-${topic}-rx",
                AUTO_OFFSET_RESET_CONFIG to "latest",
                MAX_POLL_RECORDS_CONFIG to "1"
            )
        )

}
