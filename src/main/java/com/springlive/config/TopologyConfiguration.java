package com.springlive.config;

import java.util.function.Consumer;

import com.pivotal.rabbitmq.RabbitEndpointService;
import com.pivotal.rabbitmq.topology.TopologyBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TopologyConfiguration {

	@Autowired
	RabbitEndpointService rabbit;

	static Logger log = LoggerFactory.getLogger(TopologyConfiguration.class.getName());

	public static final String NUMBERS = "MyNumber";
	public static final String MULTIPLIED_NUMBERS = "MultipliedNumber";

	@Bean
	public Consumer<TopologyBuilder> springLiveTopology() {
		return (builder) -> {
			builder
				.declareExchange(NUMBERS)
				.and()
				.declareQueue(NUMBERS)
					.boundTo(NUMBERS)
				.and()
				.declareExchange(MULTIPLIED_NUMBERS)
				.and()
				.declareQueue(MULTIPLIED_NUMBERS)
					.boundTo(MULTIPLIED_NUMBERS);
		};
	}
}
