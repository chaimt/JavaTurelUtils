package com.turel.utils.beam.encoders;

import com.google.api.services.bigquery.model.TableFieldSchema;
import com.turel.utils.beam.aggregators.AggregateTableSchemaFn;
import org.apache.beam.sdk.coders.CustomCoder;
import org.apache.beam.sdk.coders.ListCoder;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class AccumTableFieldSchemaCoder extends CustomCoder<AggregateTableSchemaFn.Accum> {

    private static final ListCoder<TableFieldSchema> tableRowListCoder = ListCoder.of(TableFieldSchemaJsonCoder.of());

    @Override
    public void encode(AggregateTableSchemaFn.Accum value, OutputStream outStream) throws IOException {
        tableRowListCoder.encode(value.tableFieldSchemas, outStream);
    }

    @Override
    public AggregateTableSchemaFn.Accum decode(InputStream inStream) throws IOException {
        return new AggregateTableSchemaFn.Accum(tableRowListCoder.decode(inStream));
    }

}
