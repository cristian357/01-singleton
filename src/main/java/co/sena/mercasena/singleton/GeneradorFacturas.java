package co.sena.mercasena.singleton;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Generador del consecutivo de facturas.
 * Singleton implementado con enum: la JVM garantiza una única instancia.
 */
public enum GeneradorFacturas {
    INSTANCIA;

    private final AtomicLong contador = new AtomicLong(0);

    public String siguienteNumero() {
    long numero = contador.incrementAndGet();
    return String.format("FAC-%06d", numero);
}

    /** Solo para pruebas. */
    void reiniciarParaPruebas() {
        contador.set(0);
    }
}
