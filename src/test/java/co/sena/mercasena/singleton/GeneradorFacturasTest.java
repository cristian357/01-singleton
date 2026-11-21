package co.sena.mercasena.singleton;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GeneradorFacturasTest {

    @BeforeEach
    void limpiar() {
        GeneradorFacturas.INSTANCIA.reiniciarParaPruebas();
    }

    @Test
    void laPrimeraFacturaEsLaNumeroUno() {
        assertEquals("FAC-000001", GeneradorFacturas.INSTANCIA.siguienteNumero());
    }

    @Test
    void losNumerosSonConsecutivos() {
        String primero = GeneradorFacturas.INSTANCIA.siguienteNumero();
        String segundo = GeneradorFacturas.INSTANCIA.siguienteNumero();
        assertNotEquals(primero, segundo);
    }
}
