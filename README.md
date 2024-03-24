
# IDFLab banking api

Микросервисная банковская api для обработки транзакций, парсинга курсов валюты, хранения и обработки информации о траназакциях.


## REST api

#### Сохранение транзакции по принятой банковской операции

```http
  GET /api/v1/debit-tr/receive-tr
```

| Body | Type     | Description                |
| :-------- | :------- | :------------------------- |
| `Информация о транзакции` | `json` | **Required** |

Вид json body транзакции:
```json
{
    "account_from": 123,
    "account_to": 9999999999,
    "currency_shortname": "USD",
    "sum": 2000,
    "expense_category": "product",
    "datetime": "2022-01-30 00:00:00+06"
}
```


#### Запрос всех транзакций, превысивших лимит

```http
  GET /api/v1/client-limits/show-exceed-transactions
```

Форма ответа:
```json
[ {
  "accountFrom" : 123,
  "accountTo" : 9999999999,
  "expenseCategory" : "PRODUCT",
  "sum" : 10000.45,
  "currency_shortname" : "KZT",
  "datetime" : "2024-04-05 06:00:00+06",
  "limit_sum" : 1000.0,
  "limit_datetime" : "2024-03-03 20:00:00Z",
  "limit_currency_shortname" : "USD"
}]
```

#### Задать новый лимит по транзакциям в USD

```http
  POST /api/v1/debit-tr//set-fresh-limit
```

| Param | Type     | Description                |
| :-------- | :------- | :------------------------- |
| `category` | `int` | **Required** 0\1 Категория транзакции
| `limit` | `double` | **Required** 0\1 Новый лимит



## Запуск

Для запуска необходима база данных postgres 15.2 Схема liquibase находится в common-resources/db/changelog.

Properties подключения к бд задаются в application.yml

Микросервисная структура сервиса предполагает, что api для клиентов и api для работы с валютами и транзакциями разделены на два модуля. Каждый может работать отдельно друг от друга.

Запуск возможен с помощью docker или классического maven build и java -jar, например в сессиях tmux.


```bash
  java -jar banking_module
  docker run banking_module
```
```bash
  java -jar client_module
  docker run client_module
```
    
## Документация
Оформлена с помощью javaDoc.


## Stack:
- Spring boot
- Spring cloud
- Postgres
- Docker
- Liquibase
- OpenFeign
- MapStruct
etc.
## Автор

- https://github.com/Tohtanbek

