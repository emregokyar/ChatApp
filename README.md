# Java WhatsApp Clone Test Project- Web

This is a full-stack WhatsApp clone built using **Java**, **Spring Framework** with clean project archtitecture and tested with **JUnit** and **Mockito**. Application is secured wwith **Spring Security**. It supports messaging, photo/file sharing, video calls, real-time updates, and secure registration with OTP authentication via email or phone.


---

## Technologies Used

### Backend
- **Java 17**
- **Spring Framework**
  - Spring Web
  - Spring Security
- **Hibernate (JPA)**
- **MySQL** (primary database)
- **H2 Database** (for testing)
- **Spring WebSocket** (real-time messaging)
- **Twilio API** (SMS OTP)
- **SMTP Mail Service** (Email OTP)
- **Agora API** (video calling)

### Testing
- **JUnit 5**
- **Mockito**
- **Custom Mock User Setup**
  - Custom security context factory
  - Custom mock user annotation

### Frontend
- **Thymeleaf**
- **HTML**
- **CSS**
- **JavaScript**
- **jQuery**
- **Bootstrap**

---

## Features

### Authentication
- **OTP-only login system** (no password fields)
- Register via **email** or **phone number**
- Email verification via **SMTP**
- Phone verification via **Twilio**
- Tokens are sent to the user’s email or phone based on registration type
- **Custom OTP token service** integrated with Spring Security
  - Token generation and validation is customized
  - Tokens are **persisted in the database**, not stored in memory
  - Includes a **custom success handler**

---

### Messaging & Contacts
- Create new **contacts**
- Create new **channels**
- Send **text messages**
- Share **photos**, **files** and **audio**
  - All files are stored in the local **file system**
- Receive real-time messages using **Spring WebSocket**
  - Supports both text and multimedia messages

---

### Video Calling
- Integrated with **Agora** for video call functionality
- WebSocket-based call notification
- Users can join a video call room when invited

---

### User Profile
- Upload and update profile photos
- Upload their fullname and about info
- Chnage privacy

---

## Testing

- In-memory **H2 database** configured for tests
- **Service layer** tests written using **Mockito**
- **Controller layer** tests with Spring’s test framework
- Secure methods are tested using:
  - **Custom mock user annotation**
  - **Custom mock security context factory**

---

## Purpose

The primary goal of this project is to **test and demonstrate integration** of real-world features like:
- OTP-based security with Spring Security
- Real-time WebSocket communication
- Video calling with external APIs
- Complex Spring Security testing strategies

---
