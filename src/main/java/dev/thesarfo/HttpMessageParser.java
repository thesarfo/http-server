package dev.thesarfo;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class HttpMessageParser {

    @Data
    public static class Request{

        /*
        * method of the request e.g. GET, POST, PUT, DELETE
         */
        private String method;

        /*
        * requested URI
         */
        private String uri;

        /*
        * version of the HTTP protocol
         */
        private String version;

        /*
        * request headers
         */
        private Map<String, String> headers;

        /*
        * request parameters
         */
        private String message;
    }

    /**
     * Parses the request line according to the HTTP protocol
     *
     * @param reader
     * @param request
     */
    private static void decodeRequestLine(BufferedReader reader, Request request) throws IOException {
        String[] strs = org.apache.commons.lang3.StringUtils.split(reader.readLine(), " ");
        assert strs.length == 3;
        request.setMethod(strs[0]);
        request.setUri(strs[1]);
        request.setVersion(strs[2]);
    }

    /**
     * Parses request headers according to the HTTP protocol
     *
     * @param reader BufferedReader object that reads request headers
     * @param request request object that stores request information
     * @throws IOException thrown when an error occurs while reading the request headers
     */
    private static void decodeRequestHeader(BufferedReader reader, Request request) throws IOException {
        // create a map object to store request header info
        Map<String, String> headers = new HashMap<>(16);

        // read the request header info, each line is a kv pair and ends with a blank line
        String line = reader.readLine();
        String[] kv;

        while(!"".equals(line)){
            // separate each line of the request by colons and store the keys and values in a map
            kv = StringUtils.split(line, ":");
            assert kv.length == 2;
            headers.put(kv[0].trim(), kv[1].trim());
            line = reader.readLine();
        }
        // store the parsed request header info in the request object
        request.setHeaders(headers);
    }

    /**
     * parse the text of the request according to the HTTP protocol annotation
     *
     * @param reader    input stream reader used to read data in the request
     * @param request   request object representing http request
     * @throws IOException thrown when an I/O error occurs
     */
    private static void decodeRequestMessage(BufferedReader reader, Request request) throws IOException {
        int contentLen = Integer.parseInt(request.getHeaders().getOrDefault("Content-Length", "0"));

        if (contentLen == 0) {
            return;
        }
        char[] message = new char[contentLen];
        reader.read(message);
        request.setMessage(new String(message));
    }

    /**
     * http request can be divided into 3 parts
     *
     * 1. request line
     * 2. request header represented by an empty line
     * 3. request body
     *
     * @param reqStream
     * @return
     * @throws IOException
     */
    public static Request parse2request(InputStream reqStream) throws IOException {
        BufferedReader httpReader = new BufferedReader(new InputStreamReader(reqStream, "UTF-8"));
        Request httpRequest = new Request();
        decodeRequestLine(httpReader, httpRequest);
        decodeRequestHeader(httpReader, httpRequest);
        decodeRequestMessage(httpReader, httpRequest);
        return httpRequest;
    }
}

