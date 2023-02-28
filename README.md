# Chatroom Backend

This is the backend for a chatroom application built using Spring Boot, PostgreSQL, and Websockets. The backend provides API endpoints for creating and managing chatrooms, sending and receiving messages in real-time using Websockets, and storing chatroom data in a PostgreSQL database.


## Requirements
- Java 17+
- PostgreSQL database
- Maven build tool

## Getting Started
1. Clone the repository
2. Configure the PostgreSQL database settings in `src/main/resources/application.properties`
3. Run `mvn spring-boot:run` to start the server
4. The server should be running on `http://localhost:8080`

## Endpoints

### Authentication
- `POST /register`: Registers a new user.
- `POST /login`: Authenticates a user and returns a JWT token in the response body.

The JWT token should be included in subsequent requests as an Authorization header with the format `Bearer {token}`.

Example response from `/login` endpoint:
```json
{
  "token": "EXAMPLE_TOKEN"
}
```

### Chatrooms
- `GET /chatrooms`: Returns a list of all chatrooms.
- `GET /chatrooms/{id}`: Returns details about a specific chatroom.
- `POST /chatrooms`: Creates a new chatroom.
- `PUT /chatrooms/{id}`: Updates an existing chatroom.
- `DELETE /chatrooms/{id}`: Deletes a chatroom.
- `POST /chatrooms/{id}/messages`: Sends a message to a specific chatroom.

### Messages
- `GET /messages`:  Returns the user's messages.
- `PUT /messages/{messageId}`: Updates a message in a specific chatroom.
- `DELETE /messages/{messageId}`: Deletes a message from a specific chatroom.

## Websockets

The backend uses Websockets to send and receive messages in real-time and track active users in a chatroom.
- Websocket connections require a JWT token to authenticate users.
- When a user sends a message, it is broadcasted to all users in the chatroom using Websockets.
- Clients can subscribe to a specific chatroom and receive updates in real-time when new messages are sent.
- Clients receive updates in real-time when users join/leave room.
