# Monolito Modular - Walrex (V1)

Este proyecto es un Monolito Modular diseñado con **Arquitectura Hexagonal**, utilizando **Spring Modulith** para garantizar la separación de conceptos y la integridad de los dominios de negocio.

## Estructura del Proyecto

El sistema está dividido en múltiples módulos que representan áreas funcionales o capacidades técnicas específicas:

### Módulos Técnicos y Core
- **module-core**: Módulo principal que arranca la aplicación Spring Boot y orquesta la integración de todos los módulos.
- **module-common**: Utilidades, modelos compartidos y clases transversales.
- **module-security-commons**: Lógica compartida para la seguridad y gestión de tokens JWT.
- **module-websocket**: Servicio centralizado de notificaciones en tiempo real vía WebSockets.
- **gateway`: Puerta de enlace para el manejo de rutas y tráfico.

### Módulos de Negocio
- **module-almacen**: Gestión integral de almacenes, incluyendo ingresos, egresos, transformaciones de insumos, gestión de rollos y el nuevo sistema de **pesaje automático**.
- **module-articulos**: Gestión del catálogo de artículos y productos.
- **module-partidas**: Manejo de partidas de producción y seguimiento de procesos.
- **module-comercial**: Funcionalidades relacionadas con la gestión comercial y ventas.
- **module-users / module-role**: Gestión de identidad, usuarios y control de acceso basado en roles.
- **module-mailing**: Sistema de envío de correos electrónicos.
- **module-ecomprobantes**: Integración y generación de comprobantes electrónicos.
- **module-driver / module-liquidaciones**: Gestión de conductores y procesos de liquidación.
- **module-revision-tela**: Módulo especializado para la revisión de calidad de telas.

## Stack Tecnológico
- **Java 21**
- **Spring Boot 3.4.5**
- **Spring Modulith**
- **R2DBC** (Acceso reactivo a base de datos PostgreSQL)
- **Apache Kafka** (Mensajería y captura de eventos con Avro)
- **Flyway** (Gestión de migraciones de base de datos)
- **Project Reactor** (Programación reactiva)
- **MapStruct** y **Lombok** (Productividad en el desarrollo)

## Cómo Ejecutar en Desarrollo

Para ejecutar la aplicación localmente utilizando el perfil de desarrollo y conectarse al entorno correspondiente, utilice el siguiente comando:

```bash
mvn clean install spring-boot:run -Dspring-boot.run.profiles=dev -Dmaven.test.skip=true
```

## Agregar un Nuevo Módulo

Cuando agregue un nuevo módulo al monolito, debe completar dos pasos obligatorios:

### 1. Registrar el módulo en el Dockerfile

Edite el `Dockerfile` y agregue el nuevo módulo en la sección de construcción de Maven. Ejemplo:

```dockerfile
RUN mvn clean package -pl module-core,module-almacen,module-nuevo -am -DskipTests
```

### 2. Registrar en la tabla de módulos del Gateway

El gateway es **dinámico** y obtiene su configuración de rutas desde la tabla `gateway.tb_modules`. Cree una migration en `gateway/src/main/resources/db/migration/` con el siguiente patrón:

```sql
-- V[VERSION]__add_module_[nombre].sql
INSERT INTO gateway.tb_modules (
    module_name,
    uri,
    path,
    strip_prefix_count,
    status,
    is_pattern
) VALUES (
    'module-nuevo',
    'http://localhost:8088',
    '\/api\/v2\/module-nuevo(\/.*)?',
    2,
    'ACTIVE',
    true
);
```

**Importante**: 
- `path` debe usar patrón compatible con regex si `is_pattern = true`
- `strip_prefix_count` define cuántos segmentos remover del path antes de enrutarlo
- `status` debe ser `'ACTIVE'` para que el gateway enrute las solicitudes
- `uri` debe apuntar al host/puerto donde corre el módulo

## Características Recientes
- **Sistema de Pesaje**: Integración con balanzas digitales vía Raspberry Pi para el registro automático de peso en rollos de tela, con notificaciones en tiempo real al frontend.
- **Arquitectura Reactiva**: Implementación completa de stacks no bloqueantes para alta escalabilidad.
