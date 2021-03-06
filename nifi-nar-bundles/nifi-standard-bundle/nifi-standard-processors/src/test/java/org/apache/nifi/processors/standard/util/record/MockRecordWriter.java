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

package org.apache.nifi.processors.standard.util.record;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;

import org.apache.nifi.controller.AbstractControllerService;
import org.apache.nifi.logging.ComponentLog;
import org.apache.nifi.serialization.RecordSetWriter;
import org.apache.nifi.serialization.RecordSetWriterFactory;
import org.apache.nifi.serialization.WriteResult;
import org.apache.nifi.serialization.record.Record;
import org.apache.nifi.serialization.record.RecordSet;

public class MockRecordWriter extends AbstractControllerService implements RecordSetWriterFactory {
    private final String header;

    public MockRecordWriter(final String header) {
        this.header = header;
    }

    @Override
    public RecordSetWriter createWriter(final ComponentLog logger) {
        return new RecordSetWriter() {
            @Override
            public WriteResult write(final RecordSet rs, final OutputStream out) throws IOException {
                out.write(header.getBytes());
                out.write("\n".getBytes());

                int recordCount = 0;
                final int numCols = rs.getSchema().getFieldCount();
                Record record = null;
                while ((record = rs.next()) != null) {
                    recordCount++;
                    int i = 0;
                    for (final String fieldName : record.getSchema().getFieldNames()) {
                        final String val = record.getAsString(fieldName);
                        out.write("\"".getBytes());
                        out.write(val.getBytes());
                        out.write("\"".getBytes());

                        if (i++ < numCols - 1) {
                            out.write(",".getBytes());
                        }
                    }
                    out.write("\n".getBytes());
                }

                return WriteResult.of(recordCount, Collections.emptyMap());
            }

            @Override
            public String getMimeType() {
                return "text/plain";
            }

            @Override
            public WriteResult write(Record record, OutputStream out) throws IOException {
                return null;
            }
        };
    }
}