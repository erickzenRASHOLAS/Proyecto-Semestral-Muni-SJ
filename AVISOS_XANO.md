# Avisos de integración con Xano

## Qué conexión usa la aplicación

La aplicación utiliza el Swagger **TEVEO** y su URL base:

`https://x8ki-letl-twmt.n7.xano.io/api:PzS4KiRV/`

La capa se organiza siguiendo el ejemplo de clases: `remote/ApiService.kt`, `remote/RetrofitInstance.kt`, DTO, `repository/XanoRepository.kt`, repositorios de aplicación y ViewModels.

## Room y Xano coexisten

- Room conserva usuarios, casos y rutas de fotografías en el dispositivo.
- Xano recibe los perfiles relacionados y los casos compartidos.
- Un caso se guarda primero en Room. Si Xano no responde, queda local y se intenta sincronizar al volver a consultar los casos.
- Los IDs de Room no se reutilizan como IDs remotos: se guardan en `xanoId` y `xanoUsuarioId`.
- La migración Room 6 -> 7 agrega esos campos sin borrar los registros existentes.
- Las fotografías siguen siendo locales: la tabla `casos` de Xano no incluye una columna para subir archivos.

## Categorías y roles

Si faltan categorías, la app intenta crear las categorías usadas por el formulario. Si no existe el rol local del usuario en Xano, intenta crearlo antes de crear el perfil remoto. Comprueba en Xano que los endpoints GET/POST de `categorias_caso`, `roles` y `usuarios` estén habilitados y permitan las operaciones.

## Autenticación: grupos independientes

La autenticación utiliza el grupo `AuthUsuarios`, cuya base URL es:

`https://x8ki-letl-twmt.n7.xano.io/api:auth-usuarios/`

Los endpoints `POST /auth/signup`, `POST /auth/login` y `GET /auth/me` trabajan con la tabla `usuarios`. El registro admite los perfiles `Vecino` y `Municipal`; Xano resuelve el `roles_id` desde la tabla `roles`. La app conserva el JWT cifrado en Android Keystore y valida la sesión al abrir mediante `/auth/me`.

La API TEVEO mantiene su base URL `https://x8ki-letl-twmt.n7.xano.io/api:PzS4KiRV/` y continúa atendiendo los casos y catálogos. No se debe reemplazar una URL por la otra.

El Swagger actual de `AuthUsuarios` no contiene un endpoint para cambiar o recuperar la contraseña. La opción antigua de cambio de contraseña en el perfil no actualiza la contraseña remota y la aplicación muestra que esa operación todavía no está disponible.

## Antes de probar en Android Studio

1. Ejecuta Gradle Sync y compila la variante Debug.
2. Verifica que el workspace y el grupo API TEVEO sigan activos.
3. Revisa los permisos de los endpoints CRUD en Xano. El Swagger entregado los marca como "Authentication: not required"; para una publicación real hay que protegerlos con autenticación y permisos por rol.
4. Prueba primero registrando un usuario Vecino y otro Municipal desde AuthUsuarios; luego inicia sesión y comprueba `/auth/me`. Después crea un caso para validar la sincronización TEVEO.
5. Confirma que las opciones de los campos enum `prioridad` y `estado` coincidan exactamente con los valores que usa la app (por ejemplo, `Baja`, `Media`, `Alta`, `Urgente`, `En derivación` y `Cerrado`).
6. Si el caso informa que solo quedó en el dispositivo, revisa las validaciones de Xano, especialmente `edad`, `terminos_aceptados` y las relaciones `roles_id`, `usuarios_id` y `categoria_id`.

## Endpoints declarados en ApiService

Incluye GET de lista y detalle, POST, PATCH y DELETE para las seis tablas documentadas: casos, categorías, roles, usuarios, historial y observaciones. El flujo principal usa casos, categorías, roles, usuarios e historial. Las operaciones de observaciones quedan disponibles para integrar su pantalla.


## Diagnóstico del historial de cierre

- Al cerrar un caso, la app primero actualiza `casos` en Xano y después ejecuta `POST /historial_estados_caso`.
- Si Xano rechaza el POST, la pantalla de detalle ahora muestra el código HTTP y el mensaje de respuesta en lugar de ocultar el error.
- Comprueba en Xano el endpoint `POST /historial_estados_caso` con los campos `casos_id`, `estado_anterior`, `estado_nuevo`, `cambiado_por_usuario_id`, `motivo_cambio` y `fecha_cambio`.
- Verifica que los IDs `casos_id` y `cambiado_por_usuario_id` correspondan a registros existentes en las tablas referenciadas.


## Verificación del caso al sincronizar

- Después de `POST /casos`, la app conserva el ID remoto recibido para evitar crear duplicados si la consulta de confirmación sufre un corte de red.
- Luego consulta `GET /casos/{id}` y comprueba que el ID y el usuario creador coincidan.
- Si el caso remoto ya no existe (HTTP 404), se limpia el ID remoto local y se vuelve a crear al sincronizar. Otros errores HTTP no provocan una creación duplicada.
- Al cerrar, la app verifica el caso, ejecuta PATCH y revisa si el historial de ese cambio ya existe antes de hacer POST. Room confirma el estado solo después de completar el proceso.

La validación final debe hacerse en el workspace Xano real; el entorno de edición no tiene acceso a tu cuenta ni a sus datos.
