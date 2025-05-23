

## Backend (`chat-room-backend/README.md`)

```markdown
# ChatRoom Backend

A Spring Boot service providing REST & WebSocket APIs for a real-time chatroom.  
Stores messages (text & images) in MongoDB, uses GridFS for image storage, and broadcasts via STOMP.

## 🚀 Features

- Create & join chat rooms  
- Persist chat history in MongoDB  
- Real-time messaging over STOMP/WebSocket  
- Image upload & retrieval via GridFS  
- Pagination of message history  

## 🛠️ Tech Stack

- **Language:** Java 21  
- **Framework:** Spring Boot 3.4.5  
- **Data:** Spring Data MongoDB + GridFS  
- **WebSocket:** spring-boot-starter-websocket (SockJS + STOMP)  
- **Build:** Maven  

## 📋 Prerequisites

- Java JDK 21  
- Maven  
- MongoDB (running on `mongodb://localhost:27017`)

## 🔧 Setup & Run

1. **Clone repo**  
   ```bash
   git clone <your-backend-repo-url>
   cd chat-room-backend
Configure
Edit src/main/resources/application.properties:

properties
Copy
Edit
spring.data.mongodb.uri=mongodb://localhost:27017/chatapp

# allow up to 20 MB image uploads
spring.servlet.multipart.max-file-size=20MB
spring.servlet.multipart.max-request-size=20MB
Build & Run

bash
Copy
Edit
mvn clean package
java -jar target/chat-room-backend-0.0.1-SNAPSHOT.jar
or for dev:

bash
Copy
Edit
mvn spring-boot:run
Server starts on port 8080 by default.

📦 API Reference
REST Endpoints
Method & URL	Description
POST /api/v1/rooms	Create a room (body = plain text roomId)
GET /api/v1/rooms/{roomId}	Join a room (returns room info)
GET /api/v1/rooms/{roomId}/messages?size=&page=	Fetch paginated history
POST /api/v1/rooms/{roomId}/images	Upload image (multipart: file, sender) → returns { imageUrl }
GET /api/v1/images/{fileId}	Retrieve image bytes

WebSocket (STOMP)
Endpoint: ws://localhost:8080/chat (SockJS fallback)

Send to: /app/sendMessage/{roomId}

json
Copy
Edit
{
  "sender": "alice",
  "content": "Hi!",
  "imageUrl": "http://localhost:8080/api/v1/images/<fileId>",
  "roomId": "familyroom1"
}
Subscribe to: /topic/room/{roomId}
Receives Message objects with sender, content?, imageUrl?, timeStamp.

⚙️ WebSocketConfig & CORS
WebSocket endpoint allowed origin: http://localhost:5173

REST CORS also enabled on controllers for the same origin.

📝 License
MIT License.

yaml
Copy
Edit

---

Feel free to tweak any URLs or sections to match your repo names and workflows.
