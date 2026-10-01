package repository;

import model.Conta;
import model.Transacao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ContaRepository
{
    private final Connection conn;

    public ContaRepository(Connection conn)
    {
        this.conn = conn;
    }

    public int criarConta(String titular, BigDecimal saldoInicial) throws SQLException
    {
        String sql = "INSERT INTO contas (titular, saldo) VALUES (?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS))
        {
            stmt.setString(1, titular);
            stmt.setBigDecimal(2, saldoInicial);
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys())
            {
                if (rs.next())
                {
                    return rs.getInt(1);
                }
            }
        }
        throw new SQLException("Falha ao obter o ID da nova conta.");
    }

    public Conta buscarPorId(int id) throws SQLException
    {
        String sql = "SELECT id, titular, saldo FROM contas WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery())
            {
                if (rs.next())
                {
                    return new Conta(
                            rs.getInt("id"),
                            rs.getString("titular"),
                            rs.getBigDecimal("saldo")
                    );
                }
            }
        }
        return null;
    }

    public void debitar(int id, BigDecimal valor) throws SQLException
    {
        String sql = "UPDATE contas SET saldo = saldo - ? WHERE id = ? AND saldo >= ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setBigDecimal(1, valor);
            stmt.setInt(2, id);
            stmt.setBigDecimal(3, valor);

            int linhasAfetadas = stmt.executeUpdate();
            if (linhasAfetadas == 0)
            {
                throw new SQLException("Saldo insuficiente ou conta de origem inexistente.");
            }
        }
    }

    public void creditar(int id, BigDecimal valor) throws SQLException
    {
        String sql = "UPDATE contas SET saldo = saldo + ? WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setBigDecimal(1, valor);
            stmt.setInt(2, id);

            int linhasAfetadas = stmt.executeUpdate();
            if (linhasAfetadas == 0)
            {
                throw new SQLException("Conta de destino inexistente.");
            }
        }
    }

    public void registrarTransacao(int origemId, int destinoId, BigDecimal valor) throws SQLException
    {
        String sql = "INSERT INTO transacoes (conta_origem_id, conta_destino_id, valor) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setInt(1, origemId);
            stmt.setInt(2, destinoId);
            stmt.setBigDecimal(3, valor);
            stmt.executeUpdate();
        }
    }

    public List<Transacao> listarExtrato(int contaId) throws SQLException
    {
        List<Transacao> transacoes = new ArrayList<>();
        String sql = "SELECT id, conta_origem_id, conta_destino_id, valor, data_hora FROM transacoes WHERE conta_origem_id = ? OR conta_destino_id = ? ORDER BY data_hora ASC";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        try (PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setInt(1, contaId);
            stmt.setInt(2, contaId);

            try (ResultSet rs = stmt.executeQuery())
            {
                while (rs.next())
                {
                    String dataTexto = rs.getString("data_hora");
                    LocalDateTime dataHora = LocalDateTime.parse(dataTexto, formatter);

                    Transacao transacao = new Transacao(
                            rs.getInt("id"),
                            rs.getInt("conta_origem_id"),
                            rs.getInt("conta_destino_id"),
                            rs.getBigDecimal("valor"),
                            dataHora
                    );
                    transacoes.add(transacao);
                }
            }
        }
        return transacoes;
    }
}