# SuitcaseSmart — Contexto del Proyecto

## ¿Qué es esto?
App móvil para organizar maletas de viaje. La **maleta es la unidad central** — no el viaje.
El usuario crea maletas, agrega items, y durante el viaje marca qué empacó y qué ya usó.

## Estado actual
- ✅ Proyecto Flutter creado (Android + iOS)
- ✅ Firebase Auth implementado (email, Google, Apple)
- ✅ Firestore configurado (base de datos limpia, lista para usar)
- 🔲 CRUD de maletas — por implementar
- 🔲 CRUD de items — por implementar
- 🔲 Sistema de estados de items — por implementar

## Stack
- **Mobile:** Flutter (Android + iOS)
- **Auth:** Firebase Authentication
- **DB:** Cloud Firestore
- **Estado:** por definir (preferencia: Riverpod o BLoC)

## Estructura de Firestore
```
users/
  {userId}/
    maletas/
      {maletaId}/
        nombre: string
        tipo: "carry-on" | "grande" | "mochila" | "personal"
        color: string (hex)
        icono: string
        creadaEn: timestamp
        items/
          {itemId}/
            nombre: string
            categoria: "ropa" | "electronica" | "documentos" | "higiene" | "medicamentos" | "otros"
            cantidad: number
            estado: "por_empacar" | "empacado" | "usado"
            notas: string (opcional)
            creadoEn: timestamp
```

## Flujo principal
Mis Maletas → Crear maleta (tipo + nombre) → Agregar items (nombre + categoría + cantidad) → Durante el viaje: marcar Empacado → marcar Usado

## Pantallas MVP
1. **Mis Maletas** — lista de maletas con barra de progreso de empaque
2. **Detalle de Maleta** — items agrupados por categoría o estado
3. **Agregar/Editar Item** — bottom sheet modal
4. **Checklist** — vista filtrada de items "por empacar"
5. **Perfil** — datos del usuario y cerrar sesión

## Estados de un item
`por_empacar` → `empacado` → `usado`

## Decisiones de arquitectura importantes
- NO hay colección de "viajes" en Firestore — las maletas viven directo bajo el usuario
- Si en el futuro se agrega el concepto de viaje, se añade un campo `viajeId` opcional en la maleta sin romper nada
- Offline-first: aprovechar el soporte offline nativo de Firestore
- Dark mode desde el inicio

## Qué NO va en el MVP (backlog)
- Compartir maleta con otro usuario
- Plantillas de maletas
- Historial de viajes
- Fotos de items
- Peso estimado
- Notificaciones
- Concepto de Viaje como agrupador

## Por dónde empezar
Implementar pantalla **Mis Maletas** con CRUD completo de maletas. Eso desbloquea todo lo demás.
