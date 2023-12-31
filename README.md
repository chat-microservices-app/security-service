# security-service

This service is responsible for authentication and authorization of users.

## Implementation

It is implemented using Spring Boot and Spring Security with OAuth2.0


## Usage

It uses JWT tokens for authentication and authorization by checking the validity of the token and 
creating a custom authentication object with the user details.

The service exposes endpoints to log in , register and refresh the token.

It also serves as an authentication server for other services. It uses the following endpoints:

- api/v1/check-token - Checks the validity of the token and returns the user details if the token is valid.
