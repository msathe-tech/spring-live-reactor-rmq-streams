package com.springlive;

import java.time.Duration;
import java.util.function.Consumer;

import com.pivotal.rabbitmq.RabbitEndpointService;
import com.pivotal.rabbitmq.stream.Transaction;
import com.pivotal.rabbitmq.stream.TransactionalConsumerStream;
import com.pivotal.rabbitmq.stream.TransactionalProducerStream;
import com.pivotal.rabbitmq.topology.TopologyBuilder;

import com.springlive.schemas.MultipliedNumber;
import com.springlive.schemas.MyNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ReliableMultiplicationStream {

	@Autowired
	RabbitEndpointService rabbit;

	@Autowired
	@Qualifier("springLiveTopology")
	Consumer<TopologyBuilder> springLiveTopology;

	static Logger log = LoggerFactory.getLogger(ReliableMultiplicationStream.class.getName());

	@Bean
	@ConditionalOnProperty(name = "role", havingValue = "produceNumbers", matchIfMissing = false)
	public CommandLineRunner produceNumbers() {
		return (args ) -> {
			rabbit
				.declareTopology(springLiveTopology)
				.createProducerStream(MyNumber.class)
				.route()
					.toExchange(Topologies.NUMBERS)
					.and()
					.whenNackByBroker().alwaysRetry(Duration.ofSeconds(2))
					.and()
					.whenUnroutable().alwaysRetry(Duration.ofSeconds(2))
					.then()
				.send(getStreamOfMyNumbers())
				.doOnNext(number -> log.info("Sent: {}", number))
				.blockLast();
		};
	}

	Flux<MyNumber> getStreamOfMyNumbers() {
		Flux<Integer> streamOfNumbersToSend = Flux
			.range(1, 100);

		MyNumber.Builder builder = MyNumber.newBuilder();

		Flux<MyNumber> numbersToSend = streamOfNumbersToSend
			.map(i -> builder.setNumber(i).build());

		return numbersToSend;
	}

	@Bean
	@ConditionalOnProperty(name = "role", havingValue = "multiplier", matchIfMissing = false)
	public CommandLineRunner multiplier() {
		return (args) -> {
			// Receive transactional stream of MyNumber
			TransactionalConsumerStream<MyNumber> myNumbersStream = rabbit
				.declareTopology(springLiveTopology)
				.createTransactionalConsumerStream(Topologies.NUMBERS, MyNumber.class)
				.withPrefetch(10)
				.ackEvery(5, Duration.ofSeconds(5));

			Flux<Transaction<MyNumber>> fluxOfReceivedMyNumber = myNumbersStream
				.receive()
				.doOnNext(txMyNumber -> log.info("Received {}", txMyNumber.get().getNumber()));

			// Transform the data
			MultipliedNumber.Builder builder = MultipliedNumber.newBuilder();

			Flux<Transaction<MultipliedNumber>> fluxOfMultipliedNumber = fluxOfReceivedMyNumber
				.map(txMyNumber -> txMyNumber.map(builder.setNumber(txMyNumber.get().getNumber() * 2).build()));

			// Send transformed data 
			TransactionalProducerStream<MultipliedNumber> streamOfMultipliedNumbersToSend = rabbit
				.declareTopology(springLiveTopology)
				.createTransactionalProducerStream(MultipliedNumber.class)
				.route()
					.toExchange(Topologies.MULTIPLIED_NUMBERS)
				.then();

			Flux<Transaction<MultipliedNumber>> streamOfSentMultipliedNumbers = streamOfMultipliedNumbersToSend
				.send(fluxOfMultipliedNumber)
				.doOnNext(txMultipliedNumber -> log.info("Sent multiplied number {}", txMultipliedNumber.get().getNumber()))
				.delayElements(Duration.ofSeconds(1));

			streamOfSentMultipliedNumbers
				.subscribe(Transaction::commit);
		};
	}
}
