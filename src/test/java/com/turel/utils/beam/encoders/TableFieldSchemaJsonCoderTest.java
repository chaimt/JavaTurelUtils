package com.turel.utils.beam.encoders;

import com.google.api.services.bigquery.model.TableFieldSchema;
import org.apache.beam.sdk.coders.Coder;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class TableFieldSchemaJsonCoderTest {

    @Test
    public void testSerialization() throws IOException {
        TableFieldSchemaJsonCoder tableFieldSchemaJsonCoder = TableFieldSchemaJsonCoder.of();
        TableFieldSchema tableFieldSchema = (new TableFieldSchema()).setName("test").setType("integer");
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        tableFieldSchemaJsonCoder.encode(tableFieldSchema,outputStream);


        InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        final TableFieldSchema decode = tableFieldSchemaJsonCoder.decode(inputStream, Coder.Context.NESTED);
        Assert.assertEquals(tableFieldSchema.getName(),decode.getName());
        Assert.assertEquals(tableFieldSchema.getType(),decode.getType());
    }

    @Test
    public void testNestedSerialization() throws IOException {
        TableFieldSchemaJsonCoder tableFieldSchemaJsonCoder = TableFieldSchemaJsonCoder.of();
        TableFieldSchema tableFieldSchema = (new TableFieldSchema()).setName("test").setType("integer");
        tableFieldSchema.setFields(new ArrayList<>());
        tableFieldSchema.getFields().add((new TableFieldSchema()).setName("inner").setType("integer"));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        tableFieldSchemaJsonCoder.encode(tableFieldSchema,outputStream);

        InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        final TableFieldSchema decode = tableFieldSchemaJsonCoder.decode(inputStream, Coder.Context.NESTED);
        Assert.assertEquals(tableFieldSchema.getName(),decode.getName());
        Assert.assertEquals(tableFieldSchema.getType(),decode.getType());
        Assert.assertEquals(1,tableFieldSchema.getFields().size());
        Assert.assertEquals(tableFieldSchema.getFields().get(0).getName(),decode.getFields().get(0).getName());
        Assert.assertEquals(tableFieldSchema.getFields().get(0).getType(),decode.getFields().get(0).getType());
    }
}
