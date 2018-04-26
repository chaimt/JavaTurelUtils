/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.turel.utils.beam.encoders;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.api.services.bigquery.model.TableFieldSchema;
import org.apache.beam.sdk.coders.AtomicCoder;
import org.apache.beam.sdk.coders.Coder;
import org.apache.beam.sdk.coders.StringUtf8Coder;
import org.apache.beam.sdk.values.TypeDescriptor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * A {@link Coder} that encodes BigQuery {@link TableFieldSchema} objects in their native JSON format.
 * based on TableRowJsonCoder
 */
public class TableFieldSchemaJsonCoder extends AtomicCoder<TableFieldSchema> {

  public static TableFieldSchemaJsonCoder of() {
    return INSTANCE;
  }

  @Override
  public void encode(TableFieldSchema value, OutputStream outStream) throws IOException {
    String strValue = MAPPER.writeValueAsString(value);
    StringUtf8Coder.of().encode(strValue, outStream);
  }

  private TableFieldSchema toTableFieldSchema(JsonNode jsonNode) throws IOException {
    final String s = jsonNode.toString();
    final TableFieldSchema tableFieldSchema1 = MAPPER.readValue(s, TableFieldSchema.class);
    final ArrayNode fields = (ArrayNode)jsonNode.get("fields");
    if (fields!=null) {
      tableFieldSchema1.setFields(new ArrayList<>());
      for (int i = 0; i < fields.size(); i++) {
        final JsonNode jsonNode1 = fields.get(i);
        tableFieldSchema1.getFields().add(toTableFieldSchema(jsonNode1));
      }
    }
    return tableFieldSchema1;
  }

  @Override
  public TableFieldSchema decode(InputStream inStream)
          throws IOException {
    String strValue = StringUtf8Coder.of().decode(inStream);
    return toTableFieldSchema(MAPPER.readTree(strValue));
  }

  @Override
  protected long getEncodedElementByteSize(TableFieldSchema value)
          throws Exception {
    String strValue = MAPPER.writeValueAsString(value);
    return StringUtf8Coder.of().getEncodedElementByteSize(strValue);
  }

  /////////////////////////////////////////////////////////////////////////////

  // FAIL_ON_EMPTY_BEANS is disabled in order to handle null values in
  // TableFieldSchema.
  private static final ObjectMapper MAPPER =
          new ObjectMapper().disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
  static {
    MAPPER.registerSubtypes(TableFieldSchema.class);
  }

  private static final TableFieldSchemaJsonCoder INSTANCE = new TableFieldSchemaJsonCoder();
  private static final TypeDescriptor<TableFieldSchema> TYPE_DESCRIPTOR = new TypeDescriptor<TableFieldSchema>() {};

  private TableFieldSchemaJsonCoder() { }



  /**
   * {@inheritDoc}
   *
   * @throws NonDeterministicException always. A {@link TableFieldSchema} can hold arbitrary
   *         {@link Object} instances, which makes the encoding non-deterministic.
   */
  @Override
  public void verifyDeterministic() throws NonDeterministicException {
    throw new NonDeterministicException(this,
            "TableCell can hold arbitrary instances, which may be non-deterministic.");
  }

  @Override
  public TypeDescriptor<TableFieldSchema> getEncodedTypeDescriptor() {
    return TYPE_DESCRIPTOR;
  }
}
