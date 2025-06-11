# Welcome to api-transaction-v1 👋


## Description
> This microservice is responsible for transferring money from one account to another.

## Getting Started:

## How to run the application

Right click Application.java class and choose <b>'Run Application.main()'</b> or <b>"Debug Application.main()"</b>

```
   api-transaction-v1
   └───src
       ├───main
          ├───java
              └───Application.java     
```

## Example curl request to test the API via postman
  ```
    curl --location 'http://localhost:8080/api/transfer' \
    --header 'Content-Type: application/json' \
    --data '{
    "senderAccountId": 1,
    "receiverAccountId": 2,
    "amount": 50,
    "currency": "USD"
    }'
    
  ```     

## Assumptions and Enhancements

- Added checking if the accounts exist in the database before processing the transaction.
- Added fx rates and transaction fee on the configuration file for easy modification.
- Added audit log to track the transactions.
    