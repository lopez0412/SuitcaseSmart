# SuitcaseSmart — Sync Android → iOS
**Última actualización:** Junio 2026

Este documento describe el estado actual de Android para que iOS vaya a la par.

---

## Estado general

| Pantalla / Feature | Android | iOS |
|---|---|---|
| Splash | ✅ | ✅ |
| Onboarding | ✅ | ✅ |
| Login (email + Google) | ✅ | ✅ |
| Registro | ✅ | ✅ |
| Mis Maletas — lista | ✅ | ✅ |
| Agregar Maleta (bottom sheet) | ✅ | ✅ |
| Detalle de Maleta — lista items | ✅ | ✅ |
| Agregar Item (bottom sheet) | ✅ | ✅ |
| Cambiar estado de item (tap checkbox) | ✅ | ✅ |
| Perfil (nombre, email, stats, sign out) | ✅ | ✅ |
| Barra de progreso en detalle de maleta | ✅ | ✅ |
| Back button en detalle | ✅ | ✅ |
| Eliminar maleta (swipe + confirmación) | ✅ | ❌ pendiente |
| Eliminar item (swipe + confirmación) | ✅ | ❌ pendiente |
| Editar maleta (long press → sheet) | ✅ | ❌ pendiente |
| Editar item (long press → sheet) | ✅ | ❌ pendiente |
| Barra de progreso + pills en maleta card (Home) | ✅ | ❌ pendiente |
| Checklist (tab en bottom nav, items por maleta) | ✅ | ❌ pendiente |
| Filtros por estado en Detalle (tabs: Todos / Por empacar / Empacado / Usado) | ✅ | ❌ pendiente |
| Items agrupados por categoría en Detalle | ✅ | ❌ pendiente |
| Stats en Perfil (maletas · items · % listo) | ✅ | ❌ pendiente |
| Toggle modo oscuro (persistido) | ✅ | ❌ pendiente |
| Tipografía custom (Sora + DM Sans) | ✅ | ❌ pendiente |
| Empty state en Home (sin maletas) | ✅ | ❌ pendiente |

**Android MVP: completo. Pendiente en iOS: 13 features.**

---

## Modelo de datos en Firestore

```
users/{userId}/maletas/{maletaId}
  nombre: String
  tipo:   "carry-on" | "grande" | "mochila" | "personal"
  color:  String  (hex, ej: "#4A90E2")
  icono:  String  (SF Symbol name, ej: "suitcase.rolling")

users/{userId}/maletas/{maletaId}/items/{itemId}
  nombre:    String
  categoria: "ropa" | "electronica" | "documentos" | "higiene" | "medicamentos" | "otros"
  cantidad:  Int
  estado:    "por_empacar" | "empacado" | "usado"
  notas:     String  (opcional, puede estar vacío)
```

> **Nota:** Al eliminar una maleta se hace un batch delete de todos sus items y luego el documento de la maleta — no quedan huérfanos. Si la operación falla, el ViewModel muestra un error al usuario.

---

## Constantes compartidas (deben ser iguales en ambas plataformas)

### Tipos de maleta
| Raw value | Ícono Android | SF Symbol iOS |
|---|---|---|
| `carry-on` | `Icons.Filled.Luggage` | `suitcase.rolling` |
| `grande` | `Icons.Filled.Work` | `suitcase` |
| `mochila` | `Icons.Filled.Backpack` | `backpack` |
| `personal` | `Icons.Filled.ShoppingBag` | `bag` |

### Colores de maleta (6 opciones, mismo orden)
| Nombre | Hex |
|---|---|
| Azul | `#4A90E2` |
| Verde | `#27AE60` |
| Rojo | `#E74C3C` |
| Naranja | `#E67E22` |
| Morado | `#8E44AD` |
| Gris | `#7F8C8D` |

### Paleta de marca (usar los mismos tokens)
| Token | Hex | Uso |
|---|---|---|
| `AviationNavy` | `#0A2342` | Primary (botones, headers, FAB) en light mode |
| `AviationNavyLight` | `#5B9BD5` | Primary en dark mode, checkboxes empacados |
| `SkyBlue` | `#5B9BD5` | Alias de AviationNavyLight |
| `SkyLight` | `#B3D1EE` | Textos secundarios sobre fondos navy |
| `GreenPacked` | `#27AE60` | Checkbox "usado" |
| `GreenPackedBg` | `#E8F8F0` | Fondo pill "empacado" |
| `AmberPendingBg` | `#FEF3E7` | Fondo pill "pendiente" |

### Estados de un item
| Raw value | Label visible | Color texto | Color fondo pill |
|---|---|---|---|
| `por_empacar` | "Pendiente" | `#E67E22` | `#FEF3E7` |
| `empacado` | "Empacado" | `AviationNavyLight` | `AviationNavyLight` 12% |
| `usado` | "Usado" | `#27AE60` | `#E8F8F0` |

Ciclo al tocar el checkbox: `por_empacar` → `empacado` → `usado` → `por_empacar`

### Tipografía
| Uso | Familia | Pesos |
|---|---|---|
| Títulos / headings | Sora | Light, Regular, Medium, SemiBold, Bold |
| Cuerpo / labels | DM Sans | Regular, Medium |

---

## UX de features implementadas en Android (referencia para iOS)

### Cambio de estado (checkbox)
Checkbox cuadrado (esquinas 8pt) a la izquierda del item. Vacío cuando `por_empacar`, relleno `AviationNavyLight` cuando `empacado`, relleno `GreenPacked` cuando `usado`. Un tap cicla al siguiente estado. **Update optimista** — cambio instantáneo en UI, Firestore en background.

### Badge de estado
Pill a la derecha del item con el label del estado y fondo semitransparente según la tabla de colores arriba.

### Barra de progreso en Detalle de Maleta
`LinearProgressIndicator` en el header navy (no dentro del contenido). Cuenta `empacado` + `usado` como "empacados". Muestra "X/Y" a la derecha.

### Barra de progreso en card de Home (MaletaRow)
- Barra lateral de acento (4dp) con el color de la maleta.
- Pills de conteo debajo del tipo: "N pendientes" (amber) y "N empacados" (verde).
- `LinearProgressIndicator` delgada (5dp) al fondo del card, coloreada con el color de la maleta.
- Los counts llegan via listeners en tiempo real de Firestore (uno por maleta).

### Filtros por estado en Detalle
`LazyRow` de chips justo debajo del header. Opciones: **Todos / Por empacar / Empacado / Usado**. Chip activo fondo `AviationNavy` texto blanco; inactivo fondo `surfaceVariant`.

### Items agrupados por categoría
Dentro de la lista de ítems del detalle, se agrupan por `categoria`. Encima de cada grupo va un label con el nombre de la categoría en uppercase, `labelSmall`, `onSurfaceVariant`.

### Stats en Perfil
Fila de 3 columnas dentro del header navy:
- **Maletas**: total de maletas del usuario
- **Items**: total de items en todas las maletas
- **Listo**: porcentaje empacado/total (ej: "72%")

### Toggle modo oscuro
`Switch` en la pantalla de Perfil, sección "Preferencias". Persiste en `SharedPreferences` / `UserDefaults`. Sobreescribe la preferencia del sistema.

### Eliminar maleta / item
- **Gesto:** swipe de derecha a izquierda. Fondo rojo con ícono de basurero.
- **Confirmación:** `AlertDialog` con botón "Eliminar" en rojo y "Cancelar". El swipe rebota (no elimina directamente).
- **Ejecución:** desaparece de la lista, Firestore en background. Al eliminar una maleta, se borran también sus items (batch delete).

### Editar maleta
- **Gesto:** long press en la card.
- **UX:** abre el mismo bottom sheet de "Agregar Maleta" pre-relleno. Título "Editar Maleta".

### Editar item
- **Gesto:** long press en el card del item.
- **UX:** abre el mismo bottom sheet de "Agregar Item" pre-relleno. Título "Editar Item". Se preservan `estado` y `notas` — solo se actualizan `nombre`, `categoria`, `cantidad`.

### Empty state en Home
Cuando el usuario no tiene maletas creadas, en lugar de la lista vacía se muestra centrado en pantalla: ícono de maleta (`Luggage` / `suitcase.rolling`) en tamaño grande (64pt) con opacidad baja, y debajo el texto "¡Agrega tu primera maleta!" en `titleMedium` con `onSurfaceVariant` al 50% de opacidad. El FAB sigue visible para que el usuario pueda crear su primera maleta. Los listeners de Firestore incluyen manejo de error — si falla la conexión, el ViewModel expone el error para mostrarlo al usuario.

### Checklist (tab en bottom nav)
Tab independiente en la barra de navegación inferior (entre Maletas y Perfil).
- **Contenido:** todos los items con estado `por_empacar` de todas las maletas del usuario.
- **Agrupación:** por maleta (nombre de maleta en uppercase como sección header).
- **Card de resumen:** fijo en la parte superior — fondo `AviationNavy`, muestra "X de Y items empacados" + círculo de progreso con porcentaje.
- **Estado vacío:** pantalla centrada con "✓" y "¡Todo empacado!".
- **Tap en checkbox:** cicla el estado del item (mismo comportamiento que en Detalle); si pasa a `empacado` o `usado`, desaparece del Checklist en tiempo real.

---

## Backlog (fuera del MVP)

- Compartir maleta con otro usuario
- Plantillas de maletas
- Historial de viajes
- Fotos de items
- Peso estimado
- Notificaciones push
- Concepto de Viaje como agrupador
- Editar perfil / cambiar contraseña
