# Bookshelf

Aplicación Android con Kotlin y Jetpack Compose basada en el [proyecto Bookshelf de Android Developers](https://developer.android.com/codelabs/basic-android-kotlin-compose-bookshelf?hl=es-419).

## Funcionalidad

- Busca libros por título, autor o tema en Google Books; al abrir muestra una búsqueda de programación.
- Consulta `volumes?q=...` y luego `volumes/{id}` para cada resultado, con un máximo de cuatro consultas de detalles simultáneas.
- Muestra hasta 20 libros por búsqueda en una cuadrícula adaptable, con portada, título y autores.
- Descarga las portadas con Coil y convierte las URL HTTP de Google Books a HTTPS.
- Incluye estados de carga, resultados vacíos, error y reintento, además de un reemplazo para portadas no disponibles.
- Conserva la consulta al girar el teléfono y la restaura después de que Android recrea el proceso.

## Organización

`network/BooksApiService.kt` define los endpoints de Retrofit. `model/Book.kt` contiene los modelos JSON de Gson y el modelo de interfaz. `data/BooksRepository.kt` coordina la búsqueda y las consultas individuales. Si falla un detalle, conserva los datos de la búsqueda y avisa en la pantalla.

`data/AppContainer.kt` y `BookshelfApplication.kt` proporcionan inyección manual de dependencias. `ui/BookshelfViewModel.kt` administra las corrutinas, cancela búsquedas anteriores y expone el estado. `ui/BookshelfApp.kt` presenta la interfaz con Compose y observa el estado respetando el ciclo de vida.

## Compilación e instalación

Abre el proyecto en Android Studio y sincroniza Gradle. Conecta un teléfono con Android 11 (API 30) o posterior, activa la depuración USB, selecciona el dispositivo y ejecuta el módulo `app`.

Para generar únicamente el APK, sin ejecutar tests:

```powershell
.\gradlew.bat :app:assembleDebug
```

El APK se genera en `app/build/outputs/apk/debug/app-debug.apk`. La aplicación necesita Internet para consultar el catálogo y descargar las portadas.

## Cuota de Google Books

La configuración inicial usa las URL públicas del codelab. Si Google rechaza consultas sin identificación o agota la cuota compartida, se puede proporcionar una clave de un proyecto con Books API habilitada mediante la propiedad Gradle `BOOKS_API_KEY` o la variable de entorno del mismo nombre y recompilar. No es necesario modificar el código ni guardar la clave en el repositorio. Las respuestas HTTP 403 y 429 se muestran como un error de acceso/cuota; el botón Reintentar vuelve a solicitar la última búsqueda.

No se agregaron pruebas automatizadas para esta implementación, por indicación del usuario. Los archivos de ejemplo de pruebas de la plantilla original se conservaron.
