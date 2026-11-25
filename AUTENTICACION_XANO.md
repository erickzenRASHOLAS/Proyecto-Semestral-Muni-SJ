# Autenticación de SecuritySJApp con Xano

## Grupo y rutas

La autenticación utiliza el grupo `AuthUsuarios`, separado de la API TEVEO:

- Base URL: `https://x8ki-letl-twmt.n7.xano.io/api:auth-usuarios/`
- `POST auth/signup`: crea una cuenta en `usuarios`.
- `POST auth/login`: valida correo y contraseña y devuelve `authToken` y `user_id`.
- `GET auth/me`: recibe `Authorization: Bearer <token>` y devuelve el perfil autenticado.

El registro envía `nombre_completo`, `correo`, `contrasenia` y `perfil`. El campo `perfil` admite `Vecino` o `Municipal`; Xano resuelve el registro de `roles` y asigna `roles_id` en `usuarios`.

## Flujo de la aplicación

1. La pantalla Registro envía los datos al endpoint `signup`.
2. La pantalla Login envía `correo` y `contrasenia` a `login`.
3. Después de obtener el token, la app consulta `auth/me` para recuperar el perfil.
4. La app consulta `GET /roles` de APITeveo para traducir `roles_id` al nombre de perfil usado por las pantallas.
5. El perfil mínimo se conserva en Room para que las pantallas y los casos sigan trabajando con el ID local. La relación con Xano se conserva en `xanoId`.
6. El JWT se cifra con AES-GCM mediante una clave del Android Keystore y se conserva en preferencias privadas.
7. Al abrir la aplicación, se valida el JWT guardado mediante `auth/me`. Si Xano lo rechaza por vencimiento o autorización, se elimina la sesión y se muestra Login.

## Duración de sesión

El endpoint actual crea tokens con `expiration = 86400` (24 horas). Cuando el token vence, el usuario debe iniciar sesión nuevamente con su cuenta existente; no debe volver a registrarse.

## Contraseñas y datos locales

La contraseña se envía a Xano exclusivamente para signup/login y no se guarda en Room. La columna histórica `contrasena` de la entidad local se deja vacía al importar o actualizar un perfil autenticado, por compatibilidad con el esquema Room anterior.

El Swagger de `AuthUsuarios` no incluye un endpoint para cambiar la contraseña. La antigua acción de cambio de contraseña del perfil no actualiza la contraseña de Xano y no debe considerarse una operación remota.

## Endpoints TEVEO

La sincronización de casos y los catálogos continúa usando la API TEVEO:

`https://x8ki-letl-twmt.n7.xano.io/api:PzS4KiRV/`

No se cambia la base URL ni las operaciones de casos, categorías, roles, usuarios, historial y observaciones.

## Pruebas

Ejecutar todas las pruebas unitarias:

```bash
./gradlew testDebugUnitTest
```

Las pruebas de `AuthApiServiceTest` verifican las rutas, los campos JSON del registro/login y el encabezado Bearer de `/auth/me` mediante MockWebServer; no sustituyen la validación contra el workspace real de Xano.
