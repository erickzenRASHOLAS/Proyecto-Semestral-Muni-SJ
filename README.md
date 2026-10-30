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
- **Room** para persistencia local (usuarios y casos)
- Navigation Compose
- `SharedPreferences` para recordar la sesión iniciada
- Recursos nativos: **cámara** (evidencia y foto de perfil) y **GPS** (ubicación del caso)
- Google Maps (`maps-compose`) para el mapa de casos

## Funcionalidades

- **Registro e inicio de sesión** con validación por campo (los datos de usuario se guardan en Room). La sesión se mantiene al cerrar la app (`SharedPreferences`) hasta que el usuario cierra sesión.
- Dos roles de referencia: **Vecino** y **Municipal**.
- **Menú de inicio** con el logo de la municipalidad y acceso a registrar caso, mis casos, todos los casos, mapa y perfil.
- **Registrar un caso**: categoría, descripción, ubicación, prioridad, evidencia fotográfica opcional. Al guardar, la app pide el permiso de ubicación y, si se acepta, guarda las coordenadas GPS del caso; si se rechaza, el caso se guarda igual pero solo aparece en la lista, no en el mapa.
- **Mis casos** y **Todos los casos**, con búsqueda por texto y filtro por categoría.
- **Mapa de casos**: un pin por cada caso con ubicación GPS (el color indica la prioridad); solo aparecen los casos con permiso de ubicación; al tocar un pin se ve un resumen y el acceso al detalle.
- **Detalle del caso**: estado, prioridad, categoría, ubicación, evidencia y quién lo creó.
- **Cierre de caso**: solo el creador del caso o un usuario Municipal puede cerrarlo.
- **Perfil**: foto con la cámara, cambio de nombre, cambio de contraseña y cierre de sesión.
- Modales (`AlertDialog`) al guardar un caso y al cambiar el nombre o la contraseña.
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
5. Usar un emulador de Android o celular Android (valga la redundancia) con **Google Play** o **Google APIs**, necesario para el GPS.
6. En el emulador, definir una ubicación: `...` > *Location* > escribir coordenadas > *Set Location* en mi emulador puse las cordenadas de DuocUC.
7. Ejecutar la app.
8. Registrar un usuario desde "¿No tienes cuenta? Regístrate aquí" e iniciar sesión.

Requisitos: `minSdk 24`, `compileSdk 37(importante esto fue pedido por el profesor en multiples clases)`, JDK 11 o superior.


## Estructura del proyecto

```
app/src/main/java/com/duoc/securitysjapp/
├── model/        Entidades Room (Usuario, Caso), DAOs y AppDatabase
├── repository/   UsuarioRepository, CasoRepository
├── viewModel/    Lógica de pantalla y validaciones
├── ui/           Pantallas Compose y tema
└── MainActivity.kt   Navegación (NavHost)
```

## Flujo del caso

`Registro del caso` → `En derivación` → `Cerrado`

## Límites y supuestos

- MVP académico: no hay conexión con sistemas municipales ni servidor: todo se guarda en el dispositivo y ademas son casos creados por uno mismo(desarrolladores).
- Las contraseñas se guardan sin cifrar porque los datos son de prueba. En una versión real habría que cifrarlas y autenticar contra un servidor, una tecnica sugerida seria el Hashing(vista en clases de machine learning por Erick Rosales).
- El rol se elige al registrarse: no hay un administrador que lo asigne, esto tambien es por ser una versión de desarrollo, en una versión oficial esto seria una gran brecha de seguridad

## Mejoras futuras

- Derivar un caso a un rol responsable (operador, supervisor) y registrar observaciones de seguimiento.
- Historial de cambios de estado (trazabilidad)
- Notificaciones al cambiar el estado de un caso.
- Conexión con una API de prueba.
- La adición de mas estados (Se esta evaluando cuales podrían ser)

## Gestión del proyecto

- Tablero Trello: https://trello.com/invite/b/6abfa9047c2bf28c8a633aff/ATTI37e2101929948db9395b639a87df8721DBE0F685/tablero-proyecto-semestral-muni-sj 
- Video de demostración: https://youtu.be/oMdiWkoXN98
