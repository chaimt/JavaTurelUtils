package com.turel.utils.beam.encoders;

import com.google.api.services.bigquery.model.TableRow;
import com.turel.utils.beam.aggregators.AggregateMonthlyRowsFn;
import org.apache.beam.sdk.coders.CustomCoder;
import org.apache.beam.sdk.coders.ListCoder;
import org.apache.beam.sdk.io.gcp.bigquery.TableRowJsonCoder;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class AccumTableRowCoder extends CustomCoder<AggregateMonthlyRowsFn.Accum> {

    private static final ListCoder<TableRow> tableRowListCoder = ListCoder.of(TableRowJsonCoder.of());

    @Override
    public void encode(AggregateMonthlyRowsFn.Accum value, OutputStream outStream) throws IOException {
        tableRowListCoder.encode(value.rows, outStream);
    }

    @Override
    public AggregateMonthlyRowsFn.Accum decode(InputStream inStream) throws IOException {
        return new AggregateMonthlyRowsFn.Accum(tableRowListCoder.decode(inStream));
    }

}
