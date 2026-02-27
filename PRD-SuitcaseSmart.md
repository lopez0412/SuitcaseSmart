# PRD — SuitcaseSmart
**Versión:** 0.1 MVP  
**Plataformas:** Android + iOS (proyectos ya creados)  
**Backend:** Firebase Auth + Firestore  
**Fecha:** Febrero 2026

---

## 1. Visión del producto

Una app móvil donde la **maleta es la unidad central**. El usuario crea sus maletas, agrega lo que lleva, y durante el viaje marca qué ha usado y qué no. Simple, visual, sin fricción.

**Problema que resuelve:** La gente olvida qué empacó, lleva cosas de más, o llega al destino sin saber qué usó o qué le falta.

**Propuesta de valor:** Tu maleta digital, siempre en el bolsillo.

---

## 2. Usuarios objetivo

- Viajeros frecuentes (negocios, mochileros, turistas)
- Personas que viajan en familia y necesitan organizar múltiples maletas
- Cualquier persona que quiera empacar de forma más inteligente

---

## 3. Flujo principal (Happy Path)

```
Inicio → Mis Maletas
    → [+] Crear maleta
        → Elegir tipo/tamaño (carry-on, grande, mochila, personal)
        → Nombrarla (opcional, ej: "Maleta de Ana")
    → Abrir maleta
        → [+] Agregar item
            → Nombre del item
            → Categoría (ropa, electrónica, documentos, higiene, otros)
            → Cantidad
            → Estado inicial: "Por empacar"
        → Ver lista de items con su estado
        → Durante el viaje: cambiar estado → "Empacado" → "Usado"
```

---

## 4. Funcionalidades MVP

### 4.1 Autenticación ✅ (ya implementada)
- Email/password
- Google Sign-In
- Apple Sign-In
- Firebase Auth

### 4.2 Maletas
| Feature | Descripción |
|---|---|
| Crear maleta | Nombre, tipo/tamaño, color o ícono identificador |
| Tipos de maleta | Carry-on, Maleta grande, Mochila, Bolso personal |
| Listar maletas | Vista de cards en pantalla principal |
| Editar / Eliminar | Swipe o menú contextual |

### 4.3 Items dentro de la maleta
| Feature | Descripción |
|---|---|
| Agregar item | Nombre, categoría, cantidad |
| Categorías | Ropa, Electrónica, Documentos, Higiene, Medicamentos, Otros |
| Estado del item | `Por empacar` → `Empacado` → `Usado` |
| Editar / Eliminar item | Swipe o tap largo |
| Contador de progreso | "8 de 12 empacados" visible en la card de la maleta |

### 4.4 Lista de pendientes (Checklist)
- Vista filtrada por estado "Por empacar"
- Tap para marcar como "Empacado"
- Indicador visual de progreso por maleta (barra o porcentaje)

### 4.5 Marcar como Usado
- Durante el viaje, el usuario puede cambiar estado a "Usado"
- Útil para saber qué ropa ya usó, qué medicamento tomó, etc.
- Vista de "Lo que ya usé" por maleta

---

## 5. Estructura de datos en Firestore

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

> **Nota:** Sin colección de "viajes". Las maletas viven directamente bajo el usuario. Si en el futuro se quiere agregar el concepto de viaje, se puede añadir un campo `viajeId` opcional en cada maleta sin romper nada.

---

## 6. Pantallas principales

| Pantalla | Descripción |
|---|---|
| **Splash / Onboarding** | Logo + llamada a acción |
| **Auth** | Login / Registro (ya existe) |
| **Mis Maletas** | Lista de maletas con progreso |
| **Detalle de Maleta** | Items agrupados por categoría o estado |
| **Agregar/Editar Item** | Bottom sheet o pantalla modal |
| **Checklist de Empaque** | Vista filtrada "por empacar" |
| **Perfil** | Cerrar sesión, datos del usuario |

---

## 7. UX/UI Guidelines

- **Diseño limpio y colorido** — cada maleta puede tener su color o ícono
- **Interacciones rápidas** — cambiar estado con un tap, no navegar a otra pantalla
- **Offline-first** — los cambios deben funcionar sin internet y sincronizar después (Firestore offline support está built-in)
- **Dark mode** — soporte desde el inicio

---

## 8. Métricas de éxito del MVP

- Usuarios que crean al menos 1 maleta y 3+ items en su primera sesión
- Retención: usuarios que abren la app el día del viaje (marcan items como "empacado" o "usado")
- Menos de 2 segundos de carga en pantalla principal

---

## 9. Fuera del alcance para MVP (backlog)

- Compartir maleta con otro usuario
- Plantillas de maletas ("viaje de negocios 3 días", "playa")
- Historial de viajes
- Fotos de items
- Peso estimado de la maleta
- Notificaciones ("tu vuelo es mañana, ¿ya empacaste?")
- Concepto de Viaje como agrupador de maletas

---

## 10. Stack técnico

| Capa | Tecnología |
|---|---|
| Mobile | Flutter — proyectos Android e iOS ya creados |
| Auth | Firebase Authentication — ya implementado (email, Google, Apple) |
| Base de datos | Cloud Firestore — base de datos reiniciada y lista |
| Estado | Riverpod o BLoC |
| CI/CD | GitHub Actions + Fastlane |

---

## 11. Próximos pasos

1. ✅ PRD aprobado
2. Definir diseño visual (paleta de colores, tipografía)
3. Configurar proyecto Flutter + conectar Firebase existente
4. Implementar pantalla "Mis Maletas" + CRUD de maletas
5. Implementar CRUD de items + estados
6. Testing en dispositivo Android real
7. Beta cerrada con 5-10 usuarios

