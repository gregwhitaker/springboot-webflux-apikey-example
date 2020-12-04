# springboot-webflux-apikey-example
![build](https://github.com/gregwhitaker/springboot-webflux-apikey-example/workflows/Build/badge.svg)

An example of authenticating with a Spring Boot WebFlux application using an API key.

## Prerequisites
This example requires the following software to be installed before executing:

* [Docker Compose](https://docs.docker.com/compose/install/)

## Building the Example
Run the following command to build the example application:

    ./gradlew bootBuildImage

## Running the Example
Follow the steps below to run the example:

1. Run the following command to start the example application and database as Docker containers:

        docker-compose up
        
2. Run the following command to send a request to the non-secure endpoint:

        curl -v http://localhost:8080/api/v1/nonsecure

    If successful, you will receive an `HTTP 200 OK` response.

3. Run the following command to send a request to the secure endpoint:

        curl -v http://localhost:8080/api/v1/secure
        
    You will receive an `HTTP 401 Unauthorized` response because you have not supplied a valid API key.
    
4. Run the following command to send a request to the secure endpoint with an API key:

        curl -v --header "API_KEY: aec093c2-c981-44f9-9a4a-365ad1d2f05e" http://localhost:8080/api/v1/secure
        
    If successful, you will now receive an `HTTP 200 OK` response because you have supplied a valid API key.

## Bugs and Feedback
For bugs, questions, and discussions please use the [Github Issues](https://github.com/gregwhitaker/springboot-webflux-apikey-example/issues).

## License
Copyright (C) 2020 Greg Whitaker

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published
by the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
