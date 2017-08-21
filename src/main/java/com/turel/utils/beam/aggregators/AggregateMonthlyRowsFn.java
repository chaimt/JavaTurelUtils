package com.turel.utils.beam.aggregators;

import com.google.api.services.bigquery.model.TableRow;
import com.turel.utils.beam.SalesforceBigQuery;
import org.apache.beam.sdk.transforms.Combine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Chaim on 07/03/2017.
 */
public class AggregateMonthlyRowsFn extends Combine.CombineFn<TableRow, AggregateMonthlyRowsFn.Accum, List<TableRow>> {
    private static final Logger LOG = LoggerFactory.getLogger(AggregateMonthlyRowsFn.class);

    public static class Accum implements Serializable {
        public Accum() {
        }

        public Accum(List<TableRow> rows) {
            this.rows = rows;
        }

        public List<TableRow> rows;
    }

    public Accum createAccumulator() {
        final Accum accum = new Accum();
        accum.rows = new ArrayList<>();
        return accum;
    }

    public Accum addInput(Accum accum, TableRow input) {
        final LocalDateTime fromDateTime = SalesforceBigQuery.getDateFromRow(input, SalesforceBigQuery.SystemModstamp);

        final List<TableRow> newListOfRows = accum.rows.stream().map(tableRow -> {
            final LocalDateTime checkFromDateTime = SalesforceBigQuery.getDateFromRow(tableRow, SalesforceBigQuery.SystemModstamp);
            final boolean replace = fromDateTime.getMonth() == checkFromDateTime.getMonth() && fromDateTime.isAfter(checkFromDateTime);
            return replace ? input : tableRow;
        }).collect(Collectors.toList());

        accum.rows = newListOfRows;

        final long count = accum.rows.stream().filter(tableRow -> {
            final LocalDateTime checkFromDateTime = SalesforceBigQuery.getDateFromRow(tableRow, SalesforceBigQuery.SystemModstamp);
            return fromDateTime.getMonth() == checkFromDateTime.getMonth();// && checkFromDateTime.isAfter(fromDateTime);
        }).count();

        if (count == 0) {
            accum.rows.add(input);
        }

        return accum;
    }

    public Accum mergeAccumulators(Iterable<Accum> accums) {
        Accum result = createAccumulator();
        final Accum merged = createAccumulator();
        for (Accum accum : accums) {
            for (TableRow row : accum.rows) {
                result = addInput(merged, row);
            }
        }
        return result;
    }

    public List<TableRow> extractOutput(Accum accum) {
        return accum.rows;
    }
}

