# SuitcaseSmart — Contexto del Proyecto

## Stack real
- **Android nativo** con Kotlin + Jetpack Compose
- **Auth:** Firebase Authentication (email/password + Google Sign-In)
- **DB:** Cloud Firestore (listeners en tiempo real)
- **Push:** Firebase Cloud Messaging
- **Config remota:** Firebase Remote Config
- **Navegación:** Navigation Compose con bottom nav + nested graphs
- **Estado:** MVVM + StateFlow (sin DI/Hilt)

## Versiones actuales
- compileSdk: 36 | minSdk: 24 | targetSdk: 36
- versionCode: 11 | versionName: 1.0.9
- Kotlin: 2.3.20 | Compose BOM: 2026.03.00 | AGP: 9.1.0
- Firebase BOM: 34.11.0 | Navigation Compose: 2.8.8 | Coil: 3.4.0
- Java/JVM target: 17

## Estado actual del MVP — COMPLETO
- ✅ Auth email + Google Sign-In
- ✅ Onboarding + Splash
- ✅ CRUD maletas (swipe-to-delete, long-press para editar)
- ✅ CRUD items (swipe-to-delete, long-press para editar)
- ✅ Estados de items: por_empacar → empacado → usado
- ✅ Progreso de empaque por maleta (barra + contador en tiempo real)
- ✅ Modo checklist (filtro items "por empacar")
- ✅ Confirmación al borrar maleta/item (AlertDialog)
- ✅ Notas en items + timestamps creadaEn/creadoEn (v1.0.9)
- ✅ ProfileScreen con foto, nombre, email, sign-out
- ✅ Dark mode toggle persistido en SharedPreferences
- ✅ Firestore listeners en tiempo real (addSnapshotListener)

## Estructura de archivos clave
```
app/src/main/java/com/loptech/suitcasesmart/
├── MainActivity.kt                          # NavHost + bottom nav shell
├── MyApplication.kt
├── MyFirebaseMessagingService.kt
├── firebase/
│   ├── FirestoreDatabase.kt                 # Toda la lógica Firestore
│   └── GoogleAuthUiClient.kt               # Google Sign-In via CredentialManager
├── model/domain/
│   └── Viaje.kt                             # Maleta + Item (con @DocumentId)
├── navigation/AppScreens.kt                 # Rutas selladas
├── provider/preferences/PreferencesManager.kt
├── ui/theme/                                # Color.kt, Theme.kt, Type.kt
└── usecases/
    ├── home/Home.kt + HomeViewModel.kt      # Pantalla "Mis Maletas"
    ├── TravelDetail/SuitcaseDetail.kt + SuitcaseDetailViewModel.kt
    ├── checklist/ChecklistScreen.kt
    ├── profile/ProfileScreen.kt
    ├── login/ | registration/ | onboarding/ | launch/
    └── common/
        ├── MaletaDefaults.kt                # Colores/íconos por tipo
        ├── rows/MaletaRow.kt
        ├── views/AddMaletaSheetForm.kt      # Form agregar/editar maleta
        └── views/AddSuitcaseSheetForm.kt    # Form agregar/editar item
```

## Estructura de Firestore
```
users/{userId}/
  maletas/{maletaId}/
    nombre: string
    tipo: "carry-on" | "grande" | "mochila" | "personal"
    color: string (hex)
    icono: string
    items/{itemId}/
      nombre: string
      categoria: "ropa" | "electronica" | "documentos" | "higiene" | "medicamentos" | "otros"
      cantidad: number
      estado: "por_empacar" | "empacado" | "usado"
      notas: string
```

## Decisiones de arquitectura
- `@DocumentId val id: String = ""` en modelos (no `@get:Exclude`)
- `HomeViewModel` mantiene `itemListeners: Map<String, ListenerRegistration>` — uno por maleta
- `FirestoreDatabase` tiene helpers privados `userMaletas()` y `maletaItems()`
- `deleteMaleta` usa batch delete (items + doc) para evitar huérfanos
- Sign-out navega con `popUpTo(0) { inclusive = true }` para limpiar el back stack
- ViewModels cancelan sus listeners en `onCleared()`

## Comandos frecuentes
```bash
# Build debug
./gradlew assembleDebug

# Build release AAB (para Play Store)
./gradlew bundleRelease

# Verificar estado del proyecto
.\scripts\check-status.ps1
```

## Roadmap → ver ROADMAP.md
