package com.example.test_helpers;

import spark.utils.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ApiClient {
    // server URL
    private String server;

    // upon construction server URL is passed like "http://localhost:4567"
    public ApiClient(String server) {
        this.server = server;
    }

    /**
     * returns ApiResponse class, when request body is null
     * @param method HTTP method: can be "PUT", "POST", "DELETE", "GET"
     * @param uri URI of request
     * @return ApiResponse instance with body and status to check
     */
    public ApiResponse request(String method, String uri) {
        return request(method, uri, null);
    }

    /**
     *
     * @param method HTTP method: can be "PUT", "POST", "DELETE", "GET"
     * @param uri URI of request
     * @param requestBody used for POST and PUT requests
     * @return ApiResponse class instance with bosy and status to check
     */
    public ApiResponse request(String method, String uri, String requestBody) {
        try {
            // adds uri to server URL
            URL url = new URL(server + uri);

            // create http connection and open it
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // set request method
            connection.setRequestMethod(method);

            // set content-type header
            connection.setRequestProperty("Content-Type", "application/json");

            // if request bosy is not null, i.e. PUT and POST requests
            if (requestBody != null) {
                // set output
                connection.setDoOutput(true);
                // trying to get bytes from request body
                try (OutputStream os = connection.getOutputStream()) {
                    os.write(requestBody.getBytes("UTF-8"));
                }
            }

            // connect
            connection.connect();

            // if response code is good : > 400, we get input stream,
            // otherwise error stream
            InputStream inputStream = connection.getResponseCode() < 400 ?
                    connection.getInputStream() :
                    connection.getErrorStream();

            // get body from input stream: error or success
            String body = IOUtils.toString(inputStream);

            // return new ApiResponse class with body and status
            return new ApiResponse(connection.getResponseCode(), body);

            // here we basically catch all possible errors and print
            // stack trace
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Whoops!  Connection error");
        }
    }
}