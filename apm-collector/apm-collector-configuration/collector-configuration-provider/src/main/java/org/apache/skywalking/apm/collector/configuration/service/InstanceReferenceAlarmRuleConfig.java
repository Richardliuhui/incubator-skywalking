/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.skywalking.apm.collector.configuration.service;

/**
 * @author peng-yongsheng
 */
public class InstanceReferenceAlarmRuleConfig implements IInstanceReferenceAlarmRuleConfig {

    private double errorRateThreshold;
    private int averageResponseTimeThreshold;

    public InstanceReferenceAlarmRuleConfig(double errorRateThreshold, int averageResponseTimeThreshold) {
        this.errorRateThreshold = errorRateThreshold;
        this.averageResponseTimeThreshold = averageResponseTimeThreshold;
    }

    @Override public double calleeErrorRateThreshold() {
        return errorRateThreshold;
    }

    @Override public double calleeAverageResponseTimeThreshold() {
        return averageResponseTimeThreshold;
    }

    @Override public double callerErrorRateThreshold() {
        return errorRateThreshold;
    }

    @Override public double callerAverageResponseTimeThreshold() {
        return averageResponseTimeThreshold;
    }
}
