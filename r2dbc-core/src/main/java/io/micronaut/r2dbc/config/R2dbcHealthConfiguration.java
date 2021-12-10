/*
 * Copyright 2017-2020 original authors
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
package io.micronaut.r2dbc.config;

import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.management.endpoint.health.HealthEndpoint;

/**
 * Configuration for R2DBC Health Indicator.
 *
 * @author Anton Kurako (GoodforGod)
 * @since 2.0.1
 */
@ConfigurationProperties(HealthEndpoint.PREFIX + ".r2dbc")
public class R2dbcHealthConfiguration {

    private boolean enabled = true;

    /**
     * @return true if health indicator is enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * @param enabled value to set for autoconfiguration
     */
    void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
