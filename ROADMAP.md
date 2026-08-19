# SuitcaseSmart — Roadmap de Versiones

> Fecha de referencia: 18 agosto 2026
> Versión actual: **1.0.8** (versionCode 10)

---

## v1.0.9 — Esta semana (ago 18–22, 2026)
**Objetivo:** Pulir antes de la primera subida a Play Store.

### TODO
- [ ] **Notas en items** — campo `notas` ya existe en el modelo `Item`, solo falta el UI en `AddSuitcaseSheetForm.kt`
- [ ] **Timestamps** — agregar `creadaEn: Timestamp` a `Maleta` e `Item` en `Viaje.kt` + guardar en `addMaleta` y `addItem`
- [ ] **Keystore** — generar keystore de producción y configurar signing en `app/build.gradle.kts`
- [ ] **Release AAB** — `./gradlew bundleRelease` y verificar que compila sin errores
- [ ] **Ícono 512x512** — subir a Play Console (requerido)
- [ ] **Screenshots** — mínimo 2 por orientación para la ficha de Play Store
- [ ] **Política de privacidad** — URL pública requerida por Play Store
- [ ] **Descripción** — título, descripción corta y larga en Play Console
- [ ] **Subir a Play Store** — internal testing track

### Mejoras incluidas en esta versión
- Notas visibles en detalle de item (read-only si no se edita)
- Timestamps para orden cronológico de maletas e items

---

## v1.1.0 — Septiembre 2026 (meta: 15 sep)
**Objetivo:** Mejorar UX de uso diario durante el viaje.

### TODO
- [ ] **Búsqueda de items** — barra de búsqueda en `SuitcaseDetail` para filtrar por nombre
- [ ] **Ordenamiento** — toggle "agrupar por categoría" vs "agrupar por estado" en `SuitcaseDetail`
- [ ] **Confirmación al borrar** — diálogo de confirmación antes de eliminar maleta o item
- [ ] **Swipe para empacar** — swipe derecho = marcar como empacado directamente en la lista
- [ ] **Empty state mejorado** — mensaje motivacional en Checklist cuando todos los items están empacados
- [ ] **Contador en bottom nav** — badge con cantidad de items pendientes en ícono de Checklist
- [ ] **Accesibilidad** — revisar content descriptions en íconos para TalkBack

### Mejoras de código
- [ ] Extraer constantes de strings a `strings.xml` (actualmente hay strings hardcodeados en Compose)
- [ ] Añadir manejo de errores de red (distinción entre sin internet vs error de Firestore)

---

## v1.2.0 — Octubre 2026 (meta: 15 oct)
**Objetivo:** Features de productividad para viajeros frecuentes.

### TODO
- [ ] **Plantillas de maletas** — lista de plantillas predefinidas (carry-on básico, semana de negocios, playa, etc.)
- [ ] **Duplicar maleta** — long-press → opción "Duplicar" → crea copia con todos sus items
- [ ] **Exportar lista** — compartir contenido de maleta como texto plano (Intent share)
- [ ] **Peso estimado** — campo opcional `peso: Float` en `Item`, suma total visible en detalle de maleta
- [ ] **Agrupación por categoría** — sección colapsable por categoría en `SuitcaseDetail`
- [ ] **Foto de item** — seleccionar imagen de galería y guardar URL en Firestore Storage

### Mejoras de código
- [ ] Migrar a `ViewModel` con `SavedStateHandle` para sobrevivir process death
- [ ] Unit tests para `HomeViewModel` y `SuitcaseDetailViewModel`

---

## v1.3.0 — Noviembre 2026 (meta: 15 nov)
**Objetivo:** Engagement y retención de usuarios.

### TODO
- [ ] **Push notifications** — recordatorio configurable "empaca mañana" via FCM scheduled
- [ ] **Widget de pantalla de inicio** — Glance widget con progreso de maleta activa
- [ ] **Modo viaje** — vista simplificada optimizada para uso durante el viaje (solo empacado → usado)
- [ ] **Historial** — campo `ultimoUso: Timestamp` en maleta, sección "Último uso" en ProfileScreen
- [ ] **Apple Sign-In** — si se agrega soporte iOS en el futuro
- [ ] **Soporte de tablets** — layout adaptativo con panel lateral en pantallas grandes

### Mejoras de código
- [ ] Implementar DI con Hilt (o Koin) para mejorar testabilidad
- [ ] Crashlytics dashboards y alertas configuradas

---

## Backlog sin versión asignada
- Compartir maleta con otro usuario (Firestore rules + invitaciones)
- Concepto de "Viaje" como agrupador de maletas
- Integración con Google Flights / Calendar para fechas de viaje
- Modo offline mejorado con sync indicator
- Linter CI (GitHub Actions + ktlint)

---

## Historial
| Versión | Fecha | Highlights |
|---------|-------|-----------|
| 1.0.8 | ago 2026 | UI redesign, dark mode toggle, checklist screen |
| 1.0.7 | ago 2026 | Fix profile header color, bump versión |
| 1.0.6 | ago 2026 | Error handling Firestore + empty state |
| 1.0.5 | ago 2026 | Fix pantalla de perfil |
| 1.0.4 | ago 2026 | UI redesign + dark mode + checklist |
