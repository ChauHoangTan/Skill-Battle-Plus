# 🏆 Skill Battle Plus
Skill Battle Plus is a competitive online learning platform that combines quizzes, programming challenges, and real-time PvP battles.
Players can test their knowledge, challenge friends, and compete in real-time duels powered by WebSocket.

This project follows a Microservices Architecture, is fully containerized with Docker, orchestrated by Kubernetes (K8s), and supports real-time communication.

# 🚀 Features
- 👤 User Management – sign up, login, and role-based access (Admin, Player)

- 📚 Quiz & Exam – create and take quizzes with automatic scoring

- ⚔️ PvP Battle – real-time player vs. player challenges via WebSocket

- 📊 Result & Analytics – leaderboard, performance tracking

- 🔔 Notification System – event and challenge notifications

- 🔐 Security – JWT Authentication & Role-based Authorization

#  ⚙️ Tech Stack

- Backend: Java, Spring Boot (Web, Security, JPA, Validation)

- Database: PostgreSQL

- Search Engine: Elasticsearch, Kibana

- Message Broker: RabbitMQ (event-driven communication)

- Real-time Communication: WebSocket

- API Gateway & Discovery: Spring Cloud Gateway, Eureka

- Containerization & Orchestration: Docker, Docker Compose, Kubernetes

- CI/CD: Jenkins, GitHub Actions

# 🏗️ Architecture

- Microservices-based architecture built with Spring Boot

- Event-driven communication between services via RabbitMQ

- API Gateway for routing and centralized entry point

- Service discovery with Eureka

- Real-time features using WebSocket

- Search and analytics powered by Elasticsearch & Kibana

- Deployment with Docker & Kubernetes

- Automated CI/CD pipelines using Jenkins / GitHub Actions

# 🏗️ Modules

- admin-service – Manage system, users, and content.

- analytics-service – Collect and analyze performance data.

- api-gateway – Entry point for all requests.

- auth-service – Authentication & JWT authorization.

- battle-service – Real-time battle logic via WebSockets.

- notification-service – Push system/user notifications.

- question-service – Manage quiz/exam questions.

- quiz-service – Handle quizzes and results.

- exam-service – Manage exams and grading.

- user-service – User profiles & settings.

- eureka-service – Service discovery.

# 🔄 System Flows - Skill Battle Plus
**1. Service-to-Service Flow (via API Gateway + Eureka)**

1. Client (Frontend) sends a request → api-gateway.

2. api-gateway checks authentication/authorization with auth-service.

3. api-gateway discovers the correct service via eureka-service.

4. The request is routed to the target service (e.g., quiz-service, battle-service, user-service).

5. The service processes the request and may call other services internally.

6. The response flows back → api-gateway → client.

**📌 Example:**

- User starts a quiz → request goes to quiz-service.

- quiz-service fetches questions from question-service.

- Final quiz data is returned to the frontend.

**2. RabbitMQ Flow (Event-Driven Communication)**

RabbitMQ is used to decouple services when sending events or data asynchronously.

**📌 Example:**

- User Service updates a user profile → publishes a USER_UPDATED event to RabbitMQ.

- Analytics Service consumes it to log activity.

- Notification Service also consumes it to notify friends (e.g., "Haze just leveled up").

**📌 Another example:**

- Quiz Service finishes a quiz → publishes QUIZ_COMPLETED.

- Analytics Service updates stats.

- Notification Service sends results notification.

**3. Elasticsearch Flow (Search & Indexing)**

Elasticsearch is used for fast searching and filtering (better than plain SQL for queries like "search question by keyword, tags, difficulty").

**📌 Example:**

- Admin creates or updates a question in Question Service.

- After saving in DB, service pushes the data to Elasticsearch index.

- When a user searches (keyword=Spring Boot, tags=Java), the API Gateway routes request to Question Service, which queries Elasticsearch instead of DB → results returned fast.

- Analytics Service can also store aggregated logs into Elasticsearch for dashboards.

**4. WebSocket Flow (Real-Time Battle & Notifications)**

WebSocket is mainly used for real-time battle (PvP) and live notifications.

**📌 Example PvP Battle:**

1. Player A challenges Player B → request goes to Battle Service.

2. Battle Service opens a WebSocket room for both players.

3. Each question is pushed via WebSocket → players send answers in real-time.

4. The server validates answers, broadcasts updates (score, timer) back instantly.

5. When battle ends → Battle Service saves results → pushes BATTLE_COMPLETED event to RabbitMQ → Analytics + Notification consume it.

**📌 Example Notification:**

1. Notification Service receives an event (e.g., "Friend request accepted").

2. If the user is online → push directly via WebSocket.

3. If offline → store it → push via email or when they reconnect.

# ⚡ Getting Started

**Clone repository**

git clone https://github.com/your-repo/skill-battle-plus.git

**Run with Docker Compose**

docker-compose up --build

**Access to port**

http://localhost:8080/
