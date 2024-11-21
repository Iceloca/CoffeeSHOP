# ﻿Результаты тестирования

| Сценарий                                                               | Действие                                                                         | Ожидаемый результат                                                  | Фактический результат                                          | Оценка                |
|:-----------------------------------------------------------------------|:---------------------------------------------------------------------------------|:---------------------------------------------------------------------|:---------------------------------------------------------------|:----------------------|
| 1: Запуск приложения                                                   | Запустить приложение на устройстве Android                                       | Успешный запуск приложения                                           | Успешный запуск приложения                                     | Тест пройден          |
| 2: Отображение интерфейса                                              | Проверка соответствия интерфейса моккапам                                        | Интерфейс соответствует моккапам                                      | Частичное соответствие моккапам                                | Тест пройден частично |
| 3: Успешная регистрация пользователя                                   | Ввод корректных данных для регистрации нового пользователя в системе.            | Успешная регистрация пользователя                             | Успешная регистрация пользователя                              | Тест пройден          |
| 4: Неуспешная регистрация пользователя                                 | Ввод некорректных данных для регистрации нового пользователя в системе           | Пользователь не будет зарегистрирован и получит сообщение об ошибках | Пользователь не зарегистрирован и получил сообщение об ошибках | Тест пройден          |
| 5: Авторизация пользователя                                            | Ввод корректных данных для авторизации пользователя.                             | Успешная авторизация пользователя                             | Успешная авторизация пользователя                              | Тест пройден          |
| 6: Неуспешная авторизация пользователя                             | Ввод некорректных данных для авторизации пользователя.         | Пользователь не будет авторизован и получит сообщение об ошибках | Пользователь не авторизоваен и получил сообщение об ошибках | Тест пройден          |
| 7: Переход на главную страницу приложения при успешной регистрации | Произойдет переход на главную страницу приложения                                | Переход на главную страницу приложения произведен                    | Переход на главную страницу приложения произведен              | Тест пройден          |
| 8: Получить меню | При при переходе в меню пользователь увидит доступное меню | Открытие меню   | Открыто меню            | Тест пройден          |
| 9: Добавление продукта в зака | При нажатии на кнопку "BUY" продукт добавляется в корзину | Пользователь должен успешно добавить продукт | Пользователь успешно добавил продукт  | Тест пройден          |
| 10: Отправка сообщения в техподдержку| Отправка сообщения при нажатии на кнопку "Send" | Сообщение будет отправлено|  Сообщение было отправлено | Тест пройден          |
| 11: Удаление продукта из заказа| Удаление продукта при нажатии на кнопку "Delete" | Продукт должен пропасть из заказа, ассума должна пересчитаться |  ПРодукт удалился, сумма пересчиталась | Тест пройден    |
| 12: Отображение заказов| Отображение всех заказов пользователя | Все заказы должны отобразиться | Заказы отобразились| Тест пройден          |
