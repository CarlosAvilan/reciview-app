# App Registro de Compras - Grupo ReciView

## Integrantes
* **Avilán, Carlos** - Legajo: 1190123
* **Mollo, Matías** - Legajo: 1190364
* **Pou, Iara** - Legajo: 1189360
* **Sicalo, Nicolás** - Legajo: 1162518

## Descripción del Proyecto
Aplicación móvil desarrollada en **Kotlin** y **Jetpack Compose** para la gestión inteligente de gastos personales o de negocios. La app permite escanear tickets mediante OCR (Reconocimiento Óptico de Caracteres), categorizar consumos y sincronizar datos con un backend en **Supabase** para filtrar consultas. También permite la gestión de gastos manuales, generar reportes mensuales permitiendo descargas de pdf  y comparar la evolución de los mismos.

## Tecnologías Utilizadas
* **Frontend:** Kotlin, Jetpack Compose, CameraX, ML Kit Text Recognition, Google Play Services Document Scanner API.
* **Backend:** Supabase
* **Arquitectura:** MVVM + Repository Pattern + Off-Line First

## Arquitectura del proyecto (Clean Architecture)
*   **`/data`**: Capa de datos e infraestructura.
    *   `enums/`: Clases con enums utilizados en el proyecto para el cambio de clave y sincronización de datos.
    *   `local/`: Persistencia local con **Room Database**, DAOs y SharedPreferences.
    *   `remote/`: Servicios de API (Retrofit) y DTOs para la comunicación con **Supabase**.
    *   `repository/`: Implementaciones que coordinan datos locales y remotos (Offline-first).
    *   `SessionManager.kt`: Gestión de sesión de usuario y tokens JWT.
*   **`/domain`**: Capa con lógica y reglas de negocio.
    *   `model/`: Modelos de dominio puros.
    *   `usecase/`: Casos de uso específicos (ej. `SaveCategoryUseCase`).
    *   `Managers`: Lógica independiente como `OcrManager`, `ReportCalculator` y `ReportPdfGenerator`.
*   **`/events`**: Eventos globales o de navegación unidireccional.
*   **`/navigation`**: Definición de rutas y grafos de navegación de la app.
*   **`/ui`**: Capa de presentación.
    *   `theme/`: Definiciones de colores, tipografías y el sistema de diseño (Material 3).
    *   `screens/`: Pantallas principales de la aplicación (Home, Login, Perfil, etc.).
    *   `components/`: Componentes de UI reutilizables (Botones, Cards, Dialogs).
    *   `viewmodel/`: Lógica de estado de la UI y comunicación con la capa de dominio.
*   **`/utils`**: Logica de funciones reutilizables en el sistema.
*   **`/scanner`**: Módulo especializado para la gestión del escaneo de documentos con Google ML Kit.

## Links Relevantes del Proyecto
**Figma:** https://figma.com/design/UGaNGMN3RpYT0Ih5KdzVsn/TPO-Desarrollo-de-Apps--ReciView-?node-id=20-195&t=ouQJbVBb5mHUp9tv-0
**Documentación:** https://docs.google.com/document/d/1l8IyWbx-SBBoKI04OLQZTpOVLu7QRUr6EeBFRE1e2PE/edit?usp=sharing
**Supabase:** https://supabase.com/dashboard/project/rzebtxdswubvzbgwkvab/editor/17590?schema=public
**Trello:** https://trello.com/b/ZQ16PSeX/reciview-tpo
