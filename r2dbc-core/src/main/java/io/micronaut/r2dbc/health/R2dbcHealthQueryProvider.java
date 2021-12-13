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

import io.r2dbc.spi.ConnectionFactoryMetadata;
import jakarta.inject.Singleton;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Provides Health Query for selection version from database
 *
 * @author Anton Kurako (GoodforGod)
 * @since 2.0.1
 */
@Singleton
public class R2dbcHealthQueryProvider {

    private static final String COMMON_QUERY = "SELECT version();";

    private static final String POSTGRES = "PostgreSQL";
    private static final String MARIADB = "MariaDB";
    private static final String MYSQL = "MySQL";

    /**
     * {@link ConnectionFactoryMetadata#getName()} to SQL query for database version
     */
    private final Map<String, String> metadataNameToQuery;

    public R2dbcHealthQueryProvider() {
        this.metadataNameToQuery = new HashMap<>(12);
        this.metadataNameToQuery.put(POSTGRES, COMMON_QUERY);
        this.metadataNameToQuery.put(MARIADB, COMMON_QUERY);
        this.metadataNameToQuery.put(MYSQL, COMMON_QUERY);
    }

    /**
     * @param metadataName name of r2dbc driver from metadata {@link ConnectionFactoryMetadata#getName()} ()}
     * @return SQL query to return version for specified database
     * @see #COMMON_QUERY
     */
    public Optional<String> getVersionQuery(String metadataName) {
        return Optional.ofNullable(metadataNameToQuery.get(metadataName));
    }
}
