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
 */
package com.hxg.sofa.jraft.core;

import java.nio.ByteBuffer;

import com.hxg.sofa.jraft.Closure;
import com.hxg.sofa.jraft.Iterator;
import com.hxg.sofa.jraft.Status;
import com.hxg.sofa.jraft.entity.EnumOutter;
import com.hxg.sofa.jraft.entity.LogEntry;

public class IteratorWrapper implements Iterator {

    private final IteratorImpl impl;

    public IteratorWrapper(IteratorImpl iterImpl) {
        super();
        this.impl = iterImpl;
    }

    @Override
    public boolean hasNext() {
        return this.impl.isGood() && this.impl.entry().getType() == EnumOutter.EntryType.ENTRY_TYPE_DATA;
    }

    @Override
    public ByteBuffer next() {
        final ByteBuffer data = getData();
        if (hasNext()) {
            this.impl.next();
        }
        return data;
    }

    @Override
    public ByteBuffer getData() {
        final LogEntry entry = this.impl.entry();
        return entry != null ? entry.getData() : null;
    }

    @Override
    public long getIndex() {
        return this.impl.getIndex();
    }

    @Override
    public long getTerm() {
        return this.impl.entry().getId().getTerm();
    }

    @Override
    public Closure done() {
        return this.impl.done();
    }

    @Override
    public void setErrorAndRollback(final long ntail, final Status st) {
        this.impl.setErrorAndRollback(ntail, st);
    }
}