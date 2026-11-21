# MercaSENA · Patrón Singleton (Creacional)

**Dominio:** Configuración global de la tienda y consecutivo de facturas

## Contexto

MercaSENA atiende cientos de peticiones web al mismo tiempo. Cada petición necesita leer la configuración de la tienda (porcentaje de IVA, moneda, monto para envío gratis) y algunas necesitan generar un número de factura. Cargar la configuración es **lento**: lee un archivo del disco. Por eso se decidió usar el patrón **Singleton**.

Clases principales:

- `ConfiguracionTienda`: Singleton clásico con inicialización perezosa (*lazy*).
- `GeneradorFacturas`: Singleton implementado con `enum`.

## Reglas de negocio

1. **Una sola instancia.** Solo puede existir **una** instancia de `ConfiguracionTienda` durante toda la ejecución, **aunque varios hilos la pidan exactamente al mismo tiempo**. Como la carga es lenta, crear dos instancias duplica el tiempo y puede dejar datos inconsistentes.
2. **Solo lectura.** La configuración es de solo lectura. Ninguna clase externa puede agregar, cambiar ni borrar propiedades. Si alguien lo intenta con el mapa que devuelve `getPropiedades()`, debe recibir una `UnsupportedOperationException`.
3. **Propiedad inexistente.** Pedir una propiedad que no existe lanza `IllegalArgumentException`.
4. **Consecutivo de facturas.** Las facturas se numeran desde `FAC-000001`, con seis dígitos y relleno de ceros. Los números nunca se repiten, aunque se pidan desde varios hilos.
5. **Pruebas.** El método `reiniciarParaPruebas()` existe solo para que las pruebas empiecen desde un estado limpio. No se usa en producción.

## Tu misión

Este proyecto implementa el patrón **Singleton** dentro de la tienda en línea **MercaSENA**. El código *compila*, pero tiene **errores sembrados**: algunos los detectan las pruebas que ya existen y otros **no**. Tu trabajo es encontrarlos todos, demostrarlos con pruebas y corregirlos con ayuda de la IA, sin perder el control técnico.

> La fuente de verdad son las **reglas de negocio** de este README. La IA no las conoce si no se las das.

## Protocolo de trabajo (obligatorio)

1. **Prepara el repositorio.** Crea un repositorio en GitHub con este proyecto y haz el primer commit sin cambios: `git commit -m "chore: estado inicial"`.
2. **Predice sin IA (20 min).** Lee este README y el código. Escribe tus hipótesis en `BITACORA_IA.md`, sección 1.
3. **Ejecuta las pruebas.** Corre `mvn test` o las pruebas desde IntelliJ / Eclipse. Anota cuáles fallan.
4. **Demuestra cada error con una prueba.** Por cada error que encuentres, incluidos los que las pruebas actuales no detectan, escribe primero una prueba que **falle**. Haz commit: `test: demuestra <error>`.
5. **Consulta al jurado de IAs.** Envía el mismo prompt a ChatGPT, Gemini y DeepSeek. Usa el prompt de 5 piezas: contexto, tarea, restricciones, formato y criterio de verificación. Registra en la bitácora en qué coincidieron y en qué no.
6. **Corrige.** Aplica la corrección que **tú** entiendes y puedes explicar. La prueba debe pasar. Haz commit: `fix: corrige <error>`.
7. **Refactoriza con IA.** Pide a la IA una mejora de legibilidad que no cambie el comportamiento. Todas las pruebas deben seguir en verde. Haz commit: `refactor: ...`.
8. **Revisa la seguridad.** Pregúntate qué podría hacer un usuario malicioso con este código y anota tu respuesta en la bitácora.
9. **Prepara la defensa.** El instructor te pedirá explicar, **sin IA**, cualquier línea que hayas cambiado.

## Cómo ejecutar

```bash
mvn test
```

Requisitos: JDK 17 o superior y Maven 3.9 o superior, o el Maven que trae IntelliJ IDEA o Eclipse.

## Qué NO hacer

- No borres ni debilites pruebas para que pasen.
- No pegues el proyecto completo en una IA sin explicarle las reglas de negocio.
- No aceptes una corrección que no sabes explicar.

## Referencia del patrón

- https://refactoring.guru/es/design-patterns/singleton
# 01-singleton
