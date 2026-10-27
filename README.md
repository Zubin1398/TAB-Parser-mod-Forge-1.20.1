<div align="center">
<br/>
<br/>

# 🧾 TAB Parser — экспорт списка игроков в Minecraft
**Совместим с** - [BrutForce-Minecraft-accounts-mod](https://github.com/Zubin1398/BrutForce-Minecraft-accounts-mod/tree/main) / [Step-by-Step-Checker](https://github.com/Zubin1398/Step-by-Step-Checker/tree/main)
</div>

<div align="center">

<img src="https://img.shields.io/badge/version-1.1.0-2ea043?style=for-the-badge" alt="Version 1.1.0"/>
<img src="https://img.shields.io/badge/Minecraft-1.20.1-62b47a?style=for-the-badge" alt="Minecraft 1.20.1"/>
<img src="https://img.shields.io/badge/Forge-47.1.3%2B-f16436?style=for-the-badge" alt="Forge 47.1.3+"/>
<img src="https://img.shields.io/badge/side-client--only-4c8dff?style=for-the-badge" alt="Client only"/>
<img src="https://img.shields.io/badge/output-txt-8b949e?style=for-the-badge" alt="TXT output"/>
<img src="https://img.shields.io/badge/license-MIT-2ea043?style=for-the-badge" alt="MIT License"/>

</div>

> **TAB Parser** — клиентский мод для Minecraft **1.20.1 (Forge)**, который по команде `.parse` выгружает текущий список игроков из TAB в локальный текстовый файл. Мод не устанавливается на сервер, не использует OCR и внешние сервисы, работает только у тебя на клиенте.

<div align="center">

<h3>⭐ Если мод выручил — поставь звезду репозиторию.</h3>

</div>

<br/>

## 🧭 Содержание

<table>
  <tr>
    <td align="right"><b>🚀 Старт</b></td>
    <td align="center"><a href="#quick-start">Быстрый старт</a></td>
    <td align="center"><a href="#installation">Установка</a></td>
    <td align="center"><a href="#usage">Использование</a></td>
  </tr>
  <tr>
    <td align="right"><b>📄 Данные</b></td>
    <td align="center"><a href="#output">Формат вывода</a></td>
    <td align="center"><a href="#limits">Ограничения</a></td>
    <td align="center"><a href="#build">Сборка</a></td>
  </tr>
</table>

<br/>

## ✨ Возможности

| | |
|---|---|
| 🧾 **Строгий формат** | Каждая строка — ровно две колонки: `ник \| донат`. |
| 🔤 **Чистое значение** | Из доната остаётся первое «слово» только из букв и цифр, без `★`, `~`, `/` и прочих символов. |
| 🎯 **Только TAB** | Парсится именно видимый список игроков из TAB, а не все, кто есть в пакете. |
| 🧠 **Префикс и суффикс** | Донат ищется и перед ником, и после него. |
| 🎨 **Очистка формата** | Minecraft-коды цвета и форматирования убираются. |
| 📁 **Один файл на сервер** | Папка `.minecraft/TabParser/`, имя файла — по MOTD, бренду или IP сервера. |
| 🔁 **Перезапуск вручную** | Парсинг запускается только командой `.parse`. Ничего не происходит само. |

### 🆕 Что нового в 1.1.0

- **Список из TAB, а не все игроки.** Парсер берёт только тех, кто реально виден в списке TAB.
- **Сортировка как в TAB.** Порядок совпадает с ванильным списком: зрители → команда → ник.
- **Донат из префикса и суффикса.** Теперь распознаются и варианты `ᴋʀᴀᴋᴇɴ Player`, и `Player ᴋʀᴀᴋᴇɴ`.
- **Только два значения в строке.** Мусорные символы и лишние слова отбрасываются.
- **Регистронезависимая команда.** `.PARSE` тоже работает.
- **Точное значение доната.** Регистр больше не принудительно приводится к верхнему.

<br/>

<a id="quick-start"></a>

## 🚀 Быстрый старт

1. Установи Forge для Minecraft **1.20.1**.
2. Скопируй `tabparser-1.1.0.jar` в `.minecraft/mods`.
3. Запусти Minecraft и зайди на сервер.
4. Дождись загрузки списка игроков.
5. Напиши в чат:
   ```text
   .parse
   ```
6. Открой `.minecraft/TabParser/<server>.txt`.

<br/>

<a id="output"></a>

## 📄 Формат вывода

Каждая строка — строго две колонки:

```text
ник | донат
```

Примеры:

```text
Player123 | VIP
Player456 | PREMIUM
Admin123 | ADMIN
v1nrete | ᴋʀᴀᴋᴇɴ
```

Значение — первое «слово» из доната: только буквы и цифры. Всё остальное (`★`, `~`, `/`, `banned for 1.2`, `Трапер`) отбрасывается.

Если донат не распознан — пишется `Не определён`. Если доната нет — `Нет доната`.

<br/>

<a id="limits"></a>

## ⚠️ Ограничения

- Мод видит только то, что сервер прислал клиенту.
- Скрытых игроков и скрытых префиксов нет в данных TAB, поэтому они не попадут в файл.
- Если донат состоит только из символов, он будет помечен как `Не определён`.
- Если в донате несколько «чистых» слов, останется только первое.

<br/>

<a id="installation"></a>

## 🛠 Установка

1. Установи Forge 47.1.3 или новее для Minecraft 1.20.1.
2. Скопируй `tabparser-1.1.0.jar` в `%APPDATA%\.minecraft\mods`.
3. Запусти Minecraft.

<br/>

<a id="usage"></a>

## 📦 Использование

1. Зайди на сервер.
2. Дождись, пока TAB-список заполнится.
3. Напиши в чат `.parse`.
4. Файл появится в `.minecraft/TabParser/<server>.txt`.

<br/>

<a id="build"></a>

## 🧰 Сборка

Требуется Java 17 или новее.

```powershell
.\gradlew.bat build
```

Готовый jar будет в:

```text
build/libs
```

<br/>

## 📜 Лицензия

MIT.
