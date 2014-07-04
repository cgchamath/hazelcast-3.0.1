/*
 * Copyright (c) 2008-2013, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hazelcast.executor;

import com.hazelcast.core.ManagedContext;
import com.hazelcast.instance.HazelcastInstanceImpl;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.SerializationServiceImpl;
import com.hazelcast.spi.Operation;

import java.io.IOException;
import java.util.concurrent.Callable;

/**
 * @author mdogan 1/18/13
 */
abstract class BaseCallableTaskOperation extends Operation {

    protected String name;
    protected String uuid;
    protected Callable callable;

    public BaseCallableTaskOperation() {
    }

    public BaseCallableTaskOperation(String name, String uuid, Callable callable) {
        this.name = name;
        this.uuid = uuid;
        this.callable = callable;
    }

    @Override
    public final void beforeRun() throws Exception {
        HazelcastInstanceImpl hazelcastInstance = (HazelcastInstanceImpl) getNodeEngine().getHazelcastInstance();
        SerializationServiceImpl serializationService = (SerializationServiceImpl) hazelcastInstance.getSerializationService();
        ManagedContext managedContext = serializationService.getManagedContext();

        if (callable instanceof RunnableAdapter) {
            RunnableAdapter adapter = (RunnableAdapter) callable;
            adapter.setRunnable((Runnable) managedContext.initialize(adapter.getRunnable()));
        } else {
            callable = (Callable) managedContext.initialize(callable);
        }
    }

    public final void run() throws Exception {
        DistributedExecutorService service = getService();
        service.execute(name, uuid, callable, getResponseHandler());
    }

    @Override
    public final void afterRun() throws Exception {
    }

    @Override
    public final boolean returnsResponse() {
        return false;
    }

    @Override
    public final Object getResponse() {
        return null;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        out.writeUTF(name);
        out.writeUTF(uuid);
        out.writeObject(callable);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        name = in.readUTF();
        uuid = in.readUTF();
        callable = in.readObject();
    }
}
