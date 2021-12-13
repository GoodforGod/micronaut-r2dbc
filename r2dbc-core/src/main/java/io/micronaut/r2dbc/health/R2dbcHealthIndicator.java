/*
 * Copyright 2017-2021 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micronaut.r2dbc.health;

import io.micronaut.context.annotation.Requires;
import io.micronaut.context.exceptions.ConfigurationException;
import io.micronaut.core.util.StringUtils;
import io.micronaut.health.HealthStatus;
import io.micronaut.management.endpoint.health.HealthEndpoint;
import io.micronaut.management.health.indicator.HealthIndicator;
import io.micronaut.management.health.indicator.HealthResult;
import io.r2dbc.spi.ConnectionFactory;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;

/**
 * Supports ony Postgres, MariaDB, MySQL
 *
 * @author Anton Kurako (GoodforGod)
 * @since 2.0.1
 */
@Requires(classes = HealthIndicator.class)
@Requires(beans = ConnectionFactory.class)
@Requires(condition = R2dbcHealthCondition.class)
@Requires(property = HealthEndpoint.PREFIX + ".r2dbc.enabled", value = StringUtils.TRUE, defaultValue = StringUtils.TRUE)
@Singleton
public class R2dbcHealthIndicator implements HealthIndicator {

    private static final String NAME = "r2dbc-connection-factory";

    private final ConnectionFactory connectionFactory;
    private final String versionQuery;

    @Inject
    public R2dbcHealthIndicator(ConnectionFactory connectionFactory,
                                R2dbcHealthQueryProvider queryProvider) {
        this.connectionFactory = connectionFactory;
        this.versionQuery = queryProvider.getVersionQuery(connectionFactory.getMetadata().getName())
                .orElseThrow(() -> new ConfigurationException("Unexpected behavior while getting Health Query for: " + connectionFactory));
    }

    @Override
    public Publisher<HealthResult> getResult() {
        return Flux.from(connectionFactory.create())
                .flatMap(connection -> Flux.from(connection.createStatement(versionQuery).execute())
                        .flatMap(r -> r.map((row, meta) -> String.valueOf(row.get(0))))
                        .doAfterTerminate(connection::close))
                .map(this::buildUpResult)
                .onErrorResume(e -> Mono.just(buildDownResult(e)));
    }

    private HealthResult buildUpResult(String version) {
        return HealthResult.builder(NAME)
                .status(HealthStatus.UP)
                .details(Collections.singletonMap("version", version))
                .build();
    }

    private HealthResult buildDownResult(Throwable throwable) {
        return HealthResult.builder(NAME)
                .status(HealthStatus.DOWN)
                .exception(throwable)
                .build();
    }
}
