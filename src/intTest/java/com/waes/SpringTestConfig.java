package com.waes;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.velocity.VelocityAutoConfiguration;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.TestPropertySource;

@Configuration
@EnableAutoConfiguration(exclude = {VelocityAutoConfiguration.class})
@EnableJpaRepositories
@EntityScan
@TestPropertySource("classpath:application-db.properties")
@ComponentScan(basePackages = {"com.waes"},
        excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                value = {DifferenceGeneratorApplication.class, DataSourceAutoConfiguration.class, DataSourceProperties.class})})
public class SpringTestConfig
{

}