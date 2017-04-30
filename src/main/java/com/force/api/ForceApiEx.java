package com.force.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.force.api.http.Http;
import com.force.api.http.HttpRequest;
import com.force.api.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by Chaim on 26/04/2017.
 * The ForceApi does not
 */
public class ForceApiEx extends ForceApi {
    private static final ObjectMapper jsonMapper;

    private static final Logger logger = LoggerFactory.getLogger(ForceApi.class);

    static {
        jsonMapper = new ObjectMapper();
        jsonMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    private boolean autoRenew = false;

    public ForceApiEx(ApiConfig config, ApiSession session) {
        super(config, session);
        if (session.getRefreshToken() != null) {
            autoRenew = true;
        }

    }

    public ForceApiEx(ApiSession session) {
        super(session);
    }

    public ForceApiEx(ApiConfig apiConfig) {
        super(apiConfig);
        autoRenew = true;
    }


    private final String uriBase() {
        return (session.getApiEndpoint() + "/services/data/" + config.getApiVersionString());
    }

    private final JsonNode normalizeCompositeResponse(JsonNode node) {
        Iterator<Map.Entry<String, JsonNode>> elements = node.fields();
        ObjectNode newNode = JsonNodeFactory.instance.objectNode();
        Map.Entry<String, JsonNode> currNode;
        while (elements.hasNext()) {
            currNode = elements.next();

            newNode.set(currNode.getKey(),
                    (currNode.getValue().isObject() &&
                            currNode.getValue().get("records") != null
                    ) ?
                            currNode.getValue().get("records") :
                            currNode.getValue()
            );
        }
        return newNode;

    }

    private final HttpResponse apiRequest(HttpRequest req) {
        req.setAuthorization("Bearer " + session.getAccessToken());
        HttpResponse res = Http.send(req);
        if (res.getResponseCode() == 401) {
            // Perform one attempt to auto renew session if possible
            if (autoRenew) {
                logger.debug("Session expired. Refreshing session...");
                if (session.getRefreshToken() != null) {
                    session = Auth.refreshOauthTokenFlow(config, session.getRefreshToken());
                } else {
                    session = Auth.authenticate(config);
                }
                if (config.getSessionRefreshListener() != null) {
                    config.getSessionRefreshListener().sessionRefreshed(session);
                }
                req.setAuthorization("Bearer " + session.getAccessToken());
                res = Http.send(req);
            }
        }
        if (res.getResponseCode() > 299) {
            if (res.getResponseCode() == 401) {
                throw new ApiTokenException(res.getString());
            } else {
                throw new ApiException(res.getResponseCode(), res.getString());
            }
        } else if (req.getExpectedCode() != -1 && res.getResponseCode() != req.getExpectedCode()) {
            throw new RuntimeException("Unexpected response from Force API. Got response code " + res.getResponseCode() +
                    ". Was expecting " + req.getExpectedCode());
        } else {
            return res;
        }
    }


    private static String toIso8601(Date date) {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'+00:00'");
        df.setTimeZone(tz);
        return df.format(date);
    }

    /**
     * @param startDateAndTime YYYY-MM-DDThh:mm:ss+hh:mm
     * @param endDateAndTime
     * @param sObject
     * @param <T>
     * @return
     */
    public GetDeletedSObject queryDeleted(String sObject,Date startDateAndTime, Date endDateAndTime) {
        try {
            String startParam = toIso8601(startDateAndTime);
            String endParam = toIso8601(endDateAndTime);
            String url = String.format("%s/sobjects/%s/deleted/?start=%s&end=%s", uriBase(), sObject, URLEncoder.encode(startParam, "UTF-8"), URLEncoder.encode(endParam, "UTF-8"));
            try {
                return jsonMapper.readValue(apiRequest(new HttpRequest()
                        .url(url)
                        .method("GET")
                        .header("Accept", "application/json")
                        .expectsCode(200)).getStream(),GetDeletedSObject.class);
            } catch (JsonParseException e) {
                throw new ResourceException(e);
            } catch (JsonMappingException e) {
                throw new ResourceException(e);
            } catch (IOException e) {
                throw new ResourceException(e);
            }

        } catch (UnsupportedEncodingException e) {
            throw new ResourceException(e);
        }

    }
}
