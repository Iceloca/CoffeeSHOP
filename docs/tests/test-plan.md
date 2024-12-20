# ﻿Тест-план

## Содержание
- [Введение](#1-Введение)
- [Объект тестирования](#2-Объект-тестирования)
- [Риски](#3-Риски)
- [Аспекты тестирования](#4-Аспекты-тестирования)
- [Подходы к тестированию](#5-Подходы-к-тестированию)
- [Представление результатов](#6-Представление-результатов)
- [Выводы](#7-Выводы)

## 1 Введение

Тест-план описывает стратегию и подходы к тестированию приложения Coffee Shop. Основной целью тестирования является обеспечение соответствия приложения заявленным функциональным и нефункциональным требованиям. Тестирование будет включать в себя как функциональные, так и нефункциональные тесты, направленные на подтверждение качества и надежности приложения.

## 2 Объект тестирования
Объектом тестирования является приложение для Android, позволяющее пользователям делать заказы.

### Атрибуты качества
1. Функциональность: приложение должно выполнять все заявленные функции корректно
2. Удобство использования: интуитивно понятный интерфейс для пользователя
3. Надежность: приложение должно работать корректно без сбоев

## 3 Риски

К рискам можно отнести следующие факторы:
1. Проблемы совместимости с различными версиями Android
2. Потеря локальных данных при сбое мобильного устройства 

## 4 Аспекты тестирования

### Интерфейс

1. Корректное отображение всех элементов
2. Работа навигации

### Регистрация/авторизация пользователя

1. Проверка корректности данных введенных при регистрации/авторизации
2. Успешная регистрация/авторизация
3. Переход на главную страницу

### Работа отображения меню

1. Корректное отображение меню
2. Корректное добавление в корзину

### Работа корзины

1. Корректное добавление предметов
2. Корректный заказ
3. Корректное удаление продуктов

### Работа поддержки

1. Корректная отправка сообщения

### Работа отметки заказов

1. Корректное отображение всех заказов

## 5 Подходы к тестированию

При тестировании будут использованы:
1. Ручное тестирование
2. Модульное тестирование
3. Интеграционное тестирование
4. UI-тестирование

## 6 Представление результатов

[Результаты тестирования](test-result.md)

## 7 Выводы

Данный тестовый план позволяет проверить основной функционал приложения Coffee Shop. Успешное прохождение всех тестов не гарантирует полной работоспособности на всех устройствах и версиях Android, однако позволяет утверждать о корректной работе основных функций приложения. Тестирование должно проводиться регулярно при внесении существенных изменений в код проекта.
