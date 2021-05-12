package dds.monedero.model;

import dds.monedero.exceptions.MaximaCantidadDepositosException;
import dds.monedero.exceptions.MaximoExtraccionDiarioException;
import dds.monedero.exceptions.MontoNegativoException;
import dds.monedero.exceptions.SaldoMenorException;

import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
import java.util.ArrayList;
import java.util.List;

public class Cuenta {

  private double saldo;
  private int depositosMaximos = 3;
  private double montoExtraidoMaximo = 1000;


  private List<Movimiento> movimientos = new ArrayList<>();


  public Cuenta() {
    saldo = 0;
  }

  public Cuenta(double montoInicial) {
    saldo = montoInicial;
  }

  public void setMovimientos(List<Movimiento> movimientos) {
    this.movimientos = movimientos;
  }

  public void depositar(double montoDeposito) {
    validarDeposito(montoDeposito);
    saldo = saldo + montoDeposito;
    agregarMovimiento(LocalDate.now(), montoDeposito, true);
  }

void validarDeposito(double monto){
    chequearMontoNoNegativo(monto);
    chequearCantidadMaximaDepositos();
}

void chequearCantidadMaximaDepositos(){
  if (getMovimientos().stream().filter(movimiento -> movimiento.fueDepositado(LocalDate.now())).count() >= depositosMaximos) {
    throw new MaximaCantidadDepositosException("Ya excedio los " + depositosMaximos + " depositos diarios");
  }
}

  void chequearMontoNoNegativo(double monto){
    if (monto <= 0) {
      throw new MontoNegativoException(monto + ": el monto a ingresar debe ser un valor positivo");
    }
  }

  public void extraer(double montoExtraccion) {

   validarExtraccion(montoExtraccion);
   saldo = saldo - montoExtraccion;
    agregarMovimiento(LocalDate.now(), montoExtraccion, false);
  }

  void validarExtraccion(double monto){
    chequearMontoNoNegativo(monto);
    validarMontoMaximoExtraccion(monto);
  }

  void validarMontoMaximoExtraccion(double monto){
    if (getSaldo() - monto < 0) {
      throw new SaldoMenorException("No puede sacar mas de " + getSaldo() + " $");
    }
    double montoExtraidoHoy = getMontoExtraidoA(LocalDate.now());
    double limite = montoExtraidoMaximo - montoExtraidoHoy;
    if (monto > limite) {
      throw new MaximoExtraccionDiarioException("No puede extraer mas de $ " + montoExtraidoMaximo
          + " diarios, límite: " + limite);
    }
  }

  public void agregarMovimiento(LocalDate fecha, double cuanto, boolean esDeposito) {
    Movimiento movimiento = new Movimiento(fecha, cuanto, esDeposito);
    movimientos.add(movimiento);
  }


  public double getMontoExtraidoA(LocalDate fecha) {
    return getMovimientos().stream()
        .filter(movimiento -> movimiento.fueExtraido(fecha))
        .mapToDouble(Movimiento::getMonto)
        .sum();
  }

  public List<Movimiento> getMovimientos() {
    return movimientos;
  }

  public double getSaldo() {
    return saldo;
  }



}
