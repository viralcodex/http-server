package com.bootdev.constants;

import java.nio.charset.StandardCharsets;

public class Constants {
    public static final byte[] BAD_REQUEST_HTML = """
            <html>
              <head>
                <title>400 Bad Request</title>
                <style>
                  body { font-family: Arial, sans-serif; max-width: 800px; margin: 50px auto; padding: 20px; background: #f5f5f5; }
                  .container { background: white; padding: 30px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
                  h1 { color: #d32f2f; margin-top: 0; }
                  .status { background: #ffebee; padding: 10px; border-radius: 4px; border-left: 4px solid #d32f2f; margin: 20px 0; }
                  .info { color: #666; margin-top: 20px; }
                  code { background: #f5f5f5; padding: 2px 6px; border-radius: 3px; font-family: monospace; }
                </style>
              </head>
              <body>
                <div class="container">
                  <h1>400 Bad Request</h1>
                  <div class="status">
                    <strong>Your request honestly kinda sucked.</strong>
                  </div>
                  <p>The server couldn't understand your request. This usually happens when:</p>
                  <ul>
                    <li>The request is malformed or missing required headers</li>
                    <li>Invalid HTTP syntax was used</li>
                    <li>The request body is corrupted</li>
                  </ul>
                  <div class="info">
                    <strong>Available endpoints:</strong>
                    <ul>
                      <li><code>GET /</code> - Success page</li>
                      <li><code>GET /httpbin/*</code> - Streaming proxy to httpbin.org</li>
                      <li><code>GET /video</code> - Binary video file</li>
                      <li><code>GET /yourproblem</code> - Triggers 400 error (this page)</li>
                      <li><code>GET /myproblem</code> - Triggers 500 error</li>
                    </ul>
                  </div>
                </div>
              </body>
            </html>
            """.getBytes(StandardCharsets.US_ASCII);

    public static final byte[] INTERNAL_SERVER_ERROR_HTML = """
            <html>
              <head>
                <title>500 Internal Server Error</title>
                <style>
                  body { font-family: Arial, sans-serif; max-width: 800px; margin: 50px auto; padding: 20px; background: #f5f5f5; }
                  .container { background: white; padding: 30px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
                  h1 { color: #ff6f00; margin-top: 0; }
                  .status { background: #fff3e0; padding: 10px; border-radius: 4px; border-left: 4px solid #ff6f00; margin: 20px 0; }
                  .info { color: #666; margin-top: 20px; }
                  code { background: #f5f5f5; padding: 2px 6px; border-radius: 3px; font-family: monospace; }
                </style>
              </head>
              <body>
                <div class="container">
                  <h1>500 Internal Server Error</h1>
                  <div class="status">
                    <strong>Okay, you know what? This one is on me.</strong>
                  </div>
                  <p>Something went wrong on the server side. This endpoint is designed to trigger an error for demonstration purposes.</p>
                  <p>In a real application, this would be caused by:</p>
                  <ul>
                    <li>Unhandled exceptions in the handler code</li>
                    <li>Database connection failures</li>
                    <li>External service timeouts</li>
                    <li>Unexpected null values or runtime errors</li>
                  </ul>
                  <div class="info">
                    <strong>Try these working endpoints instead:</strong>
                    <ul>
                      <li><code>GET /</code> - Success page</li>
                      <li><code>GET /httpbin/stream/5</code> - Streaming proxy demo</li>
                      <li><code>GET /video</code> - Binary video file</li>
                    </ul>
                  </div>
                </div>
              </body>
            </html>
            """.getBytes(StandardCharsets.US_ASCII);

    public static final byte[] SUCCESS_HTML = """
            <html>
              <head>
                <title>200 OK - HTTP Server Demo</title>
                <style>
                  body { font-family: Arial, sans-serif; max-width: 900px; margin: 50px auto; padding: 20px; background: #f5f5f5; }
                  .container { background: white; padding: 40px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
                  h1 { color: #2e7d32; margin-top: 0; border-bottom: 3px solid #4caf50; padding-bottom: 10px; }
                  h2 { color: #1565c0; margin-top: 30px; font-size: 1.3em; }
                  .status { background: #e8f5e9; padding: 15px; border-radius: 4px; border-left: 4px solid #4caf50; margin: 20px 0; }
                  .endpoint { background: #f5f5f5; padding: 12px; border-radius: 4px; margin: 10px 0; border-left: 3px solid #1976d2; }
                  .endpoint code { background: #e3f2fd; padding: 3px 8px; border-radius: 3px; font-family: monospace; font-size: 0.95em; color: #0d47a1; }
                  .endpoint p { margin: 5px 0; color: #555; }
                  .feature { display: inline-block; background: #fff3e0; padding: 5px 10px; border-radius: 3px; margin: 5px; font-size: 0.9em; }
                  .info { color: #666; margin-top: 20px; padding: 15px; background: #fafafa; border-radius: 4px; }
                  code { font-family: 'Courier New', monospace; }
                </style>
              </head>
              <body>
                <div class="container">
                  <h1>HTTP/1.1 Server from Scratch</h1>
                  <div class="status">
                    <strong>Your request was an absolute banger.</strong>
                  </div>
                  <p>This is a working HTTP/1.1 server built from raw TCP sockets in Java. No frameworks, no built-in HTTP libraries.</p>
            
                  <h2>Available Endpoints</h2>
            
                  <div class="endpoint">
                    <code>GET /</code>
                    <p>This page - demonstrates basic HTTP response handling</p>
                  </div>
            
                  <div class="endpoint">
                    <code>GET /httpbin/stream/N</code>
                    <p>Streaming proxy to httpbin.org - forwards N JSON objects using chunked transfer-encoding with SHA-256 trailer</p>
                    <p><em>Example: <code>curl http://localhost:42069/httpbin/stream/5</code></em></p>
                  </div>
            
                  <div class="endpoint">
                    <code>GET /video</code>
                    <p>Binary file response - serves an MP4 video file to demonstrate binary content handling</p>
                    <p><em>Example: <code>curl http://localhost:42069/video -o video.mp4</code></em></p>
                  </div>
            
                  <div class="endpoint">
                    <code>GET /yourproblem</code>
                    <p>Triggers a 400 Bad Request error (demonstrates error handling)</p>
                  </div>
            
                  <div class="endpoint">
                    <code>GET /myproblem</code>
                    <p>Triggers a 500 Internal Server Error (demonstrates server error handling)</p>
                  </div>
            
                  <h2>Implemented Features</h2>
                  <div style="margin-top: 15px;">
                    <span class="feature">Incremental Request Parser</span>
                    <span class="feature">Chunked Transfer Encoding</span>
                    <span class="feature">HTTP Trailers</span>
                    <span class="feature">Streaming Proxy</span>
                    <span class="feature">Binary Responses</span>
                    <span class="feature">SHA-256 Checksums</span>
                    <span class="feature">Case-insensitive Headers</span>
                  </div>
            
                  <div class="info">
                    <strong>Technical Details:</strong>
                    <ul style="margin: 10px 0;">
                      <li>Pure Java 21 implementation</li>
                      <li>Raw TCP sockets (ServerSocket)</li>
                      <li>One thread per connection</li>
                      <li>No external dependencies</li>
                      <li>Fully HTTP/1.1 compliant parsing</li>
                    </ul>
                  </div>
                </div>
              </body>
            </html>
            """.getBytes(StandardCharsets.US_ASCII);
}
