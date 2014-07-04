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

package com.hazelcast.concurrent.lock;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.BackupOperation;
import com.hazelcast.spi.ObjectNamespace;

import java.io.IOException;

public class LockBackupOperation extends BaseLockOperation implements BackupOperation {

    private String originalCallerUuid;

    public LockBackupOperation() {
    }

    public LockBackupOperation(ObjectNamespace namespace, Data key, int threadId, String originalCallerUuid) {
        super(namespace, key, threadId);
        this.originalCallerUuid = originalCallerUuid;
    }

    public void run() throws Exception {
        response = getLockStore().lock(key, originalCallerUuid, threadId, ttl);
    }

    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeUTF(originalCallerUuid);
    }

    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        originalCallerUuid = in.readUTF();
    }
}
