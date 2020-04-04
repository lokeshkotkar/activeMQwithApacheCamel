package com.example.demo;

import org.apache.activemq.jms.pool.PooledConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.LoggingLevel;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.amqp.AMQPComponent;
import org.apache.qpid.jms.JmsConnectionFactory;
import org.apache.qpid.jms.policy.JmsDefaultPrefetchPolicy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ActiveMqRoute extends RouteBuilder  {
	
	@Value("${directoryUrl}")
	private String directoryUrl;

	@Override
	@Bean
	public void configure() throws Exception {
		System.out.println("inside congig method");
//		from("timer://test?period=5000")
	from(directoryUrl + "?charset=utf-8").convertBodyTo(String.class)
	.to("amqp:queue.testing").log("testing").end();
	
	from("amqp:queue.testing")
	.log(LoggingLevel.INFO, "received message")
	.to("amqp:queue.outbound").end();

	}
	
	
//	@Bean
//	public RoutesBuilder messageProcessorRoute() {
//		return new RoutesBuilder() {
//
//			@Override
//			public void addRoutesToCamelContext(CamelContext context) throws Exception {
//				context.addRoutes(new RouteBuilder() {
//					@Override
//					public void configure() throws Exception {
//						System.out.println("inside configuration");
//
//						from(directoryUrl + "?charset=utf-8").convertBodyTo(String.class)
//
//						.to("amqp:queue.testing").log("testing").end();
//
//					}
//				});
//
//			}
//		};
//	}

	@Bean(name = "amqp-component")
	public AMQPComponent amqpComponent(AMQPConfiguration config) {

		String remoteURI = String.format("amqp://%s:%s?%s", config.getServiceName(), config.getServicePort(),
				config.getParameters());

		JmsConnectionFactory qpid = new JmsConnectionFactory(config.getUserName(), config.getPassword(), remoteURI);

		JmsDefaultPrefetchPolicy prefetchPolicy = new JmsDefaultPrefetchPolicy();

		prefetchPolicy.setAll(10);
		qpid.setPrefetchPolicy(prefetchPolicy);

		PooledConnectionFactory factory = new PooledConnectionFactory();
		factory.setConnectionFactory(qpid);

		return new AMQPComponent(factory);
	}

}
