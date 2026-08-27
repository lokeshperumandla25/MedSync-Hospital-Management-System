# MedSync – Hospital Management System

A microservices-based Hospital Management System built with **Java, Spring Boot, Spring Cloud, REST APIs, MySQL, Eureka Service Discovery, API Gateway, OpenFeign, JWT authentication, and SendGrid**.

The system separates major hospital operations into independent services for patients, staff/doctors, appointments, beds/rooms, and notifications.

## Architecture

flowchart LR
    Client[Client / Frontend] --> Gateway[API Gateway :8000]
    Gateway --> Eureka[Eureka Server :8761]

    Gateway --> Patient[Patient Service :8002]
    Gateway --> Staff[Staff Service :8003]
    Gateway --> Appointment[Appointment Service :8008]
    Gateway --> Bed[Bed Service :9001]
    Gateway --> Notification[Notification Service :8004]

    Patient --> PatientDB[(MySQL)]
    Staff --> StaffDB[(MySQL)]
    Appointment --> AppointmentDB[(MySQL)]
    Bed --> BedDB[(MySQL)]

    Appointment -> OpenFeign -> Patient
    Appointment -> OpenFeign -> Staff
    Appointment -> OpenFeign -> Notification
    Staff -> OpenFeign -> Notification
    Notification --> SendGrid[SendGrid Email API]


Services

|       Service       | Port |              Responsibility                                |

| EurekaServer        | 8761 | Service discovery and registration                         |
| GatewayService      | 8000 | API gateway, routing, CORS, JWT validation                 |
| PatientService      | 8002 | Patient registration and patient information               |
| StaffService        | 8003 | Staff/doctor management, authentication, schedules         |
| NotificationService | 8004 | Email notifications through SendGrid                       |
| AppointmentService  | 8008 | Appointment booking, rescheduling, cancellation, diagnosis |
| BedService          | 9001 | Rooms, beds, assignments, vacancies, and bed history       |

## Key Features

### Patient Management
- Register patients
- Retrieve all patients
- Retrieve a patient by ID
- Update patient information
- Retrieve patient names for other services

### Staff & Doctor Management
- Staff registration
- Staff login
- Staff lookup and search
- Staff updates
- Role-based authorization for administrative operations
- Doctor name lookup
- Doctor availability scheduling
- Forgot-password workflow using OTP
- Password reset
- BCrypt password hashing

### Appointment Management
- Book appointments
- View appointments by date
- View appointments for a specific doctor
- View future appointments
- Retrieve appointment details
- Reschedule appointments
- Cancel appointments
- Submit diagnosis
- Retrieve patients who visited a doctor within a date range

### Bed & Room Management
- Create rooms
- Update rooms
- Delete rooms
- List rooms
- Add beds
- Update beds
- Delete beds
- List beds
- Find beds by room
- Find vacant beds
- Assign beds to patients
- Vacate beds
- View bed assignment history

### Notification Service
- Send email notifications
- Integrates with SendGrid
- Used by other services through OpenFeign

### Security
- JWT-based authentication
- Stateless Spring Security configuration
- BCrypt password hashing
- JWT validation at the API Gateway
- JWT authentication inside StaffService
- Role-based authorization using Spring Security

## Technology Stack

**Backend**
- Java 17
- Spring Boot
- Spring Web MVC
- Spring Data JPA
- Spring Security
- Spring Cloud

**Microservices**
- Netflix Eureka
- Spring Cloud Gateway
- Spring Cloud OpenFeign

**Database**
- MySQL

**Authentication**
- JWT
- BCrypt

**Email**
- SendGrid

**Build Tool**
- Maven

## API Overview

The main API groups are:

### Patient Service

POST   /patients/register \
GET    /patients \
GET    /patients/{id} \ 
PUT    /patients/update/{id} \
GET    /patients/getPatientName/{patientId} 


### Staff Service

POST   /staff/register \
POST   /staff/auth/login \
GET    /staff \
GET    /staff/{staffId} \
GET    /staff/searchByStaffName?name={name} \
PUT    /staff/update/{staffId} 

POST   /staff/forgot-password \
POST   /staff/verify-otp \
POST   /staff/reset-password 

GET    /staff/getDoctorName/{doctorId} 


### Doctor Schedule

POST   /doctorSchedule/markavailable \
GET    /doctorSchedule/isDoctorAvailable 


### Appointment Service

POST   /appointments/bookAppointment \
GET    /appointments/{date} \
GET    /appointments/{doctorId}/{date} \
GET    /appointments/getDoctorPatients/{staffId} \
DELETE /appointments/{appointmentId} \
GET    /appointments/allFutureAppointments/{doctorId} \
PUT    /appointments/resheduleAppointmentDetails/{appointmentId} \
GET    /appointments/getAppointmentDetails/{appointmentId} \
POST   /appointments/submitDiagnosis/{appointmentId} 


### Bed & Room Service

POST   /bed/addBed \
GET    /bed/getBeds \
GET    /bed/room/{roomNumber} \
GET    /bed/vacant/room/{roomNumber} \
PUT    /bed/update-bed/{roomNumber}/{bedNumber} \
DELETE /bed/delete-bed/{bedNumber}/{roomNumber} 

POST   /bed/assign/{bedNumber}/{patientId} \
PUT    /bed/vacate-bed/{roomNumber}/{bedNumber} \
GET    /bed/bed-history/{bedNumber} 

POST   /rooms/addroom \
GET    /rooms/getAllRooms \
PUT    /rooms/update-rooms/{roomNumber} \
DELETE /rooms/delete-rooms/{roomNumber} 

### Notification Service

POST   /api/notifications/email 

> Request/response DTOs and required request fields should be checked in the corresponding controller/DTO classes before using these endpoints.

## Service-to-Service Communication

The application uses Spring Cloud OpenFeign for internal service communication.

Examples:

- AppointmentService → PatientService
- AppointmentService → StaffService
- AppointmentService → NotificationService
- StaffService → NotificationService

Service instances are discovered through Eureka rather than hard-coded service IP addresses.

## Project Structure

MedSync-Hospital-Management-System/ \
│ \
├── AppointmentService/ \
├── BedService/ \
├── EurekaServer/ \
├── GatewayService/ \
├── NotificationService/ \
├── PatientService/ \
└── StaffService/ 

Each service follows a layered structure similar to:

src/main/java/com/flmhospitals/ \
├── controller/ \
├── service/ \
├── service/impl/ \
├── dao/ \
├── dto/ \
├── model/ \
├── exception/ \
├── builder/ \
└── config/

## Prerequisites

Install:

- Java 17+
- Maven
- MySQL
- Git
- A SendGrid account/API key if email functionality is required

## Database Setup

Create the MySQL database:

CREATE DATABASE hospital_management;

The services currently use the hospital_management database.

Before running the project, update the database configuration in:

PatientService/src/main/resources/application.properties
StaffService/src/main/resources/application.properties
AppointmentService/src/main/resources/application.properties
BedService/src/main/resources/application.properties

Do not commit real database passwords or API keys to a public repository.

## SendGrid Configuration

NotificationService reads the SendGrid API key from:

SENDGRID_API_KEY

Set this environment variable before starting NotificationService.

Example:

SENDGRID_API_KEY=your_sendgrid_api_key

The sender email configured in the current project must also be a valid/verified SendGrid sender.

## Running the Application

Start the services in this order:

### 1. Eureka Server

cd EurekaServer

./mvnw spring-boot:run

Windows:

mvnw.cmd spring-boot:run

Eureka runs on:

http://localhost:8761

### 2. Start the application services

Run each service separately:

PatientService 

StaffService 

NotificationService

AppointmentService

BedService

### 3. Start GatewayService

GatewayService

The gateway runs on:

http://localhost:8000

Clients should use the gateway as the main entry point rather than directly calling individual services.

## Authentication Flow

1. Client sends login credentials to:

POST /staff/auth/login

2. StaffService validates the credentials.

3. A JWT is generated for an authenticated user.

4. The client sends the token with subsequent requests:

Authorization: Bearer <JWT>

5. GatewayService validates the JWT before forwarding secured requests.

6. StaffService also validates JWTs for requests reaching the service.

Some authentication and password-recovery endpoints are configured as public endpoints.

## Error Handling

Services use custom exceptions and global exception handlers for cases such as:

- Resource not found
- Duplicate appointments
- Invalid appointment times
- Unavailable beds
- Invalid OTPs
- Invalid or missing staff/doctor data

## Important Configuration & Security Notes

This repository is currently a development-oriented project. Before deploying it or keeping it public, review the following:

- Move MySQL usernames/passwords from `application.properties` into environment variables or a secrets manager.
- Replace the development JWT secrets with strong environment-specific secrets.
- Do not expose SendGrid credentials.
- Do not use real personal email addresses as application configuration.
- Review CORS settings before production deployment.
- Reduce verbose Feign logging before production.
- Use HTTPS in production.
- Review and restrict service-to-service authorization.

## Future Improvements

- Add Docker and Docker Compose for simplified deployment
- Add Swagger/OpenAPI documentation
- Add Resilience4j for fault tolerance
- Add integration tests for service-to-service communication
- Implement CI/CD with GitHub Actions
- Deploy the application to AWS

## Author

**Lokesh Perumandla**

GitHub: [github.com/lokeshperumandla25](https://github.com/lokeshperumandla25)
