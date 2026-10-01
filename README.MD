# Mini Sistema Transacional com JDBC Puro e SQLite

Backend focado em consistência de saldo, controle de concorrência e integridade transacional, simulando o núcleo de uma carteira digital. O projeto dispensa intencionalmente frameworks como Spring Boot e ORMs para expor de forma explícita o controle de conexões, ciclos de transação e execução de SQL nativo.

## Decisões de Design e Engenharia

- **Controle Transacional Explícito (ACID):** Desativação do autoCommit com `conn.setAutoCommit(false)` na camada de serviço para garantir atomicidade. As operações de débito, crédito e escrita do extrato ocorrem dentro de uma única fronteira transacional, disparando `conn.rollback()` imediato caso ocorra qualquer inconsistência ou exceção.
- **Prevenção de Race Conditions e Saldo Negativo:** A checagem de saldo não depende exclusivamente da memória da aplicação. A instrução de débito utiliza cláusula relacional atômica (`UPDATE contas SET saldo = saldo - ? WHERE id = ? AND saldo >= ?`), combinada com a restrição de integridade `CHECK (saldo >= 0)` e chaves estrangeiras ativas (`PRAGMA foreign_keys = ON;`) no SQLite.
- **Precisão Numérica Financeira:** Todos os valores monetários são manipulados estritamente via `BigDecimal` para evitar perda de precisão e erros de arredondamento inerentes ao padrão IEEE 754 de tipos flutuantes (`double` ou `float`).
- **Padrão DAO / Repository e Separação de Camadas:** Isolamento total entre as entidades de domínio, regras de negócio (`service`) e instruções SQL preparadas contra injeção (`PreparedStatement` no `repository`), compartilhando a mesma instância de `Connection` durante a transação.

## Como Executar

### Pré-requisitos
- Java JDK 17 ou superior
- Maven

### Compilação e Execução via Terminal

```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="Main"
