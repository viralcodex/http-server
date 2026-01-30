package com.bootdev.constants;

import java.nio.charset.StandardCharsets;

public class Constants {
    public static final byte[] BAD_REQUEST_HTML = """
            <html>
              <head>
                <title>400 Bad Request</title>
              </head>
              <body>
                <h1>Bad Request</h1>
                <p>Your request honestly kinda sucked.</p>
              </body>
            </html>
            """.getBytes(StandardCharsets.US_ASCII);

    public static final byte[] INTERNAL_SERVER_ERROR_HTML = """
            <html>
              <head>
                <title>500 Internal Server Error</title>
              </head>
              <body>
                <h1>Internal Server Error</h1>
                <p>Okay, you know what? This one is on me.</p>
              </body>
            </html>
            """.getBytes(StandardCharsets.US_ASCII);

    public static final byte[] SUCCESS_HTML = """
            <html>
              <head>
                <title>200 OK</title>
              </head>
              <body>
                <h1>Success!</h1>
                <p>Your request was an absolute banger.</p>
              </body>
            </html>
            """.getBytes(StandardCharsets.US_ASCII);
}
