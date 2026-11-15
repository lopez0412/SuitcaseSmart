# Paridad SuitcaseSmart: Android → iOS

Estado de la app Android al 15 nov 2026, para compararla con la versión iOS.
Llena la columna **iOS** con: ✅ Hecho · ⚠️ Parcial · ❌ No existe · ➖ No aplica.

- **Android:** 1.0.9 (build 11), en Play Store
- **Stack:** Kotlin + Jetpack Compose, MVVM + StateFlow, Firebase (Auth, Firestore, FCM, Remote Config, Crashlytics)
- **Página interactiva (misma tabla):** https://claude.ai/artifact/Fo6fB3iFHgJdKppP4CTjUq

---

## 1. Contrato de datos en Firestore (debe ser idéntico en ambas apps)

Las dos apps comparten la misma base de datos. Si un nombre de campo o un valor no coincide, los datos no se ven bien entre plataformas.

```
users/{userId}/
  maletas/{maletaId}/
    nombre:    string
    tipo:      "carry-on" | "grande" | "mochila" | "personal"
    color:     string (hex, p. ej. "#4A90E2")
    icono:     string
    creadaEn:  timestamp (server timestamp)
    items/{itemId}/
      nombre:    string
      categoria: "ropa" | "electronica" | "documentos" | "higiene" | "medicamentos" | "otros"
      cantidad:  number (entero)
      estado:    "por_empacar" | "empacado" | "usado"   (default "por_empacar")
      notas:     string (default "")
      creadoEn:  timestamp (server timestamp)
```

**Reglas de comportamiento compartidas**
- El id del documento no se guarda como campo; Android lo lee con `@DocumentId`.
- Al borrar una maleta se borran primero todos sus items y luego la maleta, en un batch, para no dejar items huérfanos.
- Ciclo de estado con un toque: `por_empacar → empacado → usado → por_empacar`.
- Colores de maleta, en este orden: Azul `#4A90E2`, Verde `#27AE60`, Rojo `#E74C3C`, Naranja `#E67E22`, Morado `#8E44AD`, Gris `#7F8C8D`.
- Valor de `icono` según `tipo` (son nombres de SF Symbols; Android los traduce a íconos Material):
  `carry-on` → `suitcase.rolling` · `grande` → `suitcase` · `mochila` → `backpack` · `personal` → `bag`. Si el tipo no se reconoce, se usa `suitcase.rolling`.

| Verificación | Android | iOS | Nota iOS |
|---|---|---|---|
| Mismos nombres de campo en maleta | ✅ | | |
| Mismos nombres de campo en item | ✅ | | |
| Mismos valores de `tipo` | ✅ | | |
| Mismos valores de `categoria` | ✅ | | |
| Mismos valores de `estado` | ✅ | | |
| `creadaEn` / `creadoEn` como server timestamp | ✅ | | |
| Borrado en cascada de items | ✅ | | |
| Mismos 6 colores y orden | ✅ | | |
| Mismos valores de `icono` (SF Symbols) | ✅ | | |

---

## 2. Funcionalidades

### Pantallas y navegación
| Función | Android | Nota Android | iOS | Nota iOS |
|---|---|---|---|---|
| Splash | ✅ | | | |
| Onboarding | ⚠️ | 3 páginas; el texto habla de "crea tu viaje, salida, destino" (concepto viejo) | | |
| Barra inferior con 3 tabs | ✅ | Mis Maletas · Checklist · Perfil | | |

### Cuenta
| Función | Android | Nota Android | iOS | Nota iOS |
|---|---|---|---|---|
| Login y registro con email/contraseña | ✅ | Registro con confirmación de contraseña | | |
| Google Sign-In | ✅ | Con CredentialManager | | |
| Sign in with Apple | ➖ | No aplica en Android | | |
| Cerrar sesión | ✅ | Limpia el historial de navegación | | |
| Recuperar contraseña | ❌ | | | |
| Eliminar cuenta | ❌ | | | |

### Maletas
| Función | Android | Nota Android | iOS | Nota iOS |
|---|---|---|---|---|
| Crear y editar maleta | ✅ | Formulario inferior: nombre, tipo, color, ícono | | |
| 4 tipos de maleta | ✅ | carry-on, grande, mochila, personal | | |
| 6 colores | ✅ | Mismo orden que iOS según el código | | |
| Swipe para borrar con confirmación | ✅ | | | |
| Long-press para editar | ✅ | | | |
| Progreso de empaque en la tarjeta | ✅ | Barra + contador en tiempo real | | |
| Estado vacío | ✅ | "¡Agrega tu primera maleta!" | | |

### Items
| Función | Android | Nota Android | iOS | Nota iOS |
|---|---|---|---|---|
| Crear y editar item | ⚠️ | Bug: al editar no se guardan las notas (corregido en local, sin release) | | |
| 6 categorías | ✅ | | | |
| Cambio de estado con un toque | ✅ | | | |
| Filtro por estado | ✅ | Todos / Por empacar / Empacado / Usado | | |
| Lista agrupada por categoría | ✅ | | | |
| Swipe para borrar con confirmación | ✅ | | | |
| Long-press para editar | ✅ | | | |

### Checklist
| Función | Android | Nota Android | iOS | Nota iOS |
|---|---|---|---|---|
| Pendientes de todas las maletas | ✅ | Agrupados por maleta | | |
| Progreso total | ✅ | X de Y empacados y % | | |
| Estado "¡Todo empacado!" | ✅ | | | |

### Perfil
| Función | Android | Nota Android | iOS | Nota iOS |
|---|---|---|---|---|
| Foto, nombre y email | ✅ | | | |
| Estadísticas | ✅ | Maletas, items y % listo | | |
| Modo oscuro | ✅ | Toggle guardado en el dispositivo | | |
| Botón de crash de prueba | ✅ | Solo visible con `show_error_btn` en Remote Config | | |

### Firebase
| Función | Android | Nota Android | iOS | Nota iOS |
|---|---|---|---|---|
| Firestore en tiempo real | ✅ | Listeners por maleta | | |
| Remote Config | ✅ | Flag `show_error_btn` | | |
| Crashlytics | ✅ | | | |
| Notificaciones push | ⚠️ | FCM solo muestra un Toast de prueba con el mensaje crudo | | |

---

## 3. Roadmap v1.1.0 (pendiente en Android)
| Función | Android | iOS | Nota iOS |
|---|---|---|---|
| Búsqueda de items | ❌ | | |
| Agrupar por estado (hoy solo por categoría) | ❌ | | |
| Swipe para marcar como empacado | ❌ | | |
| Número de pendientes en tab Checklist | ❌ | | |
| Accesibilidad (lector de pantalla) | ❌ | | |
| Textos traducibles (hoy fijos en código, solo español) | ❌ | | |
| Distinguir "sin internet" de error de Firestore | ❌ | | |

---

## 4. Diferencias encontradas
_Llenar después de completar las tablas: qué tiene una app y la otra no, y cuál se corrige primero._

-
