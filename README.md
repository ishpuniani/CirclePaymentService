# CirclePaymentsService (Java)

This is an environment check for a Circle Internet Financial interview problem in Java.  You have probably been given this folder in advance of an interview with Circle to make sure that the actual interview problem will run on your development machine during the interview.


## Checking your environment

Make sure you have the following prerequisites installed:
  - [Java 8 JDK & JRE](https://docs.oracle.com/javase/8/docs/technotes/guides/install/install_overview.html)
  - [Maven](https://maven.apache.org/install.html)
  - [Docker](https://docs.docker.com/install/)

Make sure you do not have anything running on ports 5432 or 8080.  Once you have done so, start the database with:
```bash
$ ./start_db.sh
```

Then, either use your IDE to build and run the program using main class `ServiceApplication.java`, or build and run it in a new terminal window:
```bash
$ ./start_server.sh
```

## Solution Architecture
![architecture](img/arch.png?raw=true "Architecture")

Building the APIs was easy.
The challenge was to come up with a solution to avoid double spends.

Here, the solution proposed is to write transactions directly into the "transactions" table with status "PENDING".
A background job `ProcessTransactionsJob` picks all the transactions that are in the "PENDING" state.
It processes each transaction serially, hence eliminating the double spend scenario.

We are processing the pending transactions every 30 seconds for now.
We may change the frequency of the job on the basis of the number of requests.

A sample of the system working: 
![Double Spend Example](img/doubleSpendScene.png?raw=true "Double Spend Example")
## APIs

### Accounts
#### Create Account:
Request: 
```shell script
curl --location --request POST 'localhost:8080/accounts' \
--header 'Content-Type: application/json' \
--data-raw '{
    "name":"jake",
    "email":"jake@email.com",
    "balance":10000.00
}'
```
Response: JSON object of the created account.
```json
{
    "id": "73a2d70e-09d8-4c4e-853b-c80d5d3dbb24",
    "created_at": "2020-07-27T04:13:11.594Z",
    "createdAt": 1595823191594,
    "name": "jake",
    "email": "jake@email.com",
    "balance": 10000.0
}
```

#### Get Account:
Request:  
```shell script
curl --location --request GET 'localhost:8080/accounts/73a2d70e-09d8-4c4e-853b-c80d5d3dbb24'
```

Response: JSON object of the account
```json
{
    "id": "73a2d70e-09d8-4c4e-853b-c80d5d3dbb24",
    "created_at": "2020-07-27T04:13:11.594Z",
    "createdAt": 1595823191594,
    "name": "jake",
    "email": "jake@email.com",
    "balance": 10000.0
}
```

#### Add Transaction
Request: 
```shell script
curl --location --request POST 'localhost:8080/transactions' \
--header 'Content-Type: application/json' \
--data-raw '{
    "senderId":"0dad946e-dee1-4ffa-9d47-53b0f0a77a7c",
    "receiverId":"73a2d70e-09d8-4c4e-853b-c80d5d3dbb24",
    "amount":100.0
}'
```
Response:
```json
{
    "id": "c9fb6212-3686-41cd-b3f7-28dc1ba8adb7",
    "sender_id": "0dad946e-dee1-4ffa-9d47-53b0f0a77a7c",
    "receiver_id": "73a2d70e-09d8-4c4e-853b-c80d5d3dbb24",
    "amount": 100.0,
    "status": "PENDING",
    "created_at": "2020-07-27T04:20:11.478Z"
}
```
#### Get Transaction
Request:
```shell script
curl --location --request GET 'localhost:8080/transactions/c9fb6212-3686-41cd-b3f7-28dc1ba8adb7'
```
Response: JSON object of the transaction
```json
{
    "id": "c9fb6212-3686-41cd-b3f7-28dc1ba8adb7",
    "sender_id": "0dad946e-dee1-4ffa-9d47-53b0f0a77a7c",
    "receiver_id": "73a2d70e-09d8-4c4e-853b-c80d5d3dbb24",
    "amount": 100.0,
    "status": "DONE",
    "created_at": "2020-07-27T04:20:11.478Z"
}
``` 