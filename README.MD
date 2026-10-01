Mini Sistema Transacional com JDBC Puro e SQLite

Backend focado em consistencia de saldo, controle de concorrencia e integridade transacional, simulando o nucleo de uma carteira digital. O projeto dispensa intencionalmente frameworks como Spring Boot e ORMs para expor de forma explicita o controle de conexoes, ciclos de transacao e execucao de SQL nativo.

Decisoes de Design e Engenharia

    Controle Transacional Explicito (ACID): Desativacao do autoCommit com conn.setAutoCommit(false) na camada de servico para garantir atomicidade. As operacoes de debito, credito e escrita do extrato ocorrem dentro de uma unica fronteira transacional, disparando conn.rollback() imediato caso ocorra qualquer inconsistencia ou excecao.

    Prevencao de Race Conditions e Saldo Negativo: A checagem de saldo nao depende exclusivamente da memoria da aplicacao. A instrucao de debito utiliza clausula relacional atomica (UPDATE contas SET saldo = saldo - ? WHERE id = ? AND saldo >= ?), combinada com a restricao de integridade CHECK (saldo >= 0) e chaves estrangeiras ativas (PRAGMA foreign_keys = ON) no SQLite.

    Precisao Numerica Financeira: Todos os valores monetarios sao manipulados estritamente via BigDecimal para evitar perda de precisao e erros de arredondamento inerentes ao padrao IEEE 754 de tipos flutuantes (double ou float).

    Padrao DAO / Repository e Separacao de Camadas: Isolamento total entre as entidades de dominio, regras de negocio (service) e instrucoes SQL preparadas contra injecao (PreparedStatement no repository), compartilhando a mesma instancia de Connection durante a transacao.

Como Executar

Pre-requisitos:

    Java JDK 17 ou superior

    Maven

Compilacao e Execucao via Terminal:

mvn clean compile

mvn exec:java -Dexec.mainClass="Main"