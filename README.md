# Sample Inflate/Deflate Service

--- 
Solution to prompt - Take home: Generate a mvn / gradle base java micro-service to manage account balances for one or
more accounts. It should serve 2 actions: account inflation and deflation. Focus on the usecase above. No need for
concerns like security, circuit breakers, etc. Include instructions (readme) to startup the service and run tests. Feel
free to use a relational DB of your choice. I care more about submission depth.

## Technologies

![Java](https://img.shields.io/badge/Java-21%2B-orange?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1-green?logo=springboot&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-3.6%2B-blue?logo=apachemaven&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Containerized-blue?logo=docker&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-18%2B-blue?logo=postgresql&logoColor=white)

## Prerequisites

- Java 21+
- Docker
- **Optional** - API Client (Postman, Yaak, Insominia etc.). You can always use curl
- **Optional** - Database Management Tool (Dbeaver, Navicat, DataGrip etc.) I think some IDE's come inbuilt with them
- **Optional** - An IDE such as IntelliJ, VSCode, Netbeans

## Installation (With Git)

1. Clone the repository:
   ```bash
   git clone https://github.com/ZeleOeO/sample-account-app.git
   ```
2. Navigate to the project directory:
   ```bash
   cd sample_account_app
   ```   
3. Open docker if not already running (spring-boot-docker-compose is installed so no need for docker-compose)

## Running

Running this project is very simple:
<br>

```shell
mvn spring-boot:run
```

that's it, as long as you have docker installed and running

The db is hosted on docker, cause that's easier to set up than having pgadmin


## Architectural Decisions/Things I would Add

Some notes are on the service impl itself, but this is an overview

- I used a pessmistic write, with `SELECT.. FOR` because it's a small app, the other alternative would be @Version(optimistic), which is available for future integration.
    An issue with this approach is... locking is not really the best for concurrency as it can create latency. If an account is getting flooded with request (maybe paying school fees), but this is decent for a demo.
- Idempotency keys are unique to accounts, at first i wanted to make it just unique in general, but this is better. In some cases, it would be unique to the client data itself.
- There's a ledger, for now it's a single entry book-keeping strategy, just something simple, but double book keeping is most common in production systems, but this is really just an increase and decrease system. If one account inflated another and deflated itself then double entry would suffice.
- There are many more "next steps" that can be taken like adding an outbox table for reliable event publishing; move to a double-entry ledger; run a nightly reconciliation job comparing balances to ledger sums; add per-client rate limiting. All those fancy things.
- The brief specifies inflation and deflation as operations on an account, so this service is modeled that way. A transfer , where one account is deflated and another inflated atomically,  is a different use case: it needs a single transaction spanning two accounts. Transfer would be a new thing completely, so that's why no account is deflated when another is inflated. 

## Usage

- Database
    - To connect and check the database, it is hosted on `jdbc:postgresql://localhost:5433/sample_app-db`
    - username: `user`
    - password: `password`


- Application
    - port : `8080`


- API Endpoints
  <br>
  You can access the docs by checking:
    - [swagger-ui](http://localhost:8080/swagger-ui/index.html) for when the application is running

## Testing

It has unit tests for logic with account creation, inflate and deflate. Integration test for database writes on
transaction processing.

```shell
mvn test
```

## Steps to Contribute

Contributions are more than welcome, I'm still working on it though, so... keep that in mind or something.

1. Open an issue first so I can like keep track, but if that's too much stress that's fine too
2. Fork the Repository
3. Clone your fork
4. Create a new branch
   ```bash
   git checkout -b your-branch-name
   ```
5. Make your change
6. Commit your change, please
   use [Conventional Commits](https://gist.github.com/qoomon/5dfcdf8eec66a051ecd85625518cfd13) if you can, I didn't
   really use it here tbh
7. Push your change
8. Make a pull request and reference your issue <br>
   Please stick to conventional methods of programming java and springboot applications, don't mess up my already
   spaghetti code
