# 📻 Radio Streaming

App Android para escuchar radios online de todo el mundo. Busca emisoras, las filtra por país y género, guarda tus favoritas y las reproduce en streaming, con un **mini reproductor** que te acompaña por toda la app.

Tiene una identidad visual **azul y amarillo**, con un botón para cambiar entre **modo azul** y **modo amarillo**.

Construida con **Kotlin**, **Jetpack Compose**, **Clean Architecture + MVVM**, principios **SOLID** y patrones de diseño.

---

## 📑 Índice

1. [Funcionalidades](#-funcionalidades)
2. [Pan<img width="1080" height="2400" alt="1000083811" src="https://github.com/user-attachments/assets/8ab17369-28a0-4d8f-b3d7-53aa06c382fd" />
tallas](#-pantallas)
3. [Diseño visual](#-diseño-visual)
4. [Tecnologías](#️-tecnologías)
5. [Arquitectura](#️-arquitectura)
6. [Estado del reproductor compartido](#-estado-del-reproductor-compartido)
7. [SOLID y patrones de diseño](#-solid-y-patrones-de-diseño)
8. [API](#-api)
9. [Cómo correr el proyecto](#-cómo-correr-el-proyecto)
10. [Configuración del proyecto](#️-configuración-del-proyecto)
11. [Problemas comunes](#-problemas-comunes)
12. [Próximos pasos](#️-próximos-pasos)

---

## ✨ Funcionalidades

**Escuchar**
- Reproducción en streaming de emisoras reales (MP3, AAC y **HLS** `.m3u8`).
- **Mini reproductor** amarillo fijo en todas las pantallas, con play/pausa y acceso directo al reproductor completo.
- La emisora que está sonando se **resalta con borde amarillo** en la lista y en favoritos.
- La música **sigue sonando** al salir del reproductor completo.
- Control de volumen, indicador de carga (*buffering*) y etiqueta **EN VIVO**.

**Encontrar**
- Lista de emisoras desde la API pública **Radio-Browser** (las más populares primero) con contador en el encabezado.
- **Buscador** por nombre con *debounce* para no saturar la API.
- **Filtros** por país y género, combinables con el buscador.

**Guardar**
- **Favoritos** guardados en el celular (Room): se ven al instante y funcionan sin internet.
- Se guarda la emisora completa (nombre, género, país, URL), no solo su id.

**Experiencia**
- **Splash** a pantalla completa con animación (también cubre la zona de la cámara / *notch*).
- **Barra de navegación inferior flotante** tipo píldora (Emisoras / Favoritos).
- **Modo azul / modo amarillo** con la elección guardada (DataStore).
- **Manejo de errores**: sin internet, emisora caída o error desconocido, con botón **Reintentar**.

---

## 📱 Pantallas

| Pantalla | Qué hace |
|---|---|
| **Splash** | Logo animado a pantalla completa; pasa sola a Emisoras |
| **Emisoras** | Encabezado grande con contador, buscador, filtros de país y género, lista de tarjetas |
| **Favoritos** | Emisoras marcadas con el corazón, con el mismo estilo de tarjetas |
| **Reproductor** | Portada con pulso animado, nombre, género · país, volumen, favorito y flecha para volver |
| **Mini reproductor** | Barra amarilla sobre la barra inferior mientras algo está sonando |

> Agregá tus capturas en una carpeta `screenshots/` y enlazalas acá:
> `![Emisoras](screenshots/emisoras.png)`

<img width="108" height="240<img width="1080" height="2400" alt="1000083817" src="https://github.com/user-attachments/assets/2826dcce-10a2-4212-9ff4-4b4c40cfdbef" />
<img width="108" height="240" alt="1000083816" src="https://github.com/user-attachments/assets/cf32f945-e65d-41f1-8f00-6d7ecdc50312" />
<img width="108" height="240" alt="1000083815" src="https://github.com/user-attachments/assets/341a09d3-5cf0-41df-a4e2-427f8f1732dd" />
<img width="108" height="240" alt="1000083814" src="https://github.com/user-attachments/assets/8f9a1301-9300-42d7-81b0-aad5650d635c" />
<img width="108" height="240" alt="1000083813" src="https://github.com/user-attachments/assets/37e23598-434d-4f42-b04a-db72e66643b9" />
<img width="108" height="240" alt="1000083812" src="https://github.com/user-attachments/assets/6bad60d7-2eef-4bb3-8286-9cfe5d2e857e" />
" alt="1000083811" src="https://github.com/user-attachments/assets/75a2252f-3c4c-4064-9ecf-36018462e76f" />


---

## 🎨 Diseño visual

- **Dos esquemas de color** en `ui/theme`:
  - **Modo azul**: fondo azul, textos claros y acentos amarillos.
  - **Modo amarillo**: fondo amarillo, textos oscuros y acentos azules.
- Las pantallas usan `MaterialTheme.colorScheme` (no colores fijos), por eso todo cambia solo al alternar el modo.
- **Componentes reutilizables** en `presentation/components`:

| Componente | Uso |
|---|---|
| `ScreenHeader` | Título grande, subtítulo y botón circular para cambiar de modo |
| `StationItem` | Tarjeta redondeada con avatar, "género · país", etiqueta EN VIVO, corazón y borde si está sonando |
| `FilterDropdown` | Filtro en forma de píldora |
| `LivePill` | Etiqueta "EN VIVO" |
| `MiniPlayer` | Tarjeta amarilla con play/pausa |

- Esquinas muy redondeadas, tarjetas con fondo `surfaceVariant` y barra inferior flotante.

---

## 🛠️ Tecnologías

| Área | Librería |
|---|---|
| Lenguaje | Kotlin 1.9.24 |
| Interfaz | Jetpack Compose + Material 3 |
| Navegación | Navigation Compose |
| Inyección de dependencias | Hilt (con KSP) |
| Red | Retrofit 2.11 + OkHttp |
| JSON | Kotlinx Serialization |
| Base de datos local | Room |
| Preferencias | DataStore |
| Audio | Media3 ExoPlayer 1.4.1 (con módulo HLS) |
| Asincronía | Coroutines + Flow / StateFlow |

---

## 🏗️ Arquitectura

El proyecto sigue **Clean Architecture** en tres capas. Las capas de afuera dependen de las de adentro, nunca al revés (**inversión de dependencias**).

```
┌────────────────────────────────────────────────┐
│ presentation   Pantallas (Compose) + ViewModels│
├────────────────────────────────────────────────┤
│ domain         Modelos, casos de uso, contratos│  ← Kotlin puro
├────────────────────────────────────────────────┤
│ data           API, Room, ExoPlayer, DataStore │
└────────────────────────────────────────────────┘
```

**Flujo de datos** (MVVM con estado unidireccional):

```
Pantalla ──acción──▶ ViewModel ──▶ UseCase ──▶ Repository ──▶ API / Room
   ▲                    │
   └──── UiState ◀──────┘   (StateFlow)
```

### Estructura de carpetas

```
com.example.radiostreaming
├── data
│   ├── local         AppDatabase, FavoriteDao, FavoriteStationEntity, ThemeRepositoryImpl
│   ├── remote        RadioApiService, StationDto, CountryDto, TagDto, StationMapper
│   └── repository    StationRepositoryImpl
├── di                DatabaseModule, NetworkModule, PlayerModule, RepositoryModule
├── domain
│   ├── model         Station, ThemeMode
│   ├── player        RadioPlayer (+ PlaybackError), ExoPlayerRadioPlayer
│   ├── repository    StationRepository, StationFilter, ThemeRepository
│   └── usecase       GetStations, SearchStations, GetFavoriteStations,
│                     GetStationById, ToggleFavorite, GetFilterOptions
├── navigation        Screen, RadioNavGraph, RadioBottomBar
├── presentation
│   ├── components    StationItem, FilterDropdown, ScreenHeader, LivePill, MiniPlayer
│   ├── favorite      FavoriteScreen, FavoritesViewModel, FavoriteUiState
│   ├── player        PlayerScreen, PlayerViewModel, PlayerUiState, NowPlayingViewModel
│   ├── splash        SplashScreen
│   ├── stationlist   StationListScreen, StationListViewModel, StationListUiState
│   └── theme         ThemeViewModel
├── ui/theme          Color, Theme, Type
├── MainActivity
└── RadioApp          (@HiltAndroidApp)
```

> Nota: `ExoPlayerRadioPlayer` hoy vive en `domain/player`. Lo más prolijo sería moverlo a `data/player`, porque usa ExoPlayer (una librería de Android) y el `domain` debería ser Kotlin puro. Ver [Próximos pasos](#️-próximos-pasos).

### Navegación

```
Splash ──▶ Emisoras ◀──▶ Favoritos
              │             │
              └──▶ Reproductor ◀──┘
```

- `Screen` es una *sealed class* con las rutas (`Splash`, `StationList`, `Favorites`, `Player`).
- `RadioNavGraph` arma el `Scaffold`: abajo van el **mini reproductor** (si hay algo sonando y no estás en el reproductor) y la **barra inferior** (oculta en el splash).
- La barra inferior usa `popUpTo(StationList)` para no acumular pantallas al cambiar de pestaña.

---

## 🔊 Estado del reproductor compartido

Hay **un solo reproductor** (Singleton) para toda la app, y varias pantallas lo observan:

```
                    ┌──────────────────────┐
                    │ ExoPlayerRadioPlayer │  (Singleton)
                    │  currentStation      │
                    │  isPlaying           │
                    │  isBuffering         │
                    │  playbackError       │
                    └─────────┬────────────┘
          ┌───────────────────┼──────────────────┐
          ▼                   ▼                  ▼
  PlayerViewModel     NowPlayingViewModel     (otros)
  (pantalla completa) (mini reproductor y
                       borde "sonando")
```

- `RadioPlayer.play(station: Station)` recibe la emisora completa, así el reproductor sabe **qué está sonando** y todas las pantallas se enteran.
- `NowPlayingViewModel` expone `currentStation`, `isPlaying` y `togglePlayPause()` para el mini reproductor.
- `PlayerViewModel` **no** detiene el audio en `onCleared()`: por eso la música sigue al volver atrás.

---

## 🧩 SOLID y patrones de diseño

### SOLID

- **S**: cada caso de uso hace una sola acción; cada ViewModel maneja una sola pantalla.
- **O**: búsqueda, filtros, favoritos y mini reproductor se sumaron agregando clases, sin reescribir las existentes.
- **L / I**: las interfaces (`StationRepository`, `RadioPlayer`, `ThemeRepository`) son pequeñas y cualquier implementación las puede reemplazar.
- **D**: el `domain` define los contratos y `data` los implementa; los ViewModels nunca conocen Retrofit, Room ni ExoPlayer.

### Patrones

| Patrón | Dónde |
|---|---|
| **Repository** | Oculta si los datos vienen de la API o de Room |
| **Use Case** | Una clase por acción de negocio |
| **Dependency Injection** | Hilt con `@Binds` y `@Provides` |
| **Observer** | `Flow` y `StateFlow` para que la pantalla se actualice sola |
| **Mapper / DTO** | `StationDto` se traduce al modelo `Station` |
| **UiState** | Una clase que representa todo lo que muestra cada pantalla |
| **Singleton** | Un solo reproductor y una sola base de datos |
| **State hoisting** | Cada pantalla se divide en `Screen` (con ViewModel) y `Content` (UI pura, fácil de previsualizar) |

---

## 🌐 API

Usa [**Radio-Browser**](https://www.radio-browser.info/), una API pública y gratuita, **sin API key**. Base: `https://de1.api.radio-browser.info/`

| Endpoint | Uso |
|---|---|
| `json/stations/search` | Lista, búsqueda por nombre y filtros por país y género |
| `json/stations/byuuid/{uuid}` | Datos de una emisora puntual |
| `json/countries` | Países disponibles para el filtro |
| `json/tags` | Géneros disponibles para el filtro |

Parámetros usados en la búsqueda: `name`, `country`, `tag`, `limit`, `hidebroken`, `order`, `reverse`.

Notas:
- Cada emisora se identifica con un `stationuuid` (texto), no con un número.
- Se envía un `User-Agent` propio con un interceptor, como pide la documentación.
- Si el servidor `de1` no responde, se puede cambiar por otro mirror en `NetworkModule`.

---

## 🚀 Cómo correr el proyecto

### Requisitos

- Android Studio actualizado.
- JDK 17 (el que trae Android Studio).
- Un emulador o un celular con depuración USB.
- Conexión a internet.

### Pasos

1. Cloná el repositorio:
   ```bash
   git clone https://github.com/<tu-usuario>/radiostreaming.git
   ```
2. Abrilo con Android Studio (**File → Open**).
3. Esperá a que termine **Gradle Sync**.
4. Elegí un dispositivo y tocá **Run ▶️**.

---

## ⚙️ Configuración del proyecto

- **Permiso de internet** en `AndroidManifest.xml`:
  ```xml
  <uses-permission android:name="android.permission.INTERNET" />
  ```
- **Streams `http://`**: muchas radios transmiten sin cifrar, por eso la app declara:
  ```xml
  android:usesCleartextTraffic="true"
  ```
- **Plugin de serialización**: `kotlin("plugin.serialization")` con la **misma versión que Kotlin**, en el `build.gradle` raíz (`apply false`) y en el del módulo `app`.
- **Versiones compatibles**: con Kotlin `1.9.24`, `kotlinx-serialization-json` tiene que ser `1.6.3` (la `1.7.x` exige Kotlin 2.0+).
- **Media3**: todas las dependencias (`exoplayer`, `exoplayer-hls`, `ui`, etc.) con la **misma versión** (`1.4.1`).
- **Base de datos**: usa `fallbackToDestructiveMigration()`. Si cambiás las tablas de Room, **subí la versión** de `AppDatabase`.
- **Splash sin franja blanca**: `themes.xml` y `values-v28/themes.xml` definen `windowBackground` y `windowLayoutInDisplayCutoutMode = shortEdges`.

---

## 🧪 Problemas comunes

| Problema | Causa y solución |
|---|---|
| `Serializer for class 'StationDto' is not found` | Falta el plugin `kotlin("plugin.serialization")` o no coincide con la versión de Kotlin |
| `ClassNotFoundException: HlsMediaSource` | Falta `media3-exoplayer-hls` en las dependencias |
| La app se cierra al abrir una emisora | Revisá `Logcat` buscando `FATAL EXCEPTION` |
| "No se pudieron cargar las emisoras" | Mirá `Logcat` con el filtro `StationListVM` para ver el error real |
| "Emisora no encontrada" en el reproductor | La API necesita `json/stations/byuuid/{uuid}` con el id de texto |
| Favoritos no aparecen | Se guardan completos en Room; si cambiás la base, subí la versión de `AppDatabase` |
| `NullPointerException` en `Screen.getIcon()` | `bottomBarScreens` debe ser `get() = listOf(...)`, no un valor fijo |
| `Type mismatch: () -> Unit but NowPlayingViewModel was expected` | Al llamar `RadioNavGraph`, usá el nombre del parámetro: `RadioNavGraph(onToggleTheme = { ... })` |
| `Unresolved reference: currentStation` / `togglePlayPause` | `RadioPlayer` o `NowPlayingViewModel` desactualizados, o el parámetro `nowPlayingViewModel` tiene un tipo equivocado |
| Errores de `override` en `ExoPlayerRadioPlayer` | La interfaz `RadioPlayer` debe declarar `currentStation` y `play(station: Station)` |

**Consejos para Android Studio**
- Corregí **siempre el primer error**: los demás suelen ser consecuencia.
- Cuidado con las sugerencias rápidas (Alt + Enter) como *Rename reference*: pueden cambiar tu código sin que lo notes.
- Después de muchos cambios, usá **Build → Rebuild Project**.

---

## 🗺️ Próximos pasos

- [ ] Reproducción en segundo plano con servicio y notificación multimedia.
- [ ] Mover `ExoPlayerRadioPlayer` a `data/player` para mantener el `domain` puro.
- [ ] Tests unitarios de ViewModels y casos de uso.
- [ ] Paginación de la lista de emisoras.
- [ ] Reintentos automáticos si se corta el stream.
- [ ] Recordar la última emisora escuchada.
- [ ] Ícono propio de la app (logo de radio azul y amarillo).

---

## 📄 Licencia

Proyecto personal de aprendizaje. Definí acá la licencia que prefieras (por ejemplo, MIT).

---

Hecho con Kotlin y Jetpack Compose 💙💛
