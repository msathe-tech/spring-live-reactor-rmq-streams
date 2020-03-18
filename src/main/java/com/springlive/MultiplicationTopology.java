package com.springlive;

import java.util.function.Consumer;

import com.pivotal.rabbitmq.RabbitEndpointService;
import com.pivotal.rabbitmq.topology.Topology;
import com.pivotal.rabbitmq.topology.TopologyBuilder;
import com.springlive.config.TopologyConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MultiplicationTopology {
	@Autowired
	RabbitEndpointService rabbit;

	@Autowired
	@Qualifier("springLiveTopology")
	Consumer<TopologyBuilder> springLiveTopology;

	static Logger log = LoggerFactory.getLogger(MultiplicationTopology.class.getName());

	@Bean
	@ConditionalOnProperty(name = "role", havingValue = "setupTopology", matchIfMissing = false)
	public CommandLineRunner setupTopology() {
		return (args) -> {
			Topology topology = rabbit
				.manageTopologies()
				.declare(springLiveTopology)
				.block();

			log.info("Topology setup successfully {}", topology);
		};
	}
}
