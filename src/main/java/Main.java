import model.Conta;
import model.Transacao;
import repository.ConexaoFactory;
import repository.ContaRepository;
import service.TransferenciaService;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.List;

public class Main
{
    public static void main(String[] args)
    {
        ConexaoFactory.inicializarBanco();

        TransferenciaService service = new TransferenciaService();
        int aliceId = 0;
        int bobId = 0;

        try (Connection conn = ConexaoFactory.getConnection())
        {
            ContaRepository repo = new ContaRepository(conn);
            aliceId = repo.criarConta("Alice", new BigDecimal("100.00"));
            bobId = repo.criarConta("Bob", new BigDecimal("50.00"));

            System.out.println("=== ESTADO INICIAL ===");
            System.out.println(repo.buscarPorId(aliceId));
            System.out.println(repo.buscarPorId(bobId));
            System.out.println();
        }
        catch (Exception e)
        {
            System.err.println("Erro ao inicializar dados: " + e.getMessage());
            return;
        }

        try
        {
            System.out.println("=== CENÁRIO 1: TRANSFERÊNCIA BEM-SUCEDIDA (R$ 30,00) ===");
            service.transferir(aliceId, bobId, new BigDecimal("30.00"));
            System.out.println("Transferência concluída com sucesso.");
        }
        catch (Exception e)
        {
            System.err.println(e.getMessage());
        }

        exibirSaldosEExtrato(aliceId, bobId);

        try
        {
            System.out.println("=== CENÁRIO 2: FALHA / ROLLBACK (TENTATIVA DE R$ 200,00) ===");
            service.transferir(aliceId, bobId, new BigDecimal("200.00"));
        }
        catch (Exception e)
        {
            System.out.println("Exceção capturada conforme esperado: " + e.getMessage());
        }

        exibirSaldosEExtrato(aliceId, bobId);
    }

    private static void exibirSaldosEExtrato(int aliceId, int bobId)
    {
        try (Connection conn = ConexaoFactory.getConnection())
        {
            ContaRepository repo = new ContaRepository(conn);

            System.out.println("\n--- Saldos Atuais ---");
            System.out.println(repo.buscarPorId(aliceId));
            System.out.println(repo.buscarPorId(bobId));

            System.out.println("\n--- Extrato Alice ---");
            List<Transacao> extratoAlice = repo.listarExtrato(aliceId);
            for (Transacao t : extratoAlice)
            {
                System.out.println(t);
            }
            System.out.println("--------------------------------------------------\n");
        }
        catch (Exception e)
        {
            System.err.println("Erro ao consultar estado: " + e.getMessage());
        }
    }
}