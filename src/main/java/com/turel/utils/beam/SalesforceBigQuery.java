package com.turel.utils.beam;

import com.google.api.services.bigquery.model.TableRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by Chaim on 28/02/2017.
 */
public class SalesforceBigQuery {
    private static final Logger LOG = LoggerFactory.getLogger(SalesforceBigQuery.class);

    public static final String Id = "Id";
    public static final String LastModifiedDate = "LastModifiedDate";
    public static final String SystemModstamp = "SystemModstamp";
    public static final String EndSystemModstamp = "EndSystemModstamp";
    public static final String fromDate = "fromDate";
    public static final String toDate = "toDate";
    public static final String ModifiedField = SystemModstamp;
    public static final String CreatedDate = "CreatedDate";
    public static final String IsDeleted = "IsDeleted";
    public static final String SFTesting = "Testing__c";

    public static final int MODIFIED_FIELD_POS = 0;
    public static final int CREATED_FIELD_POS = 1;

    static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss zzz");

    static public LocalDateTime getDateTimeFromString(String dateTimeField){
        return LocalDateTime.parse(dateTimeField, formatter);
    }

    static public LocalDateTime getDateFromRow(TableRow row, String fieldName){
        return getDateTimeFromString((String) row.get(fieldName));
    }



}
