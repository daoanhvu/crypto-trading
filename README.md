# A SIMPLE CRYPTO-TRADING SYSTEM

## Introduction
This is a simple crypto-trading system.

## Prerequisites
- Java 17
- Maven 3.9.0 or higher
- Internet connection

## APIs
#### 1. Endpoint to get the best price by symbol
`curl --location 'http://localhost:1099/trading/prices/ETHUSDT'`
<br/>
The response should look like following:
```
{
    "result": {
        "symbol": "ETHUSDT",
        "bidPrice": 2939.05,
        "bidQty": 0.108,
        "askPrice": 2939.06,
        "askQty": 30.2281
    }
}
```
If the symbol not found the result will be:
```
{
    "message": "Price for TEST not found."
}
```

#### 2. Endpoint to get user's wallet
```
curl --location 'http://localhost:1099/trading/wallets/user1'
```
If the user exists then the response will look like:
```
{
    "result": {
        "username": "user1",
        "balance": 5000.0
    }
}
```

#### 3. Endpoint to do an exchange
```
curl --location 'http://localhost:1099/trading/transactions' \
--header 'Content-Type: application/json' \
--data '{
    "fromUser": "user1",
    "toUser": "user2",
    "amount": 500
}'
```
In the case of success, the response should look like following:
```
{
    "result": 1
}
```
Note that the result is the transaction ID

#### 4. Endpoint to retrieve user's transaction history
```
curl --location 'http://localhost:1099/trading/transactions?username=user1&page=0&size=25'
```
Response:
```
{
    "size": 25,
    "page": 0,
    "totalElements": 2,
    "totalPages": 1,
    "numberOfElements": 2,
    "content": [
        {
            "id": 1,
            "fromUser": "user1",
            "toUser": "user2",
            "amount": 500.0,
            "transactionTime": 1720401922.098699000
        },
        {
            "id": 2,
            "fromUser": "user1",
            "toUser": "user2",
            "amount": 340.0,
            "transactionTime": 1720401856.618207000
        }
    ]
}
```