# Endpoints Xano integrados

Base URL TEVEO: `https://x8ki-letl-twmt.n7.xano.io/api:PzS4KiRV/`.

Base URL de autenticación: `https://x8ki-letl-twmt.n7.xano.io/api:auth-usuarios/` (grupo `AuthUsuarios`).

La interfaz `ApiService` declara las operaciones documentadas en el Swagger para las cinco tablas:

| Recurso | GET lista/detalle | POST | PATCH | DELETE |
|---|---:|---:|---:|---:|
| `casos` | Sí | Sí | Sí | Sí |
| `categorias_caso` | Sí | Sí | Sí | Sí |
| `roles` | Sí | Sí | Sí | Sí |
| `usuarios` | Sí | Sí | Sí | Sí |
| `historial_estados_caso` | Sí | Sí | Sí | Sí |
| `observaciones_caso` | Sí | Sí | Sí | Sí |

La app usa directamente los endpoints de casos, categorías, roles, usuarios e historial. Los endpoints de observaciones quedan disponibles en `XanoRepository` para la funcionalidad de observaciones.

## Autenticación AuthUsuarios

| Endpoint | Uso | Autenticación |
|---|---|---|
| `POST /auth/signup` | Registro con `nombre_completo`, `correo`, `contrasenia` y `perfil` (`Vecino` o `Municipal`) | No requerida |
| `POST /auth/login` | Validación de correo y contraseña; devuelve `authToken` y `user_id` | No requerida |
| `GET /auth/me` | Recuperación del perfil autenticado | Bearer JWT |

La autenticación usa la tabla `usuarios`. La app guarda el token cifrado mediante Android Keystore. Consulta `AUTENTICACION_XANO.md` para el flujo de restauración de sesión.

## Reglas importantes

- La contraseña se envía solamente a AuthUsuarios en los flujos de registro/login; no se guarda en Room.
- El endpoint TEVEO `POST /usuarios` sigue reservado para la compatibilidad de sincronización de perfiles locales antiguos; los usuarios registrados por AuthUsuarios ya tienen su `xanoId` y no se duplican.
- Al crear un caso, la app guarda primero en Room, hace `POST /casos` y consulta `GET /casos/{id}` para verificar que Xano devolvió y conserva el registro.
- Al cerrar un caso, verifica el caso remoto, ejecuta `PATCH /casos/{id}` y registra el historial. Room cambia el estado solo después de confirmar ambos pasos.
- Los listados conservan los datos locales si Xano no está disponible; los casos pendientes vuelven a intentarse al consultar la lista.

## Verificación pendiente en el workspace

La compilación y las pruebas deben ejecutarse en Android Studio con el SDK y las dependencias disponibles. La cuenta/workspace real de Xano no es accesible desde este entorno, así que se debe validar allí que los endpoints estén publicados y que los campos/referencias coincidan con el Swagger.
