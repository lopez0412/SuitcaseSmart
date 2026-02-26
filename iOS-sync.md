# SuitcaseSmart — Sync Android → iOS
**Última actualización:** Febrero 2026

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
| Cambiar estado de item (tap chip) | ✅ | ✅ |
| Perfil (nombre, email, sign out) | ✅ | ✅ |
| Barra de progreso en detalle de maleta | ✅ | ✅ |
| Eliminar maleta (swipe + confirmación) | ✅ | ❌ pendiente |
| Eliminar item (swipe + confirmación) | ✅ | ❌ pendiente |
| Editar maleta (long press → sheet) | ✅ | ❌ pendiente |
| Editar item (long press → sheet) | ✅ | ❌ pendiente |
| Barra de progreso en maleta card (Home) | ✅ | ❌ pendiente |
| Checklist (filtro "por empacar") | ✅ | ❌ pendiente |
| Back button en detalle | ✅ | ✅ |

**Android MVP: completo. Pendiente en iOS: 6 features.**

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

> **Nota:** Firestore no elimina subcolecciones en cascada. Al eliminar una maleta, sus items quedan huérfanos en la base de datos pero el usuario no puede acceder a ellos. Aceptable para MVP.

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

### Estados de un item
| Raw value | Label visible | Color |
|---|---|---|
| `por_empacar` | "Por empacar" | Naranja `#E67E22` |
| `empacado` | "Empacado" | Verde `#27AE60` |
| `usado` | "Usado" | Gris `#7F8C8D` |

Ciclo al tocar el chip: `por_empacar` → `empacado` → `usado` → `por_empacar`

---

## UX de features implementadas en Android (referencia para iOS)

### Cambio de estado
Chip/pill tappable a la derecha del item. Fondo semitransparente + borde sólido del color del estado. Un tap cicla al siguiente estado. **Update optimista** — cambio instantáneo en UI, Firestore en background.

### Barra de progreso
- **En detalle de maleta:** LinearProgressIndicator encima de la lista. Cuenta `empacado` + `usado` como "empacados". Muestra "X de Y empacados".
- **En card de Home:** barra delgada debajo del tipo de maleta. Mismo conteo. Los counts se cargan con N queries al entrar en Home (uno por maleta).

### Eliminar maleta / item
- **Gesto:** swipe de derecha a izquierda. Fondo rojo (`#E74C3C`) con ícono de basurero.
- **Confirmación:** `AlertDialog` con botón "Eliminar" en rojo y "Cancelar". El swipe rebota de vuelta (no elimina directamente).
- **Ejecución:** update optimista (desaparece de la lista inmediatamente), Firestore en background.

### Editar maleta
- **Gesto:** long press en la card de la maleta.
- **UX:** abre el mismo bottom sheet de "Agregar Maleta" pre-relleno con los datos actuales. El título cambia a "Editar Maleta".
- **Ejecución:** update optimista en la lista, Firestore en background.

### Editar item
- **Gesto:** long press en cualquier parte del card del item (el chip sigue funcionando con tap normal).
- **UX:** abre el mismo bottom sheet de "Agregar Item" pre-relleno. Título cambia a "Editar Item". Se preservan `estado` y `notas` — solo se actualizan `nombre`, `categoria`, `cantidad`.
- **Ejecución:** update optimista, Firestore en background.

### Checklist
- **Acceso:** botón `FilterList` en la TopAppBar del detalle.
- **Activo:** ícono naranja, título cambia a "Por empacar", lista filtrada solo con items `por_empacar`.
- **Header en modo checklist:** "X items por empacar" (naranja) o "¡Todo empacado!" (verde) si la lista está vacía.
- **Inactivo:** vuelve a vista normal con barra de progreso.

---

## Backlog (fuera del MVP)

- Compartir maleta con otro usuario
- Plantillas de maletas
- Historial de viajes
- Fotos de items
- Peso estimado
- Notificaciones
- Concepto de Viaje como agrupador
