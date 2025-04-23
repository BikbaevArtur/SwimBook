# REST API для организации работы бассейна

👉 [Открыть Swagger UI](http://localhost:8080/swagger-ui/index.html)  
(Перед открытием, запустить приложение)

---

# Обязательные требования к реализации:

| Требования                                                             | Выполнение |      Классы реализации бизнес логики       | Тестирование                                       |
|:-----------------------------------------------------------------------|:----------:|:------------------------------------------:|----------------------------------------------------|
| Возможность записаться только на рабочее время                         |     ✅      | [TimeTableService] <br/> [ScheduleService] | [TimeTableServiceTest] <br/> [ScheduleServiceTest] |
| Ограничение на количество записей в час (допустим не более 10 человек) |     ✅      |             [TimeTableService]             | [TimeTableServiceTest]                             |
| Описание структуры таблиц в БД                                         |     ✅      |              [01_schema.sql]               |                                                    |

# Необязательные пожелания:

| Требования                                                                                     | Выполнение |            Классы реализации логики             | Классы тестирования                                     |
|------------------------------------------------------------------------------------------------|:----------:|:-----------------------------------------------:|---------------------------------------------------------|
| Поиск записей (по ФИО, дате посещения)                                                         |     ✅      |               [TimeTableService]                | [TimeTableServiceTest]                                  |
| Отдельные графики для праздничных дней                                                         |     ✅      | [HolidayScheduleService] <br/>[ScheduleService] | [HolidayScheduleServiceTest] <br/>[ScheduleServiceTest] |
| Ограничение на количество записей в день на человека (допустим не более 1-го посещения в день) |     ❌      |                                                 |                                                         |
| Возможность записи на несколько часов подряд                                                   |     ✅      |               [TimeTableService]                | [TimeTableServiceTest]                                  |

# Требования к API

| Требования                 | Выполнение |      Controller       | Service            | Test                   |
|----------------------------|:----------:|:---------------------:|--------------------|------------------------|
| API для работы с клиентами |     ✅      |  [ClientController]   | [ClientService]    | [ClientServiceTest]    |
| API для работы с записями  |     ✅      | [TimeTableController] | [TimeTableService] | [TimeTableServiceTest] |

# Слой бизнес логики

|         Service          |             Test             | Описание                               |
|:------------------------:|:----------------------------:|----------------------------------------|
|     [ClientService]      |     [ClientServiceTest]      | управление клиентскими данными         |
|    [TimeTableService]    |    [TimeTableServiceTest]    | управление данными бронирования        |
|    [ScheduleService]     |    [ScheduleServiceTest]     | составление графика работы             |
|  [WorkScheduleService]   |  [WorkScheduleServiceTest]   | управление данными по рабочим дням     |
| [HolidayScheduleService] | [HolidayScheduleServiceTest] | управление данными по праздничным дням |

---

# API для работы с клиентами

### 🔹 Получение списка клиентов

**GET** `/api/v0/pool/client/all`  
Параметры запроса: отсутствуют

Пример ответа:

```json
[
  {
    "id": 1,
    "name": "Иван Иванов"
  },
  {
    "id": 2,
    "name": "Анна Смирнова"
  }
]
```

---

### 🔹 Получение данных о клиенте

**GET** `/api/v0/pool/client/get`  
Пример запроса: `/api/v0/pool/client/get?id=1`

Пример ответа:

```json
{
  "id": 1,
  "name": "Иван",
  "phone": "+77777777777",
  "email": "ivan@mail.ru"
}
```

---

### 🔹 Добавление нового клиента

**POST** `/api/v0/pool/client/add`  
Тело запроса:

```json
{
  "name": "Иван",
  "phone": "+77777777777",
  "email": "ivan@mail.ru"
}
```

---

### 🔹 Обновление данных клиента

**POST** `/api/v0/pool/client/update`  
Тело запроса:

```json
{
  "id": 1,
  "name": "Иван",
  "phone": "+79876666666",
  "email": "ivanushka@mail.ru"
}
```

---
---

# API для работы с записями

### 🔹 Получение занятых записей на определенную дату

**GET** `/api/v0/pool/timetable/all`  
Параметры:

- `date`: строка (в формате yyyy-MM-dd)
- пример `/api/v0/pool/timetable/all?date=2025-04-21`

Пример ответа:

```json
[
  {
    "time": "10:00",
    "count": 3
  },
  {
    "time": "11:00",
    "count": 2
  }
]
```

---

### 🔹 Получение доступных записей на определенную дату

**GET** `/api/v0/pool/timetable/available`  
Параметры:

- `date`: строка (в формате yyyy-MM-dd)
- пример `/api/v0/pool/timetable/all?date=2025-04-21`

Пример ответа:

```json
[
  {
    "time": "14:00",
    "count": 5
  },
  {
    "time": "15:00",
    "count": 4
  }
]
```

---

### 🔹 Добавить запись клиента на определенное время

**POST** `/api/v0/pool/timetable/reserve`  
Тело запроса:

```json
{
  "clientId": 1,
  "datetime": "2025-05-10T12:00:00"
}
```

Пример ответа:

```json
{
  "orderId": "string"
}
```

---

### 🔹 Бронирование на несколько часов подряд

**POST** `/api/v0/pool/timetable/reserve-multiple`  
Тело запроса:

```json
{
  "clientId": 1,
  "date": "2025-04-21",
  "startTime": "12:00",
  "durationHours": 3
}
```

Пример ответа:

```json
[
  {
    "orderId": "string"
  }
]

```

---

### 🔹 Отмена записи клиента на определенное время

**POST** `/api/v0/pool/timetable/cancel`  
Тело запроса:

```json
{
  "clientId": 1,
  "orderId": "string"
}
```

---

### 🔹 Поиск записей по ФИО и дате посещения

**POST** `/api/v0/pool/timetable/visits/search`  
Тело запроса:

```json
{
  "name": "Иван",
  "date": "2025-04-21"
}
```

Пример ответа:

```json
[
  {
    "orderId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
    "time": "14:30:00"
  }
]

```

[TimeTableController]: src/main/java/ru/bikbaev/swimbook/timeTable/controller/TimeTableController.java

[TimeTableService]: src/main/java/ru/bikbaev/swimbook/timeTable/service/TimeTableService.java

[TimeTableServiceTest]: src/test/java/ru/bikbaev/swimbook/timeTable/service/TimeTableServiceTest.java

[ScheduleService]: src/main/java/ru/bikbaev/swimbook/schedule/service/ScheduleService.java

[ScheduleServiceTest]: src/test/java/ru/bikbaev/swimbook/schedule/service/ScheduleServiceTest.java

[HolidayScheduleService]: src/main/java/ru/bikbaev/swimbook/schedule/service/HolidayScheduleService.java

[HolidayScheduleServiceTest]: src/test/java/ru/bikbaev/swimbook/schedule/service/HolidayScheduleServiceTest.java

[WorkScheduleService]: src/main/java/ru/bikbaev/swimbook/schedule/service/WorkScheduleService.java

[WorkScheduleServiceTest]: src/test/java/ru/bikbaev/swimbook/schedule/service/WorkScheduleServiceTest.java

[ClientController]: src/main/java/ru/bikbaev/swimbook/client/controller/ClientController.java

[ClientService]: src/main/java/ru/bikbaev/swimbook/client/service/ClientService.java

[ClientServiceTest]: src/test/java/ru/bikbaev/swimbook/client/service/ClientServiceTest.java

[01_schema.sql]: db/01_schema.sql
