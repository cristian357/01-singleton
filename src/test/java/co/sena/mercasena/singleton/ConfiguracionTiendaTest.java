package co.sena.mercasena.singleton;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConfiguracionTiendaTest {

    @BeforeEach
    void limpiar() {
        ConfiguracionTienda.reiniciarParaPruebas();
    }

    @Test
    void devuelveSiempreLaMismaInstancia() {
        ConfiguracionTienda a = ConfiguracionTienda.getInstancia();
        ConfiguracionTienda b = ConfiguracionTienda.getInstancia();
        assertSame(a, b);
    }

    @Test
    void leeElPorcentajeDeIva() {
        assertEquals(19, ConfiguracionTienda.getInstancia().getPorcentajeIva());
    }

    @Test
    void propiedadInexistenteLanzaExcepcion() {
        ConfiguracionTienda config = ConfiguracionTienda.getInstancia();
        assertThrows(IllegalArgumentException.class, () -> config.getPropiedad("no.existe"));
    }
}
