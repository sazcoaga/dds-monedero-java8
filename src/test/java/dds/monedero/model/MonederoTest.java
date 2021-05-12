package dds.monedero.model;

import dds.monedero.exceptions.MaximaCantidadDepositosException;
import dds.monedero.exceptions.MaximoExtraccionDiarioException;
import dds.monedero.exceptions.MontoNegativoException;
import dds.monedero.exceptions.SaldoMenorException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MonederoTest {
  private Cuenta cuenta;

  @BeforeEach
  void init() {
    cuenta = new Cuenta();
  }

  @Test
  void DepositarUnMontoValido() {

     cuenta.depositar(1500);
    Assertions.assertEquals(cuenta.getSaldo(), 1500);
  }

  @Test
  void DepositarMontoNegativo() {
    assertThrows(MontoNegativoException.class, () -> cuenta.depositar(-1500));
  }

  @Test
  void TresDepositos() {
    cuenta.depositar(1500);
    cuenta.depositar(456);
    cuenta.depositar(1900);

    assertEquals(cuenta.getSaldo(), 3856);

  }

  @Test
  void MasDeTresDepositosEnUnDia() {
    assertThrows(MaximaCantidadDepositosException.class, () -> {
          cuenta.depositar(1500);
          cuenta.depositar(456);
          cuenta.depositar(1900);
          cuenta.depositar(245);
    });
  }

  @Test
  void ExtraerMasQueElSaldo() {
    assertThrows(SaldoMenorException.class, () -> {
          cuenta.depositar(90);
          cuenta.extraer(1001);
    });
  }

  @Test
  public void ExtraerMasDe1000() {
    assertThrows(MaximoExtraccionDiarioException.class, () -> {
      cuenta.depositar(5000);
      cuenta.extraer(1001);
    });
  }

  @Test
  public void ExtraerMontoNegativo() {
    assertThrows(MontoNegativoException.class, () -> cuenta.extraer(-500));
  }

  @Test
  public void CalculoMontoExtraidoEnUnDia(){

    cuenta.agregarMovimiento(LocalDate.of(2021, 5, 10), 40, false  );
    cuenta.agregarMovimiento(LocalDate.of(2021, 5, 10), 500, false);
    cuenta.agregarMovimiento(LocalDate.of(2021, 5, 8), 600, false);
    assertEquals(cuenta.getMontoExtraidoA(LocalDate.of(2021, 5, 10)), 540);
    assertEquals(cuenta.getMontoExtraidoA(LocalDate.of(2021, 5, 8)), 600);
  }


}