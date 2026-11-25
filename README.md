# SecuritySJApp - Gestión de situaciones de seguridad comunal

**Entrega de la Experiencia 3**  
**Asignatura:** DSY1105 - Desarrollo de Aplicaciones Móviles  
**Institución:** Duoc UC, Sede San Joaquín  
**Caso:** SegurApp, iniciativa académica de gestión de situaciones de seguridad comunal para la Municipalidad de San Joaquín.

SecuritySJApp es una aplicación móvil Android que permite registrar situaciones de seguridad comunal, consultar los casos y seguir su estado hasta el cierre. El proyecto utiliza datos ficticios y no contiene credenciales ni información municipal real.

## Integrantes

| Integrante | Usuario de GitHub |
|---|---|
| Erick Rosales | [@erickzenRASHOLAS](https://github.com/erickzenRASHOLAS) |
| Brian Huentes | [@brhuenten](https://github.com/brhuenten) |

## Repositorio y gestión del proyecto

- **Repositorio GitHub:** [Proyecto-Semestral-Muni-SJ](https://github.com/erickzenRASHOLAS/Proyecto-Semestral-Muni-SJ)
- **Tablero Trello:** [Planificación de SecuritySJApp](https://trello.com/b/6abfa9047c2bf28c8a633aff)
- **Video de demostración:** [Ver demostración de la aplicación](https://youtu.be/oMdiWkoXN98)

El código fuente se encuentra actualizado en GitHub. El tablero Trello también fue actualizado para reflejar la planificación y el avance del equipo. El historial de GitHub permite revisar los commits registrados por los integrantes.

## Tecnologías y herramientas

- **Lenguaje:** Kotlin.
- **Interfaz:** Jetpack Compose y Material 3.
- **Arquitectura:** MVVM, separando `model`, `repository`, `viewModel` y `ui`.
- **Persistencia local:** Room para los datos y la caché local.
- **Navegación:** Navigation Compose.
- **Comunicación HTTP:** Retrofit y Gson.
- **Backend y APIs:** Xano, mediante los grupos `AuthUsuarios` y `APITeveo`.
- **Sesión:** JWT almacenado cifrado mediante Android Keystore.
- **Mapas y ubicación:** Google Maps Compose y servicios de ubicación de Google Play.
- **Pruebas:** JUnit, Kotlin Coroutines Test y MockWebServer.
- **Construcción:** Gradle Wrapper y Android Gradle Plugin.

Para este proyecto académico, Xano se utiliza como backend para los servicios de datos y autenticación, en sustitución de microservicios propios desarrollados con Spring Boot, de acuerdo con la alternativa definida para el equipo.

## Funcionalidades principales

### Registro, autenticación y perfiles

- Registro de cuentas mediante Xano AuthUsuarios.
- Selección de perfil durante el registro: `Vecino` o `Municipal`.
- Inicio de sesión mediante correo y contraseña.
- Validación de credenciales en Xano y recuperación del perfil con `/auth/me`.
- Conservación cifrada del token JWT en el dispositivo y recuperación de sesión al volver a abrir la aplicación mientras el token siga vigente.
- Cierre de sesión desde el perfil.
- Los roles se relacionan con la tabla `roles` de Xano mediante `roles_id`.

### Gestión de casos

- Registro de casos con categoría, descripción, ubicación, prioridad y evidencia fotográfica opcional.
- Consulta de los casos propios y del listado general.
- Búsqueda por texto y filtro por categoría.
- Vista de detalle con estado, prioridad, categoría, ubicación, evidencia y creador.
- Actualización del estado y cierre del caso. El cierre está disponible para el creador del caso o para un usuario con perfil Municipal.
- Sincronización de los casos con Xano, conservando Room como persistencia local y caché.
- Reintento de sincronización de registros locales pendientes cuando se vuelve a consultar la lista de casos.

### Cámara, GPS y mapa

- Uso de la cámara para adjuntar evidencia a un caso y actualizar la fotografía del perfil.
- Uso del GPS para guardar latitud y longitud al registrar un caso, si el usuario concede el permiso.
- Si el permiso de ubicación no se concede, el caso puede guardarse sin coordenadas.
- Visualización de los casos georreferenciados en Google Maps mediante marcadores cuyo color representa la prioridad.
- Al seleccionar un marcador se muestra un resumen del caso y el acceso a su detalle.

### Interfaz

- Menú principal con acceso al registro de casos, listados, mapa y perfil.
- Formularios y pantallas de navegación implementados con Jetpack Compose.
- Diálogos de confirmación y mensajes para operaciones principales.
- Animación de entrada de la tarjeta de resumen del mapa mediante `AnimatedVisibility`.

## Integración con Xano

La aplicación utiliza dos grupos de API independientes, con sus propias bases URL.

### AuthUsuarios - registro y autenticación

Base URL:

`https://x8ki-letl-twmt.n7.xano.io/api:auth-usuarios/`

| Método | Ruta | Descripción |
|---|---|---|
| POST | `/auth/signup` | Registra una cuenta en `usuarios`, recibe el perfil elegido y asigna `roles_id` según la tabla `roles`. Devuelve `authToken` y `user_id`. |
| POST | `/auth/login` | Valida el correo y la contraseña y devuelve `authToken` y `user_id`. |
| GET | `/auth/me` | Valida el Bearer JWT y devuelve el perfil autenticado sin exponer la contraseña. |

El grupo `AuthUsuarios` utiliza la tabla `usuarios`. El registro recibe `nombre_completo`, `correo`, `contrasenia` y `perfil`; este último admite los valores `Vecino` y `Municipal`. Xano resuelve el ID del rol y lo almacena en `usuarios.roles_id`.

### APITeveo - casos y datos relacionados

Base URL:

`https://x8ki-letl-twmt.n7.xano.io/api:PzS4KiRV/`

La capa `remote/ApiService.kt` declara operaciones GET, POST, PATCH y DELETE para los recursos `casos`, `categorias_caso`, `roles`, `usuarios`, `historial_estados_caso` y `observaciones_caso`.

Entre las operaciones utilizadas por los flujos principales de la aplicación se encuentran:

| Método | Ruta | Uso en la aplicación |
|---|---|---|
| GET | `/casos` | Obtener el listado de casos. |
| GET | `/casos/{casos_id}` | Consultar y verificar un caso específico. |
| POST | `/casos` | Crear un caso en Xano. |
| PATCH | `/casos/{casos_id}` | Actualizar el estado y los datos del caso. |
| POST | `/historial_estados_caso` | Registrar el cambio de estado. |
| GET | `/categorias_caso` | Obtener las categorías disponibles. |
| GET | `/roles` | Obtener los perfiles y relacionarlos con `roles_id`. |
| GET | `/usuarios` | Consultar los perfiles registrados en la API TEVEO. |

Por lo tanto, el proyecto integra más de siete operaciones de API y utiliza métodos GET y POST, además de PATCH y DELETE declarados en la capa remota.

### Sincronización y persistencia

- Room conserva los registros locales y sus identificadores locales.
- Los identificadores de Xano se almacenan en campos remotos como `xanoId` y `xanoUsuarioId`, sin reemplazar las claves primarias locales.
- Al registrar un caso, la aplicación lo guarda localmente, lo envía mediante `POST /casos` y verifica el registro remoto con `GET /casos/{id}`.
- Al cerrar un caso, la aplicación actualiza el caso en Xano y registra el movimiento en `historial_estados_caso`. El cambio local se confirma después de completar las operaciones remotas.
- Si no hay conexión, los datos locales se conservan y los casos pendientes pueden reintentarse al consultar el listado.

Los endpoints de `observaciones_caso` están declarados en la capa remota, pero la interfaz de registro y consulta de observaciones no forma parte de las funcionalidades terminadas de esta entrega.

La documentación técnica complementaria se encuentra en:

- [`AUTENTICACION_XANO.md`](AUTENTICACION_XANO.md)
- [`XANO_ENDPOINTS.md`](XANO_ENDPOINTS.md)
- [`AVISOS_XANO.md`](AVISOS_XANO.md)

## Estructura del proyecto

```text
app/src/main/java/com/duoc/securitysjapp/
├── model/          Entidades Room, DAOs y AppDatabase
├── remote/         Servicios Retrofit, DTO y configuración de Xano
├── repository/     Repositorios locales y remotos
├── viewModel/      Estado, lógica de pantalla y validaciones
├── ui/             Pantallas Compose y tema visual
└── MainActivity.kt Navegación principal (NavHost)
```

## Requisitos de ejecución

- Android Studio compatible con el Android Gradle Plugin utilizado por el proyecto.
- Android SDK con `compileSdk 37` y plataforma de ejecución compatible con `minSdk 24`.
- Gradle Wrapper incluido en el repositorio.
- La configuración de Gradle utiliza JDK 25 para el Gradle Daemon mediante `gradle/gradle-daemon-jvm.properties`; el código Java del módulo mantiene compatibilidad de compilación 11.
- Conexión a Internet para sincronizar Gradle y consumir las APIs de Xano.
- Emulador o dispositivo Android con Google Play Services para las funciones de ubicación y mapas.

### Configuración de Google Maps

La clave de Google Maps no se almacena en el repositorio. Debe configurarse en el archivo `local.properties` de la raíz del proyecto, junto con la ruta local del Android SDK:

```properties
MAPS_API_KEY=clave_de_google_maps
```

La clave debe tener habilitado **Maps SDK for Android** en Google Cloud. `local.properties` está excluido de Git por contener configuración específica del equipo y datos que no deben publicarse.

### Abrir y ejecutar la aplicación

1. Clonar el repositorio:

   ```bash
   git clone https://github.com/erickzenRASHOLAS/Proyecto-Semestral-Muni-SJ.git
   ```

2. Abrir en Android Studio la carpeta del proyecto y esperar la sincronización de Gradle.
3. Configurar `local.properties` con el Android SDK y `MAPS_API_KEY`.
4. Seleccionar un emulador o dispositivo Android.
5. Ejecutar la configuración `app` desde Android Studio.
6. Registrar una cuenta seleccionando `Vecino` o `Municipal`, iniciar sesión y probar el registro y consulta de casos.
7. Para probar el mapa en un emulador, definir una ubicación desde los controles extendidos del emulador.

### Ejecutar pruebas unitarias

En Windows PowerShell:

```powershell
.\gradlew.bat testDebugUnitTest
```

En macOS o Linux:

```bash
./gradlew testDebugUnitTest
```

Las pruebas de `CasoRemoteRepositoryTest` utilizan MockWebServer para validar la conversión de respuestas, el envío de casos, el registro del historial y la consulta de un caso por ID. `AuthApiServiceTest` comprueba las rutas y cuerpos de registro e inicio de sesión y el encabezado Bearer de `/auth/me`. También se mantiene la prueba básica `ExampleUnitTest`.

La ejecución de las pruebas unitarias se realizó correctamente en el entorno de desarrollo del equipo.

## Flujo principal de un caso

`Registro del caso` → `En derivación` → `Cerrado`

## Límites conocidos de esta versión

- Los tokens JWT generados por AuthUsuarios tienen una vigencia configurada de 24 horas. Al vencer, el usuario debe iniciar sesión nuevamente con su cuenta existente.
- El endpoint publicado de AuthUsuarios no incluye una operación de cambio o recuperación de contraseña. Por ello, el cambio de contraseña del perfil no se sincroniza con la contraseña remota de Xano.
- La interfaz de observaciones no está implementada en esta entrega, aunque sus operaciones están declaradas en la capa remota.
- Los casos sin coordenadas GPS se mantienen en los listados, pero no aparecen como marcadores en el mapa.
- Los roles `Vecino` y `Municipal` se pueden seleccionar durante el registro en esta versión académica, según la configuración actual de Xano.

## Estado de la entrega

- **Código fuente:** actualizado en el repositorio público de GitHub; los cambios fueron commiteados y pusheados.
- **Planificación:** tablero Trello actualizado.
- **Pruebas unitarias:** archivo `CasoRemoteRepositoryTest` reducido a cuatro pruebas y ejecución reportada correctamente; se incluyen además pruebas del servicio de autenticación.
- **Pruebas funcionales:** se verificaron los registros Vecino y Municipal, el inicio de sesión, la recuperación de sesión, el cierre de sesión y la sincronización de casos con Xano. También se probaron los flujos de cámara y GPS.
- **APK firmado en modo release:** pendiente de generar.
- **Prueba de instalación del APK release:** pendiente de realizar después de generar la firma.

## Firma y entrega del APK

El APK de entrega debe generarse en la variante `release` mediante **Build → Generate Signed Bundle / APK → APK** en Android Studio. La firma se configura con un archivo de clave `.jks`.

El archivo APK firmado y la evidencia de la configuración de firma forman parte de la preparación final de la entrega. La llave privada `.jks` y sus contraseñas deben conservarse de forma segura y no deben publicarse en el repositorio GitHub.

Una vez generado, el APK debe instalarse en un dispositivo o emulador y probarse nuevamente, verificando como mínimo el inicio de sesión, la recuperación de sesión, la creación y sincronización de un caso, la cámara, el GPS y el mapa. A la fecha de esta actualización, la generación y prueba del APK `release` están pendientes.
