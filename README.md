# SecuritySJApp – Gestión de situaciones de seguridad comunal

MVP académico para la asignatura **DSY1105 – Desarrollo de Aplicaciones Móviles** (Duoc UC, Sede San Joaquín).
Caso: **SegurApp**, iniciativa de la Municipalidad de San Joaquín.

La app permite que un vecino registre una situación de seguridad comunal, la consulte y siga su estado hasta el cierre.
**Todos los datos son ficticios.** No se usan datos personales reales, credenciales ni información municipal.

## Integrantes

| Nombre | Usuario de GitHub |
|---|---|
|  Erick Rosales | erickzenRASHOLAS |
|  Brian Huentes | brhuenten        |

## Tecnologías

- Kotlin + Jetpack Compose 
- Patrón **MVVM** (`model`, `repository`, `viewModel`, `ui`)
- **Room** para persistencia local (usuarios y casos), con IDs remotos para sincronización
- Navigation Compose
- JWT de AuthUsuarios cifrado con Android Keystore para recordar y validar la sesión
- Recursos nativos: **cámara** (evidencia y foto de perfil) y **GPS** (ubicación del caso)
- Google Maps (`maps-compose`) para el mapa de casos
- **Retrofit + Gson** para consumir Xano (API TEVEO)
- **MockWebServer + JUnit** para pruebas de solicitudes HTTP

## Funcionalidades

- **Registro e inicio de sesión con Xano AuthUsuarios**. El usuario selecciona Vecino o Municipal; Xano valida las credenciales y asigna el rol. La sesión se recupera mediante `/auth/me` y el JWT se conserva cifrado en el dispositivo.
- Dos roles de referencia: **Vecino** y **Municipal**.
- **Menú de inicio** con el logo de la municipalidad y acceso a registrar caso, mis casos, todos los casos, mapa y perfil.
- **Registrar un caso**: categoría, descripción, ubicación, prioridad, evidencia fotográfica opcional. Al guardar, la app pide el permiso de ubicación y, si se acepta, guarda las coordenadas GPS del caso; si se rechaza, el caso se guarda igual pero solo aparece en la lista, no en el mapa.
- **Mis casos** y **Todos los casos**, con búsqueda por texto y filtro por categoría.
- **Mapa de casos**: un pin por cada caso con ubicación GPS (el color indica la prioridad); solo aparecen los casos con permiso de ubicación; al tocar un pin se ve un resumen y el acceso al detalle.
- **Detalle del caso**: estado, prioridad, categoría, ubicación, evidencia y quién lo creó.
- **Cierre de caso**: solo el creador del caso o un usuario Municipal puede cerrarlo.
- **Perfil**: foto con la cámara, cambio de nombre y cierre de sesión. El cambio remoto de contraseña queda pendiente de un endpoint específico en Xano.
- Modales (`AlertDialog`) al guardar un caso y al cambiar el nombre; la modificación remota de contraseña queda pendiente de un endpoint en Xano.
- Animación: la tarjeta de resumen del mapa aparece deslizándose desde abajo (`AnimatedVisibility`).

## Cómo ejecutar el proyecto

1. Clonar el repositorio:
   ```
   git clone https://github.com/erickzenRASHOLAS/Proyecto-Semestral-Muni-SJ.git
   ```
2. Abrir la carpeta con **Android Studio** y esperar el *Sync* de Gradle.
3. **API key de Google Maps (IMPORTANTE)**: la key no está en el repositorio por seguridad. Se debe buscar la API y agrégarla
   al archivo `local.properties` de la raíz del proyecto (ese archivo no se sube a GitHub por seguridad como se dijo anteriormente) 
   se debe verificar tener activado *Maps SDK for   Android*:
   ```
   MAPS_API_KEY=tu_api_key_aqui
   ```
   Sin la key la app funciona igual, pero el mapa se ve en gris.
4. Usar un emulador de Android o celular Android con **Google Play** o **Google APIs**, necesario para el GPS.
5. En el emulador, definir una ubicación: `...` > *Location* > escribir coordenadas > *Set Location*.
6. Ejecutar la app.
7. Registrar un usuario desde "¿No tienes cuenta? Regístrate aquí" e iniciar sesión.
8. Crear un caso para probar la sincronización con Xano. Revisa `AVISOS_XANO.md` si queda guardado solamente en Room.

Requisitos: `minSdk 24`, `compileSdk 37` y JDK 11 o superior.


## Estructura del proyecto

```
app/src/main/java/com/duoc/securitysjapp/
├── model/        Entidades Room (Usuario, Caso), DAOs y AppDatabase
├── remote/       ApiService, RetrofitInstance y DTO de Xano
├── repository/   Repositorios locales y XanoRepository
├── viewModel/    Lógica de pantalla y validaciones
├── ui/           Pantallas Compose y tema
└── MainActivity.kt   Navegación (NavHost)
```

## Conexión con Xano

La app utiliza dos grupos independientes. `AuthUsuarios` administra el registro, login y recuperación del perfil mediante JWT. La API TEVEO (`api:PzS4KiRV`) mantiene los endpoints de casos, categorías, roles, usuarios, historial y observaciones. Las dos bases URL se configuran separadamente en `remote/AuthRetrofitInstance.kt` y `remote/RetrofitInstance.kt`.

Los identificadores remotos quedan en `xanoId` / `xanoUsuarioId`, sin reemplazar las claves locales de Room. La ficha local contiene los datos de perfil necesarios para las pantallas y los casos; la contraseña no se guarda en Room. Consulta `AUTENTICACION_XANO.md` y `XANO_ENDPOINTS.md` para los detalles.

## Pruebas unitarias

Se agregaron pruebas JUnit con MockWebServer para comprobar GET de lista y detalle, POST de casos, PATCH de estado, POST de historial, propagación de errores HTTP y las rutas/cuerpos de AuthUsuarios:

```bash
./gradlew testDebugUnitTest
```

## Flujo del caso

`Registro del caso` → `En derivación` → `Cerrado`

## Límites y supuestos

- La app conserva Room como caché local y utiliza Xano para los datos compartidos. Si no hay conexión, los casos permanecen en Room y se reintentan al consultar la lista.
- Los JWT de AuthUsuarios tienen una duración actual de 24 horas. Al vencer, la app solicita iniciar sesión de nuevo con la cuenta existente.
- El rol se elige al registrarse (`Vecino` o `Municipal`) porque así está definido para esta versión académica. En un despliegue real, el rol Municipal debería asignarse mediante un proceso administrativo.
- El Swagger de AuthUsuarios todavía no expone cambio/recuperación de contraseña; esa función no queda sincronizada con la contraseña remota.

## Mejoras futuras

- Incorporar endpoints de Xano para cambiar y recuperar contraseñas.
- Incorporar pantallas para consultar y registrar observaciones de seguimiento; sus operaciones remotas ya están declaradas en el repositorio Xano.
- Mostrar el historial de cambios de estado en la pantalla de detalle; el cierre ya registra un evento remoto.
- Notificaciones al cambiar el estado de un caso.
- La adición de más estados (se está evaluando cuáles podrían ser).

## Gestión del proyecto

- Tablero Trello: https://trello.com/b/6abfa9047c2bf28c8a633aff
- Video de demostración: https://youtu.be/oMdiWkoXN98


## Integración con Xano

La conexión se implementa con Retrofit en `remote/` y se consume desde los repositorios. Se incluyen operaciones GET, POST, PATCH y DELETE documentadas en el Swagger TEVEO para casos, categorías, roles, usuarios, historial y observaciones. Consulta `XANO_ENDPOINTS.md` para ver el detalle.

Al registrar un caso, la aplicación lo guarda en Room, lo envía a `POST /casos` y confirma el ID con `GET /casos/{id}`. Al cerrarlo, actualiza el registro remoto y guarda el historial antes de confirmar el cambio en Room.

El registro y el inicio de sesión utilizan el grupo `AuthUsuarios` y la tabla `usuarios`. La sincronización de casos continúa usando `APITeveo`. Revisa `AUTENTICACION_XANO.md` para la configuración de JWT y los límites actuales.
