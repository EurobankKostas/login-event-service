# Login Event Service

## Overview
The Login Event service designed to emit login events to a Kafka topic upon user login and listen for updates on player bonuses from another Kafka topic.

## Prerequisites
- Java JDK 21
- Docker
- Kafka setup with the necessary topics configured
- Access to a relational database (PostgreSQL)


## Kafka Integration

### Topics
- **player-login-events**: Kafka topic for login events.
- **player-bonus-updates**: Kafka topic for receiving bonus updates.

### Producing Login Events
The service produces a message to `player-login-events` whenever a user logs in, containing the user's details and timestamp.

### Consuming Bonus Updates
The service listens on the `player-bonus-updates` topic for messages about bonus updates and applies them to the user's account in the database.

## API Endpoints

### POST `/v1/login-event` example locally: http://localhost:8084/v1/login.
- **Request Body**:
  ```json
  {
  "userId": "123e4567-e89b-12d3-a456-426614174000"
}

