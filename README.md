# ToDoDo

Android-приложение для управления задачами (to-do list). Создание, редактирование и удаление задач, приоритеты, сроки, отметка выполнения, фильтрация по вкладкам (Все / Активные / Сегодня / Просроченные), свайп для удаления с возможностью отмены.

Приложение построено на **View-based UI** (XML + ViewBinding, Material 3), архитектура **MVVM + Repository** с многомодульной структурой Gradle. Локальное хранение — Room3, асинхронность — Coroutines + Flow, внедрение зависимостей — Hilt.

> Ранее этот файл содержал план разработки. Текущая версия описывает фактическое состояние кодовой базы.

## Технологический стек

| Категория | Технология |
|---|---|
| Язык | Kotlin |
| UI | Views + ViewBinding, Material 3, ConstraintLayout, RecyclerView, ViewPager2 + TabLayout |
| Навигация | Jetpack Navigation Component (Safe Args) |
| Архитектура | MVVM (ViewModel + StateFlow / SharedFlow) + Repository |
| БД | Room3 (`androidx.room3`) |
| Асинхронность | Coroutines + Flow |
| DI | Hilt (обработка через KSP) |
| Сборка | Gradle (Kotlin DSL), Version Catalog (`gradle/libs.versions.toml`) |

- `minSdk` 24, `targetSdk`/`compileSdk` 37, Java 11
- `applicationId`: `ru.tododo`, namespace приложения: `ru.tododo.app`

## Возможности

- **CRUD задач**: создание, редактирование, удаление.
- **Поля задачи**: название (с валидацией — обязательно и не короче 5 символов), описание, срок (`dueDate`), приоритет (`LOW`/`MEDIUM`/`HIGH`), признак выполнения, временные метки `createdAt`/`updatedAt`.
- **Список задач** с сортировкой по выполненности и приоритету (`ORDER BY isCompleted, priority DESC`).
- **Фильтрация по вкладкам** через `ViewPager2` + `TabLayoutMediator`: Активные, Сегодня, Просроченные, Все. Логика фильтрации — на уровне `TaskRepository`.
- **Свайп для удаления** с Snackbar и кнопкой «Отмена» (undo-восстановление).
- **Уведомления** через Snackbar после сохранения / удаления.
- **Edge-to-edge** с применением системных insets.
- **Светлая / тёмная тема** (Material 3 DayNight).
- **Bottom navigation**: «Задачи» и «Настройки» (раздел настроек пока не реализован — присутствует только пункт меню).

## Архитектура

```
UI (Fragment + ViewBinding)
        ↓  (state / events)
ViewModel (StateFlow / SharedFlow)
        ↓
TaskRepository (interface, data:repository:api)
        ↓
TaskRepositoryImpl + Room (TaskDao / TaskEntity)
```

- **State** — `StateFlow<UiState>` для состояния экрана.
- **Events** — `SharedFlow<Event>` для одноразовых событий (навигация, показ Snackbar, открытие диалога).
- Fragment подписывается на state/events через `repeatOnLifecycle(STARTED)`.

## Структура модулей

```
ToDoDo/
├── app/                          # Точка входа: Application, MainActivity, навигация, тема
├── core/
│   ├── resources/                # Общие ресурсы и drawable
│   └── utils/                    # Расширения (snackbar, лог, EditText)
├── data/
│   ├── dto/                      # Доменные модели: Task, Priority
│   ├── repository/
│   │   ├── api/                  # Интерфейс TaskRepository, FilterType, ErrorCode, исключения
│   │   └── room/                 # Реализация на Room3: Entity, Dao, Database, mapper, DI
└── feature/
    └── tasks/                    # Фича задач: список и детали
        ├── list/                 # TaskListFragment/ViewModel, ViewPager, recycler, фильтр-вкладки
        └── details/              # TaskDetailsFragment/ViewModel, форма, диалоги приоритета/удаления
```

Зависимости модулей направлены внутрь: `feature:tasks` → `data:repository:api` + `data:dto` + `core`; `app` собирает всё вместе. Реализация Room подключается через Hilt-биндинг `RepositoryModule` (`@Binds TaskRepositoryImpl -> TaskRepository`), поэтому `feature` зависит только от контракта `api`.

## Модель данных

Доменная модель (`data/dto`):

```kotlin
data class Task(
    val id: UUID? = null,
    val title: String,
    val description: String?,
    val dueDate: Long?,
    val priority: Priority,          // LOW, MEDIUM, HIGH
    val isCompleted: Boolean = false,
    val createdAt: Long? = null,
    val updatedAt: Long? = null,
)
```

Сущность БД (`TaskEntity`, таблица `task`) хранит те же поля; `priority` сохраняется как `Int`. Маппинг — в `TaskMapper`. БД: `TododoDatabase`, версия 1, `exportSchema = true`.

## Фильтрация

`TaskRepository.FilterType` (`ALL`, `ACTIVE`, `TODAY`, `EXPIRED`) преобразуется в запросы DAO:

- `ALL` — все задачи;
- `ACTIVE` — невыполненные;
- `TODAY` — `dueDate` попадает в today (диапазон начало/конец дня в системном часовом поясе, с учётом API < O через `Calendar`);
- `EXPIRED` — `dueDate` раньше начала сегодняшнего дня.

## Навигация

`app/src/main/res/navigation/nav_graph.xml` включает граф фичи `feature_tasks_nav.xml`:

- `taskListFragment` (стартовый) → `action_taskListFragment_to_taskDetailsFragment`
- `taskDetailsFragment` принимает nullable `argument` `taskId` (`String`). `null` — режим создания, иначе — редактирование.

## Сборка и запуск

Требуется Android Studio с поддержкой AGP 9.x и JDK 11+.

```bash
# Установить debug-сборку на подключённое устройство/эмулятор
./gradlew :app:installDebug

# Собрать APK
./gradlew :app:assembleDebug

# Запустить unit-тесты
./gradlew test
```

> В `local.properties` должен быть указан путь к Android SDK (`sdk.dir`).

## Структура репозитория и git-флоу

Разработка ведётся ветками `feature/*`, `fix/*`, `style/*` слиянием через Pull Request в `main`. История коммитов соответствует соглашению Conventional Commits (`feat:`, `fix:`, `style:`, `refactor:`).

## Дорожная карта

Из реализованного плана остаются открытыми (на момент текущей версии `0.0`):

- Раздел «Настройки» (тема, сортировка по умолчанию, экспорт).
- Поиск по задачам.
- Категории / теги, подзадачи.
- Уведомления-напоминания о сроках (WorkManager / AlarmManager).
- Синхронизация с облаком.
- Тесты (unit для ViewModel / Repository, UI-тесты, интеграционные с in-memory Room).
- Полировка UI: единая палитра и типографика, MaterialYou / динамические цвета, индикация приоритета цветом, стилизация завершённых задач и фона свайпа.
