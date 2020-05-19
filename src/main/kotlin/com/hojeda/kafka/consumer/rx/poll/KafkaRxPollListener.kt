package com.hojeda.kafka.consumer.rx.poll

import com.hojeda.util.LoggerDelegate
import io.reactivex.*
import io.vertx.reactivex.core.Vertx
import io.vertx.reactivex.kafka.client.consumer.KafkaConsumer
import io.vertx.reactivex.kafka.client.consumer.KafkaConsumerRecord
import org.apache.kafka.clients.consumer.ConsumerConfig.*
import org.apache.kafka.common.serialization.StringDeserializer
import java.util.concurrent.TimeUnit

class KafkaRxPollListener<K, V>(
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
        createConsumer(host, port, topic, rate.toString()).let { consumer ->

            Flowable.interval(interval, TimeUnit.SECONDS).flatMap {
                consumer
                    .subscribe(topic) { result ->
                        if (result.succeeded()) {
                            logger.info("KAFKA_SUCCESSFULLY_SUBSCRIBE_TO $topic")
                        } else {
                            logger.error(
                                "KAFKA_SUBSCRIBE_ERROR $topic - ${result.cause().message}",
                                result.cause()
                            )
                        }
                    }
                    .rxPoll(TimeUnit.SECONDS.toMillis(interval))
                    .flatMapMaybe { records ->
                        if (records.isEmpty) Maybe.empty() else Maybe.just(records)
                    }
                    .flatMapObservable { records ->
                        Observable.fromIterable(0 until records.size())
                            .flatMapMaybe { index ->
                                val record = records.recordAt(index)
                                handler(record)
                                    .andThen(Maybe.just(record))
                                    .onErrorResumeNext(Maybe.empty())
                            }
                    }
                    .doAfterNext { consumer.commit() }
                    .toFlowable(BackpressureStrategy.BUFFER)
            }
        }

    private fun createConsumer(
        host: String,
        port: String,
        topic: String,
        pollRate: String
    ): KafkaConsumer<K, V> =
        KafkaConsumer.create(
            vertx,
            mapOf(
                BOOTSTRAP_SERVERS_CONFIG to "$host:$port",
                KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java.name,
                VALUE_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java.name,
                GROUP_ID_CONFIG to "consumer-${topic}-rx-poll",
                AUTO_OFFSET_RESET_CONFIG to "latest",
                MAX_POLL_RECORDS_CONFIG to pollRate,
                MAX_POLL_INTERVAL_MS_CONFIG to "500"
            )
        )

}
