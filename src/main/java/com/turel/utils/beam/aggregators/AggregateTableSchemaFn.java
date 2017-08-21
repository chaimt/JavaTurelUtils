package com.turel.utils.beam.aggregators;

import com.google.api.services.bigquery.model.TableFieldSchema;
import org.apache.beam.sdk.transforms.Combine;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chaim on 07/03/2017.
 */
public class AggregateTableSchemaFn extends Combine.CombineFn<List<TableFieldSchema>, AggregateTableSchemaFn.Accum, List<TableFieldSchema>> {
    public static class Accum implements Serializable {
        public Accum(){}

        public Accum(List<TableFieldSchema> tableFieldSchemas) {
            this.tableFieldSchemas = tableFieldSchemas;
        }

        public List<TableFieldSchema> tableFieldSchemas;
    }

    public AggregateTableSchemaFn.Accum createAccumulator() {
        final AggregateTableSchemaFn.Accum accum = new AggregateTableSchemaFn.Accum();
        accum.tableFieldSchemas = new ArrayList<>();
        return accum;
    }

    public Accum addInput(Accum accum, List<TableFieldSchema> input) {
//        accum.tableFieldSchemas = MongodbManagment.mergeSchmea(accum.tableFieldSchemas, input).stream().map(field -> (new TableFieldSchema()).setName(field.getName()).setType(field.getType()).setFields(field.getFields())).collect(Collectors.toList());
        return accum;
    }

    public Accum mergeAccumulators(Iterable<Accum> accums) {
        Accum merged = createAccumulator();
        List<TableFieldSchema> tableFieldSchemas = merged.tableFieldSchemas;
        for (Accum accum : accums) {
//            tableFieldSchemas = MongodbManagment.mergeSchmea(tableFieldSchemas,accum.tableFieldSchemas);
        }
        return merged;
    }

    public List<TableFieldSchema> extractOutput(Accum accum) {
        return accum.tableFieldSchemas;
    }
}

