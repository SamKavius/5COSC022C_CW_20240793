# MLOps Pipeline Management API

## Overview
This coursework implements a JAX-RS REST API using Jersey and an embedded Grizzly server.

The API models an MLOps platform with three in-memory resources:
- `MLWorkspace`
- `MachineLearningModel`
- `EvaluationMetric`

It uses only JAX-RS plus in-memory Java collections, as required by the brief. No database or Spring-based stack is used.

Base path: `/api/v1`

## Build
Requirements:
- Java 21
- Maven

Run:

```bash
mvn clean package
```

## Run
Start the server with:

```bash
java -jar target/mlops-pipeline-api-1.0.0.jar
```

The default base URL is:

```text
http://localhost:8080/api/v1
```

To override the server base URI:

```bash
MLOPS_API_BASE_URI=http://0.0.0.0:8080/ java -jar target/mlops-pipeline-api-1.0.0.jar
```

## Endpoint Summary

### Discovery
- `GET /api/v1`

### Workspaces
- `GET /api/v1/workspaces`
- `POST /api/v1/workspaces`
- `GET /api/v1/workspaces/{workspaceId}`
- `HEAD /api/v1/workspaces/{workspaceId}`
- `DELETE /api/v1/workspaces/{workspaceId}`

### Models
- `GET /api/v1/models`
- `GET /api/v1/models?status=DEPLOYED`
- `POST /api/v1/models`
- `GET /api/v1/models/{modelId}`

### Evaluation Metrics
- `GET /api/v1/models/{modelId}/metrics`
- `POST /api/v1/models/{modelId}/metrics`

## Sample curl Commands

### 1. Discovery
```bash
curl -X GET http://localhost:8080/api/v1
```

### 2. Create a workspace
```bash
curl -X POST http://localhost:8080/api/v1/workspaces \
  -H "Content-Type: application/json" \
  -d '{
    "id": "WS-VISION-01",
    "teamName": "Computer Vision Lab",
    "storageQuotaGb": 500
  }'
```

### 3. List workspaces
```bash
curl -X GET http://localhost:8080/api/v1/workspaces
```

### 4. Check workspace existence with HEAD
```bash
curl -I http://localhost:8080/api/v1/workspaces/WS-VISION-01
```

### 5. Register a model
```bash
curl -X POST http://localhost:8080/api/v1/models \
  -H "Content-Type: application/json" \
  -d '{
    "framework": "TensorFlow",
    "status": "TRAINING",
    "latestAccuracy": 0.0,
    "workspaceId": "WS-VISION-01"
  }'
```

### 6. Filter models by status
```bash
curl -X GET "http://localhost:8080/api/v1/models?status=TRAINING"
```

### 7. Add an evaluation metric
```bash
curl -X POST http://localhost:8080/api/v1/models/MOD-REPLACE-ME/metrics \
  -H "Content-Type: application/json" \
  -d '{
    "timestamp": 1761830400000,
    "accuracyScore": 0.942
  }'
```

### 8. Read evaluation history
```bash
curl -X GET http://localhost:8080/api/v1/models/MOD-REPLACE-ME/metrics
```

### 9. Create and delete an empty workspace
```bash
curl -X POST http://localhost:8080/api/v1/workspaces \
  -H "Content-Type: application/json" \
  -d '{
    "id": "WS-TEMP-01",
    "teamName": "Temporary Sandbox",
    "storageQuotaGb": 100
  }'

curl -X DELETE http://localhost:8080/api/v1/workspaces/WS-TEMP-01
```

## Error Handling
The API returns JSON error bodies instead of raw stack traces. Key mapped cases are:
- `409 Conflict` when deleting a workspace that still contains models
- `422 Unprocessable Entity` when posting a model with a missing `workspaceId`
- `403 Forbidden` when posting a metric to a `DEPRECATED` model
- `404 Not Found` for missing resources
- `500 Internal Server Error` for unexpected unhandled exceptions

Example:

```json
{
  "error": "WORKSPACE_NOT_EMPTY",
  "message": "Workspace cannot be deleted because models are still assigned to it.",
  "status": 409
}
```

## Coursework Answers

### Part 1.1 - Role of `MessageBodyWriter` / JSON Provider
When a JAX-RS resource method returns a Java object, the runtime does not send that object directly over HTTP. It asks a suitable `MessageBodyWriter` to convert the object into the negotiated representation. In this project, Jersey uses the Jackson JSON provider to serialise POJOs such as `MLWorkspace`, `MachineLearningModel`, and `EvaluationMetric` into JSON for the response body.

This matters because resource methods stay focused on business logic while the provider handles representation concerns such as field-to-JSON mapping, content type support, and output encoding. Without a compatible provider, Jersey would not know how to convert custom Java objects into `application/json`.

### Part 1.2 - Statelessness in REST
Statelessness means each HTTP request contains everything the server needs in order to process it. The server should not depend on conversational session state stored between requests for correctness. In this API, every operation is self-contained: for example, `POST /models` includes the `workspaceId` needed to validate the relationship, and `POST /models/{modelId}/metrics` identifies the parent model in the URI.

This makes cloud APIs easier to scale horizontally because any request can be routed to any server instance without requiring sticky sessions. If one instance fails, another can continue processing requests because the client carries the necessary context in each request.

### Part 2.1 - `Cache-Control` on `GET /workspaces`
`Cache-Control` headers could improve performance by allowing clients or intermediaries to reuse fresh representations instead of repeatedly downloading the same workspace list. For example, a short `max-age` could reduce latency for dashboards that poll frequently and reduce repeated JSON serialisation work on the server.

They also reduce unnecessary compute and bandwidth consumption. If the resource does not change often, caching avoids rebuilding the same collection response for every request. In a larger cloud deployment this can materially lower load on application instances.

### Part 2.2 - Which HTTP Method Instead of `GET`?
The client should use `HEAD`. A `HEAD` request targets the same resource as `GET` but returns only the headers and status code, not the response body. That makes it appropriate when the client only needs to confirm whether a workspace exists.

This saves bandwidth because the server does not transmit the JSON representation. In this project, `HEAD /api/v1/workspaces/{workspaceId}` is implemented specifically for that purpose.

### Part 3.1 - Why Generate Model IDs on the Server?
Server-generated IDs are better for security and integrity because the server remains the single authority over primary-key creation. That avoids accidental collisions, predictable identifiers, and malicious attempts to overwrite or impersonate existing resources by submitting chosen IDs in the payload.

It also simplifies validation and consistency rules. In this implementation the server generates model IDs with a `MOD-...` pattern, so clients only supply domain data such as framework, status, and `workspaceId`, while identity management stays under server control.

### Part 3.2 - URL Encoding for Spaces and Special Characters
If a query value contains spaces or special characters, the client must percent-encode the value. For example, `Scikit Learn & Tools` should be sent as:

```text
?framework=Scikit%20Learn%20%26%20Tools
```

Encoding is necessary because characters such as spaces and `&` have structural meaning in URLs. Without encoding, the server may interpret the query incorrectly, split parameters at the wrong place, or reject the request as malformed.

### Part 4.1 - Benefit of Class-Level `@Produces`
Placing `@Produces(MediaType.APPLICATION_JSON)` at class level removes duplication and gives all methods in that resource a clear default response format. That improves readability and keeps the media-type contract consistent across the resource.

Method-level annotations can still override the class-level default for specific operations. JAX-RS resolves the method-level declaration first when both are present, so a single endpoint can opt into a different representation if needed while the rest of the class keeps the shared default.

### Part 4.2 - Why the Sub-Resource Updates the Parent Model
When a new evaluation metric is created, updating the parent model’s `latestAccuracy` keeps the aggregate state consistent across related resources. Clients can read the current headline accuracy from the model itself without having to fetch the full metrics history and compute the latest value manually.

This is useful because the parent resource and nested history remain synchronised. The API exposes both a quick summary (`latestAccuracy`) and the underlying audit trail (`/models/{modelId}/metrics`) without leaving them out of step.

### Part 5.1 - Why a Missing `workspaceId` Must Return 4xx, Not 5xx
A non-existent `workspaceId` is caused by invalid client input, so it is a client-side problem rather than a server failure. The request reached a valid endpoint and the server understood it, but the submitted representation could not be processed because the linked workspace does not exist.

That is exactly what 4xx status codes communicate: the client must change something about the request before retrying. A 5xx response would incorrectly suggest that the server itself malfunctioned even though the real issue was with the submitted data.

### Part 5.2 - Mapper Selection When Both Specific and Global Mappers Exist
JAX-RS chooses the most specific applicable `ExceptionMapper`. If an operation throws `LinkedWorkspaceNotFoundException`, the runtime will use `ExceptionMapper<LinkedWorkspaceNotFoundException>` instead of the generic `ExceptionMapper<Throwable>`.

The global mapper acts as a safety net for exceptions that do not have a more specific mapper. This is why it is safe to keep a catch-all 500 mapper while still returning accurate 403, 404, 409, and 422 responses for known business cases.

### Part 5.3 - Valuable Metadata in Request/Response Filters
Two especially useful pieces of metadata are the full request URI and the HTTP headers. The URI helps identify exactly which resource and parameter combination triggered the issue, while headers can reveal content negotiation problems, authentication mistakes, or malformed client metadata.

Another high-value item is the final response status code. In this project the filter logs the method, URI, and final status so failures can be correlated quickly during debugging and demonstration.

## Notes on Compliance
- JAX-RS with Jersey is the only web API technology used.
- Data is stored in memory via Java collections, not a database.
- The README includes the API overview, build/run instructions, sample `curl` commands, and the written answers required by the brief.
