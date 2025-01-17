/*
 * Copyright (c) 2022, WSO2 LLC. (http://www.wso2.com).
 *
 * WSO2 LLC. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


package org.wso2.apk.apimgt.impl.kmclient;

import feign.Client;
import feign.Request;
import feign.Response;
import feign.Util;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.Configurable;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.wso2.apk.apimgt.impl.APIConstants;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.*;

import static feign.Util.UTF_8;

/***
 * This class is to be used to get HTTP Client from feign library
 * TODO: Remove this once the Http Client library is updated
 */
public final class ApacheFeignHttpClient implements Client {
    private static final String ACCEPT_HEADER_NAME = "Accept";

    private final HttpClient client;

    public ApacheFeignHttpClient() {
        this(HttpClientBuilder.create().build());
    }

    public ApacheFeignHttpClient(HttpClient client) {
        this.client = client;
    }

    @Override
    public Response execute(Request request, Request.Options options) throws IOException {
        HttpUriRequest httpUriRequest;
        try {
            httpUriRequest = toHttpUriRequest(request, options);
        } catch (URISyntaxException e) {
            throw new IOException("URL '" + request.url() + "' couldn't be parsed into a URI", e);
        }
        HttpResponse httpResponse = client.execute(httpUriRequest);
        return toFeignResponse(httpResponse, request);
    }

    HttpUriRequest toHttpUriRequest(Request request, Request.Options options)
            throws URISyntaxException {
        RequestBuilder requestBuilder = RequestBuilder.create(request.httpMethod().name());

        // per request timeouts
        RequestConfig requestConfig =
                (client instanceof Configurable ? RequestConfig.copy(((Configurable) client).getConfig())
                        : RequestConfig.custom())
                        .setConnectTimeout(options.connectTimeoutMillis())
                        .setSocketTimeout(options.readTimeoutMillis())
                        .build();
        requestBuilder.setConfig(requestConfig);

        URI uri = new URIBuilder(request.url()).build();

        requestBuilder.setUri(uri.getScheme() + "://" + uri.getAuthority() + uri.getRawPath());

        // request query params
        List<NameValuePair> queryParams =
                URLEncodedUtils.parse(uri, APIConstants.DigestAuthConstants.CHARSET);
        for (NameValuePair queryParam : queryParams) {
            requestBuilder.addParameter(queryParam);
        }

        // request headers
        boolean hasAcceptHeader = false;
        for (Map.Entry<String, Collection<String>> headerEntry : request.headers().entrySet()) {
            String headerName = headerEntry.getKey();
            if (headerName.equalsIgnoreCase(ACCEPT_HEADER_NAME)) {
                hasAcceptHeader = true;
            }

            if (headerName.equalsIgnoreCase(Util.CONTENT_LENGTH)) {
                // The 'Content-Length' header is always set by the Apache client and it
                // doesn't like us to set it as well.
                continue;
            }

            for (String headerValue : headerEntry.getValue()) {
                requestBuilder.addHeader(headerName, headerValue);
            }
        }
        // some servers choke on the default accept string, so we'll set it to anything
        if (!hasAcceptHeader) {
            requestBuilder.addHeader(ACCEPT_HEADER_NAME, "*/*");
        }

        // request body
        if (request.body() != null) {
            HttpEntity entity = null;
            if (request.charset() != null) {
                ContentType contentType = getContentType(request);
                String content = new String(request.body(), request.charset());
                entity = new StringEntity(content, contentType);
            } else {
                entity = new ByteArrayEntity(request.body());
            }

            requestBuilder.setEntity(entity);
        } else {
            requestBuilder.setEntity(new ByteArrayEntity(new byte[0]));
        }

        return requestBuilder.build();
    }

    private ContentType getContentType(Request request) {
        ContentType contentType = null;
        for (Map.Entry<String, Collection<String>> entry : request.headers().entrySet())
            if (entry.getKey().equalsIgnoreCase("Content-Type")) {
                Collection<String> values = entry.getValue();
                if (values != null && !values.isEmpty()) {
                    contentType = ContentType.parse(values.iterator().next());
                    if (contentType.getCharset() == null) {
                        contentType = contentType.withCharset(request.charset());
                    }
                    break;
                }
            }
        return contentType;
    }

    Response toFeignResponse(HttpResponse httpResponse, Request request) throws IOException {
        StatusLine statusLine = httpResponse.getStatusLine();
        int statusCode = statusLine.getStatusCode();

        String reason = statusLine.getReasonPhrase();

        Map<String, Collection<String>> headers = new HashMap<String, Collection<String>>();
        for (Header header : httpResponse.getAllHeaders()) {
            String name = header.getName();
            String value = header.getValue();

            Collection<String> headerValues = headers.get(name);
            if (headerValues == null) {
                headerValues = new ArrayList<String>();
                headers.put(name, headerValues);
            }
            headerValues.add(value);
        }

        return Response.builder()
                .status(statusCode)
                .reason(reason)
                .headers(headers)
                .request(request)
                .body(toFeignBody(httpResponse))
                .build();
    }

    Response.Body toFeignBody(HttpResponse httpResponse) {
        final HttpEntity entity = httpResponse.getEntity();
        if (entity == null) {
            return null;
        }
        return new Response.Body() {

            @Override
            public Integer length() {
                return entity.getContentLength() >= 0 && entity.getContentLength() <= Integer.MAX_VALUE
                        ? (int) entity.getContentLength()
                        : null;
            }

            @Override
            public boolean isRepeatable() {
                return entity.isRepeatable();
            }

            @Override
            public InputStream asInputStream() throws IOException {
                return entity.getContent();
            }

            @SuppressWarnings("deprecation")
            @Override
            public Reader asReader() throws IOException {
                return new InputStreamReader(asInputStream(), UTF_8);
            }

            @Override
            public Reader asReader(Charset charset) throws IOException {
                Util.checkNotNull(charset, "charset should not be null");
                return new InputStreamReader(asInputStream(), charset);
            }

            @Override
            public void close() throws IOException {
                EntityUtils.consume(entity);
            }
        };
    }
}
