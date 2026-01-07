# HTTP Server from Scratch (Java)

A simple HTTP server implementation in Java, following the Boot.dev course structure.

## Project Structure

This project implements an HTTP server from scratch to learn:
- TCP socket programming
- HTTP protocol fundamentals
- Request/response parsing
- Routing and handlers
- Concurrency

## Building and Running

### Prerequisites
- Java 21 or higher

### Compile
```bash
./compile.sh
```
Or manually:
```bash
javac --enable-preview --release 21 src/main/java/com/bootdev/httpserver/*.java -d out
```

### Run
```bash
./run.sh
```

Or manually:
```bash
java --enable-preview -cp out com.bootdev.httpserver.HTTPServerMain
```

### Test
```bash
curl http://localhost:8080/
```