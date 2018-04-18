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

package org.apache.skywalking.apm.collector.analysis.segment.parser.provider;

import org.apache.skywalking.apm.collector.analysis.register.define.AnalysisRegisterModule;
import org.apache.skywalking.apm.collector.analysis.segment.parser.define.AnalysisSegmentParserModule;
import org.apache.skywalking.apm.collector.analysis.segment.parser.define.service.ISegmentParseService;
import org.apache.skywalking.apm.collector.analysis.segment.parser.define.service.ISegmentParserListenerRegister;
import org.apache.skywalking.apm.collector.analysis.segment.parser.provider.buffer.BufferFileConfig;
import org.apache.skywalking.apm.collector.analysis.segment.parser.provider.buffer.SegmentBufferReader;
import org.apache.skywalking.apm.collector.analysis.segment.parser.provider.parser.SegmentParserListenerManager;
import org.apache.skywalking.apm.collector.analysis.segment.parser.provider.parser.SegmentPersistenceGraph;
import org.apache.skywalking.apm.collector.analysis.segment.parser.provider.parser.standardization.SegmentStandardizationGraph;
import org.apache.skywalking.apm.collector.analysis.segment.parser.provider.service.SegmentParseService;
import org.apache.skywalking.apm.collector.analysis.segment.parser.provider.service.SegmentParserListenerRegister;
import org.apache.skywalking.apm.collector.analysis.worker.model.base.WorkerCreateListener;
import org.apache.skywalking.apm.collector.analysis.worker.timer.PersistenceTimer;
import org.apache.skywalking.apm.collector.cache.CacheModule;
import org.apache.skywalking.apm.collector.core.module.Module;
import org.apache.skywalking.apm.collector.core.module.ModuleProvider;
import org.apache.skywalking.apm.collector.core.module.ServiceNotProvidedException;
import org.apache.skywalking.apm.collector.storage.StorageModule;

import java.util.Properties;

/**
 * @author peng-yongsheng
 * seqment parser
 */
public class AnalysisSegmentParserModuleProvider extends ModuleProvider {

    public static final String NAME = "default";
    private SegmentParserListenerManager listenerManager;

    @Override public String name() {
        return NAME;
    }

    @Override public Class<? extends Module> module() {
        return AnalysisSegmentParserModule.class;
    }

    @Override public void prepare(Properties config) throws ServiceNotProvidedException {
        //初始化seqment parser listener管理器
        this.listenerManager = new SegmentParserListenerManager();
        //注册seqment parser 监听器
        this.registerServiceImplementation(ISegmentParserListenerRegister.class, new SegmentParserListenerRegister(listenerManager));
        this.registerServiceImplementation(ISegmentParseService.class, new SegmentParseService(getManager(), listenerManager));

        BufferFileConfig.Parser parser = new BufferFileConfig.Parser();
        parser.parse(config);
    }

    @Override public void start(Properties config) throws ServiceNotProvidedException {
        WorkerCreateListener workerCreateListener = new WorkerCreateListener();

        graphCreate(workerCreateListener);

        PersistenceTimer persistenceTimer = new PersistenceTimer(AnalysisSegmentParserModule.NAME);
        persistenceTimer.start(getManager(), workerCreateListener.getPersistenceWorkers());

        SegmentBufferReader.INSTANCE.setSegmentParserListenerManager(listenerManager);
    }

    @Override public void notifyAfterCompleted() throws ServiceNotProvidedException {

    }

    @Override public String[] requiredModules() {
        return new String[] {AnalysisRegisterModule.NAME, CacheModule.NAME, StorageModule.NAME};
    }

    private void graphCreate(WorkerCreateListener workerCreateListener) {
        SegmentPersistenceGraph segmentPersistenceGraph = new SegmentPersistenceGraph(getManager(), workerCreateListener);
        segmentPersistenceGraph.create();

        SegmentStandardizationGraph segmentStandardizationGraph = new SegmentStandardizationGraph(getManager(), workerCreateListener);
        segmentStandardizationGraph.create();
    }
}
