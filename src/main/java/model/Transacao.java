package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transacao {
    private int id;
    private int contaOrigemId;
    private int contaDestinoId;
    private BigDecimal valor;
    private LocalDateTime dataHora;

    public Transacao(int id, int contaOrigemId, int contaDestinoId, BigDecimal valor, LocalDateTime dataHora) {
        this.id = id;
        this.contaOrigemId = contaOrigemId;
        this.contaDestinoId = contaDestinoId;
        this.valor = valor;
        this.dataHora = dataHora;
    }

    public int getId() {
        return id;
    }

    public int getContaOrigemId() {
        return contaOrigemId;
    }

    public int getContaDestinoId() {
        return contaDestinoId;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    @Override
    public String toString() {
        return String.format("Transação [ID: %d | De: %d -> Para: %d | Valor: R$ %.2f | Data: %s]",
                id, contaOrigemId, contaDestinoId, valor, dataHora);
    }
}