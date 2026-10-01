package service;

import repository.ConexaoFactory;
import repository.ContaRepository;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;

public class TransferenciaService
{
    public void transferir(int origemId, int destinoId, BigDecimal valor) throws SQLException
    {
        if (origemId == destinoId)
        {
            throw new IllegalArgumentException("A conta de origem e destino não podem ser iguais.");
        }

        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0)
        {
            throw new IllegalArgumentException("O valor da transferência deve ser estritamente positivo.");
        }

        try (Connection conn = ConexaoFactory.getConnection())
        {
            conn.setAutoCommit(false);

            try
            {
                ContaRepository repo = new ContaRepository(conn);

                repo.debitar(origemId, valor);
                repo.creditar(destinoId, valor);
                repo.registrarTransacao(origemId, destinoId, valor);

                conn.commit();
            }
            catch (Exception e)
            {
                conn.rollback();
                throw new RuntimeException("Transação abortada. Rollback executado: " + e.getMessage(), e);
            }
        }
    }
}