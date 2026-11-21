# Bitácora de uso de IA: Patrón Singleton

Proyecto: MercaSENA (Java 17, JUnit 5, Maven)

Aprendices: Cristian David Carvajal
Ficha: 3409871
Fecha de inicio: 21 de noviembre de 2026

> La bitácora es evidencia del ítem 18 de la lista de chequeo. Regístralo **todo**, también las respuestas de la IA que estaban mal.
> Nunca pegues en una IA contraseñas, tokens, datos personales reales ni código que no tengas permiso de compartir.

## 1. Predicción sin IA (máx. 20 minutos)

Antes de abrir cualquier IA, lee el README y el código. Escribe aquí qué errores crees que hay y por qué.

| # | Archivo y línea | ¿Qué creo que está mal? | ¿Qué regla de negocio rompe? |
|---|---|---|---|
| 1 | ConfiguracionTienda.java (líneas 25–30) | La comprobación if (instancia == null) en getInstancia() no está sincronizada y cargarDesdeArchivo() tiene una espera (Thread.sleep(50)). Si varios hilos llaman al método al mismo tiempo, pueden crear múltiples instancias de ConfiguracionTienda. | Regla 1 (Una sola instancia): Solo debe existir una instancia durante toda la ejecución, aunque varios hilos la pidan al mismo tiempo. |
| 2 | ConfiguracionTienda.java (líneas 44–46) | El método getPropiedades() devuelve directamente el HashMap mutable interno en vez de una vista de solo lectura (Collections.unmodifiableMap), permitiendo modificar, agregar o eliminar propiedades desde fuera. | Regla 2 (Solo lectura): La configuración debe ser de solo lectura y cualquier intento de modificar el mapa de getPropiedades() debe lanzar UnsupportedOperationException. |
| 3 | GeneradorFacturas.java (líneas 13–16) | En siguienteNumero() se usa contador.getAndIncrement() iniciando en 0, por lo que la primera factura sale como FAC-000000` en lugar de empezar en 1 (incrementAndGet()). | Regla 4 (Consecutivo de facturas): Las facturas deben numerarse desde FAC-000001` con seis dígitos y relleno de ceros. |

## 2. Registro de interacciones con IA

Copia una fila por cada consulta importante.

| # | Herramienta | Prompt (resumen o enlace) | ¿Qué respondió? | ¿Era correcto? ¿Cómo lo verifiqué? | ¿Lo usé? (sí / no / modificado) |
|---|---|---|---|---|---|
| 1 | Gemini | Prompt con rol de desarrollador Java sénior. Pide analizar y corregir solo `ConfiguracionTienda.java` (Singleton no thread-safe, líneas 25–30, `Thread.sleep(50)` en `cargarDesdeArchivo()`). Debe garantizar la Regla 1, cambiar lo mínimo, justificar qué hace con el `Thread.sleep`, presentar una prueba de concurrencia y entregar Diagnóstico / Corrección / Cambios / Elementos conservados / Pruebas / Resultado. Debe preguntar antes de tocar algo fuera de alcance. | **Diagnóstico:** el `if (instancia == null)` es una operación *check-then-act* no atómica. Aunque `instancia` ya es `volatile`, dos hilos pueden entrar al bloque y ejecutar `new ConfiguracionTienda()`, lo que incrementa `INSTANCIAS_CREADAS` más de una vez. **Decisión sobre `Thread.sleep(50)`:** se conserva, porque simula la lectura lenta del archivo y ayuda a exponer la condición de carrera en las pruebas. **Corrección:** *Double-Checked Locking* con `synchronized (ConfiguracionTienda.class)` sobre el campo `volatile`. **Conservó:** paquete, campos, constructor privado, `getPropiedad`, `getPorcentajeIva`, `getPropiedades()` (sin cambios), `cargarDesdeArchivo()` y `reiniciarParaPruebas()`. **Prueba propuesta:** clase `PruebaSingletonConcurrencia` con `main`, 20 hilos liberados con `CountDownLatch(1)`. Verifica que todos obtengan la misma instancia y que `INSTANCIAS_CREADAS` sea 1. **Resultado declarado:** cumple la Regla 1. | **Diagnóstico: correcto.** Coincide con el código original (zip, estado inicial): `getInstancia()` en las líneas 25–30 es *check-then-act* sin sincronizar, `instancia` ya es `volatile` (línea 16), y existen `INSTANCIAS_CREADAS` (14), `reiniciarParaPruebas()` (64–69) y `Thread.sleep(50)` (51). **Corrección: aplicada, confirmada en bytecode.** El `javap` de tu `ConfiguracionTienda.class` muestra en `getInstancia()` la secuencia `getstatic instancia → ifnonnull → monitorenter → getstatic → ifnonnull → new → putstatic → monitorexit`. Es *Double-Checked Locking*, igual a lo propuesto por Gemini. **Prueba propuesta: defectuosa tal como se entregó** (ver sección 5). Se reutilizó convertida a JUnit 5 en `ConfiguracionTiendaTest.debeGarantizarUnicaInstanciaEnConcurrencia` (mismo esquema de 20 hilos y `CountDownLatch`, mismos nombres de variable). Esa prueba **falla contra el código original** (15 de 15 corridas de referencia). | sí (la corrección). La prueba se usó **modificada** (de `main` a test JUnit 5). PENDIENTE: quién la adaptó. |
| 2 | ChatGPT (según la URL `https://chatgpt.com/c/...` que aparece en la terminal) | Consulta "Análisis y solución de errores Maven – Proyecto MercaSENA (Patrón Singleton)". Rol de desarrollador sénior (Java, Maven, JUnit). Contexto: proyecto `01-singleton`, Linux Mint, `mvn test`. Error: `Source option 5 is no longer supported. Use 8 or later.` Pide causa, `pom.xml` completo corregido, comandos de validación y que no se cambie el código Java. Es un problema de configuración del `pom.xml`, no un error de reglas de negocio del patrón. | Diagnóstico: `maven-compiler-plugin:3.1` ignoraba `maven.compiler.release=17`. Propuso un `pom.xml` con `maven-compiler-plugin 3.13.0` y `<release>${maven.compiler.release}</release>`. | **Diagnóstico: correcto.** El `pom.xml` original define `maven.compiler.release=17`, pero solo declara `maven-surefire-plugin 3.2.5` y ningún `maven-compiler-plugin`. Maven usa entonces el compilador por defecto (3.1), que ignora `release`. Antes: `BUILD FAILURE` con `Source option 5 is no longer supported`. Después: `mvn clean test` (21/11/2026, 11:04:52) con `maven-compiler-plugin:3.13.0`, `javac [debug release 17]`, `Tests run: 5, Failures: 0, Errors: 0` y `BUILD SUCCESS`. Entre las corridas de 10:55 y 11:02 cambió Surefire (3.2.5 → 3.5.2). PENDIENTE: no consta quién lo propuso. | sí (el plugin 3.13.0 aparece aplicado en la terminal) |
| 3 | Claude (Anthropic), en esta conversación | Organizar la bitácora con la información entregada (zip original, `.class`, reportes Surefire, `ConfiguracionTiendaTest.java`, salidas de terminal) y luego "analiza todo, compara y corrige". | Comparó el zip original con tus archivos compilados. Confirmó con `javap` que *Double-Checked Locking* e `incrementAndGet` están aplicados y que `getPropiedades()` sigue devolviendo el mapa mutable (Regla 2 sin corregir). Entregó un proyecto corregido con `Collections.unmodifiableMap`, la prueba `getPropiedadesEsDeSoloLectura` y la restauración del test original `devuelveSiempreLaMismaInstancia`. | Verificado por Claude en su propio entorno (JDK 17, JUnit 5.10.1 por consola, **no Maven**): el original falla 3 pruebas (Reglas 1, 2 y 4), el corregido pasa 7 de 7, y repetido 15 veces siempre dio verde. PENDIENTE: que el aprendiz lo reproduzca con `mvn clean test` en su máquina. | PENDIENTE: decisión del aprendiz. La corrección de la Regla 2 solo cuenta cuando el aprendiz la entienda, la aplique y haga commit. |

## 3. Jurado de IAs

Envía el **mismo prompt** a tres herramientas distintas y compara.

| Pregunta enviada | ChatGPT | Gemini | DeepSeek | ¿Quién acertó? ¿En qué discreparon? |
|---|---|---|---|---|
| PENDIENTE: falta comparación de ChatGPT / Gemini / DeepSeek | PENDIENTE | PENDIENTE | PENDIENTE | PENDIENTE |

## 4. Errores encontrados y corregidos

| # | Error | Prueba que lo demuestra (nombre del método de prueba) | Commit de la corrección | ¿Lo encontré yo, la IA o ambos? |
|---|---|---|---|---|
| 1 | Singleton no thread-safe en `getInstancia()` (`ConfiguracionTienda.java`, líneas 25–30, confirmadas en el zip original). Rompe la Regla 1. | `ConfiguracionTiendaTest.debeGarantizarUnicaInstanciaEnConcurrencia`. Falla contra el código original (`Todos los hilos deben recibir exactamente la misma referencia`, 15 de 15 corridas de referencia) y pasa con la corrección. Las pruebas originales del zip no detectan este error. | PENDIENTE | PENDIENTE / aprendiz según predicción |
| 2 | `getPropiedades()` devuelve el `HashMap` mutable interno (`ConfiguracionTienda.java`, líneas 44–46, confirmadas en el zip original). Rompe la Regla 2. | `ConfiguracionTiendaTest.getPropiedadesEsDeSoloLectura` (propuesta de Claude). Falla contra el código original (`Expected java.lang.UnsupportedOperationException to be thrown, but nothing was thrown`) y pasa con la corrección. | PENDIENTE: **tu código aún no la tiene** (el `javap` de tu `.class` muestra `getPropiedades()` devolviendo el campo sin envolver). | PENDIENTE / aprendiz según predicción |
| 3 | `siguienteNumero()` usa `getAndIncrement()` desde 0 (`GeneradorFacturas.java`). En el zip, el método ocupa las líneas 14–17 y `getAndIncrement()` está en la línea 15. Rompe la Regla 4. | `GeneradorFacturasTest.laPrimeraFacturaEsLaNumeroUno`. Con el código original falla con `expected: <FAC-000001> but was: <FAC-000000>` (también reproducido en la corrida de referencia). Con la corrección pasa. Tu `GeneradorFacturas.class` ya usa `incrementAndGet`. | PENDIENTE | PENDIENTE / aprendiz según predicción |

Observación (no numerada como error del patrón): el error de Maven "Source option 5" es de configuración del `pom.xml`. Ver consulta #2 de la sección 2.

## 5. Lo que la IA hizo mal o no vio

**Caso verificado compilando.** La prueba `PruebaSingletonConcurrencia` que propuso Gemini (consulta #1) **no compila tal como se entregó**. No declara `package` ni importa `ConfiguracionTienda`. `javac` responde `cannot find symbol ... class ConfiguracionTienda`. Además usa `INSTANCIAS_CREADAS` y `reiniciarParaPruebas()`, que son de acceso de paquete (sin `public`, líneas 14 y 64), así que solo funcionan dentro de `co.sena.mercasena.singleton`. Con la línea `package co.sena.mercasena.singleton;` y dentro del paquete sí compila y corre: `Prueba de concurrencia exitosa. Instancias creadas: 1`. También es un `main`, no un test JUnit 5, y el proyecto corre sus pruebas con `mvn test`.

PENDIENTE: cómo se dio cuenta el aprendiz (por ejemplo, error de compilación o lectura del código).

## 6. Reflexión final (5 a 10 líneas)

PENDIENTE: falta reflexión final del aprendiz

---

## Matriz de reglas y pruebas

| Regla | Archivo / método | Prueba (método JUnit 5) | Resultado de la prueba |
|---|---|---|---|
| Regla 1: una sola instancia | `ConfiguracionTienda.getInstancia()` | `ConfiguracionTiendaTest.debeGarantizarUnicaInstanciaEnConcurrencia` | Corrida de referencia de Claude: falla contra el original (15/15) y pasa con la corrección (15/15). Tu `mvn clean test` de 11:04:52 dio `Tests run: 3, Failures: 0`. PENDIENTE: nombres de los tests en tu reporte. |
| Regla 2: solo lectura | `ConfiguracionTienda.getPropiedades()` | `ConfiguracionTiendaTest.getPropiedadesEsDeSoloLectura` (propuesta de Claude) | Corrida de referencia de Claude: falla contra el original y pasa con la corrección. PENDIENTE: no está en tu proyecto ni se ejecutó en tu máquina. |
| Regla 4: consecutivo desde FAC-000001 | `GeneradorFacturas.siguienteNumero()` | `GeneradorFacturasTest.laPrimeraFacturaEsLaNumeroUno` y `losNumerosSonConsecutivos` | Antes falló (`expected: <FAC-000001> but was: <FAC-000000>`). Ahora `Tests run: 2, Failures: 0, Errors: 0` (tu terminal, 11:04:52). |

## Errores encontrados (resumen de reglas)

| Regla | Predicción del aprendiz | Corrección aplicada | Verificada con prueba |
|---|---|---|---|
| Regla 1 | Sí (sección 1, #1) | *Double-Checked Locking* (propuesta de Gemini), confirmada en el bytecode de tu `.class`. PENDIENTE: commit. | Sí, en la corrida de referencia (rojo contra el original, verde con la corrección). PENDIENTE: salida de tu `mvn` con el nombre del test. |
| Regla 2 | Sí (sección 1, #2) | PENDIENTE: no aplicada en tu código. Propuesta: `Collections.unmodifiableMap` en el constructor. | PENDIENTE: la prueba existe solo en el proyecto corregido entregado |
| Regla 4 | Sí (sección 1, #3) | `getAndIncrement` → `incrementAndGet` y formato `FAC-%06d`. PENDIENTE: commit. | Sí: el test pasó de fallar a pasar |

## Jurado de IAs (resumen)

| Criterio | Resultado |
|---|---|
| Mismo prompt enviado a tres IA | PENDIENTE: falta comparación de ChatGPT / Gemini / DeepSeek |
| IA con la mejor respuesta | PENDIENTE |

## Decisión técnica

| Tema | Decisión | Justificación | Evidencia |
|---|---|---|---|
| Mecanismo de sincronización del Singleton | *Double-Checked Locking* con `synchronized (ConfiguracionTienda.class)` | Según Gemini: `instancia` es `volatile` (visibilidad y sin reordenamiento), la primera lectura evita el costo de sincronizar y la segunda comprobación dentro del bloque garantiza una sola construcción. PENDIENTE: justificación con tus palabras. | Bytecode de tu `.class` y corrida de referencia. PENDIENTE: commit. |
| Qué hacer con `Thread.sleep(50)` en `cargarDesdeArchivo()` | Conservarlo, según Gemini | Según Gemini: simula la latencia de E/S y ayuda a exponer la condición de carrera | Tu `.class` conserva `Thread.sleep`. PENDIENTE: justificación escrita por el aprendiz. |
| Cómo hacer `getPropiedades()` de solo lectura | Propuesta de Claude: envolver el mapa con `Collections.unmodifiableMap` en el constructor. PENDIENTE: decisión del aprendiz. | Un solo punto de cambio, el mapa nunca sale mutable y `getPropiedad` sigue igual | Corrida de referencia: `getPropiedadesEsDeSoloLectura` rojo → verde |
| Cómo numerar facturas desde 1 con seis dígitos | `incrementAndGet` con formato `FAC-%06d` | PENDIENTE: falta justificación escrita por el aprendiz | Test `laPrimeraFacturaEsLaNumeroUno` en verde (11:04:52) |
| Corrección del `pom.xml` (Source option 5) | `maven-compiler-plugin 3.13.0` con `<release>${maven.compiler.release}</release>` | El `pom.xml` original no declaraba `maven-compiler-plugin`, y el 3.1 por defecto ignora `maven.compiler.release=17` | `javac [debug release 17]`, `BUILD SUCCESS`, 5 tests en verde (11:04:52) |

## Seguridad

| Aspecto | Estado |
|---|---|
| Contraseñas, tokens o datos personales pegados en IA | No se observan en la información entregada |
| Permiso para compartir el código con la IA | PENDIENTE: falta confirmación |
| Qué podría hacer un usuario malicioso con este código (README, paso 8) | PENDIENTE: falta la respuesta del aprendiz |

---

## Datos faltantes para completar la bitácora

1. Aplicar la corrección de la Regla 2 en tu proyecto y correr `mvn clean test`. Tu código actual todavía devuelve el mapa mutable.
2. Los nombres de los tests que corrieron en tu `mvn clean test`: `grep -o 'testcase name="[^"]*"' target/surefire-reports/TEST-co.sena.mercasena.singleton.ConfiguracionTiendaTest.xml`
3. Reproducir en tu máquina el antes y el después de la Regla 1 (rojo contra el original, verde con la corrección) con la salida de `mvn`.
4. Quién adaptó la prueba de Gemini a JUnit 5, y si se usó otra IA para eso.
5. Qué cambió en el `pom.xml` entre las corridas de 10:55 y 11:02 (Surefire 3.2.5 → 3.5.2) y quién lo propuso.
6. Decisión del aprendiz sobre la consulta #3 (Claude): usar, no usar o modificar.
7. Comparación del mismo prompt en ChatGPT, Gemini y DeepSeek, con quién acertó y en qué discreparon.
8. Hashes de los commits (`git log --oneline`). El README exige `chore: estado inicial`, `test: demuestra ...`, `fix: corrige ...` y `refactor: ...`. No hay evidencia de ninguno (el zip no trae `.git`).
9. Refactor con IA (README, paso 7): no hay evidencia.
10. Quién encontró cada error (aprendiz, IA o ambos), con evidencia más allá de la predicción.
11. Cómo se dio cuenta el aprendiz de la falla de la prueba de Gemini (sección 5).
12. Reflexión final del aprendiz (sección 6).
13. Decisión escrita por el aprendiz sobre sincronización, `Thread.sleep(50)`, solo lectura y numeración de facturas.
14. Respuesta de seguridad del aprendiz (README, paso 8).
15. Confirmación de permiso para compartir el código con la IA.
