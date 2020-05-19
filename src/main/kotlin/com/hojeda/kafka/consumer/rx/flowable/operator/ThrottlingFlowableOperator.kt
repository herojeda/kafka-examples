package com.hojeda.kafka.consumer.rx.flowable.operator

import io.reactivex.Flowable
import io.reactivex.FlowableOperator
import io.reactivex.FlowableSubscriber
import io.reactivex.internal.subscriptions.EmptySubscription
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription
import java.util.concurrent.TimeUnit

class ThrottlingFlowableOperator<T> private constructor(
    val rate: Long,
    val interval: Long,
    val intervalUnit: TimeUnit
) : FlowableOperator<T, T> {

    companion object {

        fun <T> allowRate(rate: Long, interval: Long, intervalUnit: TimeUnit): ThrottlingFlowableOperator<T> =
            ThrottlingFlowableOperator(
                rate,
                interval,
                intervalUnit
            )

    }

    override fun apply(subscriber: Subscriber<in T>): Subscriber<in T> {
        return object : FlowableSubscriber<T> {
            override fun onComplete() {
                subscriber.onComplete()
            }

            override fun onSubscribe(s: Subscription) {
                s.request(rate)
                Flowable.interval(interval, intervalUnit).subscribe { s.request(rate) }
                subscriber.onSubscribe(EmptySubscription.INSTANCE)
            }

            override fun onNext(t: T) {
                subscriber.onNext(t)
            }

            override fun onError(t: Throwable?) {
                subscriber.onError(t)
            }
        }
    }

}