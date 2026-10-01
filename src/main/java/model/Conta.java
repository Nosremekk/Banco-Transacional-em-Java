package model;

import java.math.BigDecimal;

public class Conta
    {
    private int id;
    private String titular;
    private BigDecimal saldo;

    public Conta(int id, String titular, BigDecimal saldo)
    {
        this.id = id;
        this.titular = titular;
        this.saldo = saldo;
    }

    public Conta(String titular, BigDecimal saldo)
    {
        this.titular = titular;
        this.saldo = saldo;
    }

    public int getId()
    {
        return id;
    }

    public String getTitular()
    {
        return titular;
    }

    public BigDecimal getSaldo()
    {
        return saldo;
    }

    @Override
    public String toString() {
        return String.format("Conta [ID: %d | Titular: %s | Saldo: R$ %.2f]", id, titular, saldo);
    }
}