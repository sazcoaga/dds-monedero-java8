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

  public void poner(double cuanto) {
    esValidoDeposito(cuanto);
    new Movimiento(LocalDate.now(), cuanto, true).agregateA(this);
  }

void esValidoDeposito(double monto){
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
  public void sacar(double cuanto) {

   chequearMontoNoNegativo(cuanto);

    if (getSaldo() - cuanto < 0) {
      throw new SaldoMenorException("No puede sacar mas de " + getSaldo() + " $");
    }
    double montoExtraidoHoy = getMontoExtraidoA(LocalDate.now());
    double limite = montoExtraidoMaximo - montoExtraidoHoy;
    if (cuanto > limite) {
      throw new MaximoExtraccionDiarioException("No puede extraer mas de $ " + montoExtraidoMaximo
          + " diarios, límite: " + limite);
    }

    //deberia la cuenta agregar el deposito y no pedirle que se agregue
    new Movimiento(LocalDate.now(), cuanto, false).agregateA(this);
  }

  public void agregarMovimiento(LocalDate fecha, double cuanto, boolean esDeposito) {
    Movimiento movimiento = new Movimiento(fecha, cuanto, esDeposito);
    movimientos.add(movimiento);
  }



  //deberia indicar que es por fecha el nombre
  public double getMontoExtraidoA(LocalDate fecha) {
    return getMovimientos().stream()
        //el movimeinto deberia brindar la informacion del monto extraido -- (feature envy?)
        //existe un metodo "esDeLaFecha" y no se usa
        //existe un metodo "isExtraccion" y no se usa
        .filter(movimiento -> !movimiento.isDeposito() && movimiento.getFecha().equals(fecha))
        .mapToDouble(Movimiento::getMonto)
        .sum();
  }

  public List<Movimiento> getMovimientos() {
    return movimientos;
  }

  public double getSaldo() {
    return saldo;
  }

  //el saldo deberia modificarse a traves de los movimientos no con un setter
  public void setSaldo(double saldo) {
    this.saldo = saldo;
  }

}
