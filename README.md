# Shop Application (Microservices)

## Project Overview
This project improves the previous shop application by using microservices.  
- The monolithic system is broken into microservices.  
- Each service handles one part, like customers, orders, or products.  
- Services talk to each other with Kafka (messages).  
- Each service has its own database (Postgres, Mongo).  
- Google sign-in is added for customers along with token security.  
- Patterns like API gateway and load balancer are used to keep the system fast and safe.  
- Tests are written to achieve at least 50% code coverage.  
- Used API Gateway as one entry point for all client requests.  

- **Enhanced Security**  
  Google login is integrated for customers in addition to the existing token-based security.  

## Technology Stack
- **Microservices Framework:** Spring Boot (Java 17)  
- **Messaging:** Apache Kafka 
- **Databases:** Each service has its own (Postgres, Mongo)   
- **Security:**  
  - Token-based authentication (access and refresh tokens)  
  - Google OAuth2 login for customers  
- **Testing:**  
  - JUnit and Mockito for unit tests  
- **Containerization:** Docker Compose  

---
