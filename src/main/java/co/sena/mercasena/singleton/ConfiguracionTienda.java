package co.sena.mercasena.singleton;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Configuración global de la tienda MercaSENA.
 * Solo debe existir una instancia (patrón Singleton con inicialización perezosa).
 */
public final class ConfiguracionTienda {

    /** Cuenta cuántas veces se ejecutó el constructor (diagnóstico y pruebas). */
    static final AtomicInteger INSTANCIAS_CREADAS = new AtomicInteger();

    private static volatile ConfiguracionTienda instancia;

    private final Map<String, String> propiedades;

    private ConfiguracionTienda() {
        INSTANCIAS_CREADAS.incrementAndGet();
        // Regla 2: el mapa que sale de esta clase es de solo lectura.
        this.propiedades = Collections.unmodifiableMap(cargarDesdeArchivo());
    }

    public static ConfiguracionTienda getInstancia() {
        // Regla 1: Double-Checked Locking. La lectura sin bloqueo evita el costo
        // de sincronizar cuando la instancia ya existe; la segunda comprobación
        // dentro del bloque garantiza que solo un hilo la construya.
        ConfiguracionTienda resultado = instancia;
        if (resultado == null) {
            synchronized (ConfiguracionTienda.class) {
                resultado = instancia;
                if (resultado == null) {
                    instancia = resultado = new ConfiguracionTienda();
                }
            }
        }
        return resultado;
    }

    public String getPropiedad(String clave) {
        String valor = propiedades.get(clave);
        if (valor == null) {
            throw new IllegalArgumentException("Propiedad no configurada: " + clave);
        }
        return valor;
    }

    public int getPorcentajeIva() {
        return Integer.parseInt(getPropiedad("iva.porcentaje"));
    }

    public Map<String, String> getPropiedades() {
        return propiedades;
    }

    /** Simula la lectura lenta de un archivo de configuración. */
    private static Map<String, String> cargarDesdeArchivo() {
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        Map<String, String> datos = new HashMap<>();
        datos.put("tienda.nombre", "MercaSENA");
        datos.put("iva.porcentaje", "19");
        datos.put("moneda", "COP");
        datos.put("envio.gratis.desde", "150000");
        return datos;
    }

    /** Solo para pruebas: olvida la instancia actual. */
    static void reiniciarParaPruebas() {
        synchronized (ConfiguracionTienda.class) {
            instancia = null;
            INSTANCIAS_CREADAS.set(0);
        }
    }
}
