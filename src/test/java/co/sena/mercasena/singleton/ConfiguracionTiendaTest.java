package co.sena.mercasena.singleton;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ConfiguracionTiendaTest {

    @BeforeEach
    void prepararEntorno() {
        ConfiguracionTienda.reiniciarParaPruebas();
    }

    @Test
    @DisplayName("Debe devolver siempre la misma instancia bajo acceso concurrente")
    void debeGarantizarUnicaInstanciaEnConcurrencia() throws Exception {
        int numHilos = 20;
        CountDownLatch inicioSimultaneo = new CountDownLatch(1);
        ExecutorService pool = Executors.newFixedThreadPool(numHilos);
        Set<ConfiguracionTienda> instanciasObservadas = Collections.synchronizedSet(new HashSet<>());

        try {
            List<Future<ConfiguracionTienda>> tareas = new ArrayList<>();
            for (int i = 0; i < numHilos; i++) {
                tareas.add(pool.submit(() -> {
                    inicioSimultaneo.await();
                    ConfiguracionTienda inst = ConfiguracionTienda.getInstancia();
                    instanciasObservadas.add(inst);
                    return inst;
                }));
            }

            inicioSimultaneo.countDown();

            ConfiguracionTienda esperada = tareas.get(0).get();
            for (Future<ConfiguracionTienda> tarea : tareas) {
                assertSame(esperada, tarea.get(), "Todos los hilos deben recibir exactamente la misma referencia");
            }

            assertEquals(1, instanciasObservadas.size(), "Solo debe existir 1 instancia en el conjunto");
            assertEquals(1, ConfiguracionTienda.INSTANCIAS_CREADAS.get(), "El constructor solo debe ejecutarse una vez");
        } finally {
            pool.shutdown();
        }
    }

    @Test
    @DisplayName("Debe conservar los valores de configuración originales")
    void debeLeerPropiedadesConfiguradas() {
        ConfiguracionTienda config = ConfiguracionTienda.getInstancia();

        assertEquals("MercaSENA", config.getPropiedad("tienda.nombre"));
        assertEquals(19, config.getPorcentajeIva());
        assertEquals("COP", config.getPropiedad("moneda"));
        assertEquals("150000", config.getPropiedad("envio.gratis.desde"));
    }

    @Test
    @DisplayName("Debe lanzar IllegalArgumentException si la propiedad no existe")
    void debeLanzarExcepcionConPropiedadInexistente() {
        ConfiguracionTienda config = ConfiguracionTienda.getInstancia();

        assertThrows(IllegalArgumentException.class, () -> config.getPropiedad("clave.inexistente"));
    }

    @Test
    @DisplayName("Debe devolver la misma instancia en dos llamadas seguidas")
    void devuelveSiempreLaMismaInstancia() {
        ConfiguracionTienda a = ConfiguracionTienda.getInstancia();
        ConfiguracionTienda b = ConfiguracionTienda.getInstancia();
        assertSame(a, b);
    }

    @Test
    @DisplayName("Regla 2: el mapa de getPropiedades() es de solo lectura")
    void getPropiedadesEsDeSoloLectura() {
        Map<String, String> mapa = ConfiguracionTienda.getInstancia().getPropiedades();
        assertNotNull(mapa);

        assertThrows(UnsupportedOperationException.class, () -> mapa.put("moneda", "USD"));
        assertThrows(UnsupportedOperationException.class, () -> mapa.put("nueva.clave", "x"));
        assertThrows(UnsupportedOperationException.class, () -> mapa.remove("tienda.nombre"));
        assertThrows(UnsupportedOperationException.class, mapa::clear);

        // La configuración no cambió.
        assertEquals("COP", ConfiguracionTienda.getInstancia().getPropiedad("moneda"));
        assertEquals("MercaSENA", ConfiguracionTienda.getInstancia().getPropiedad("tienda.nombre"));
    }
}
